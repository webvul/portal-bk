package com.kii.beehive.portal.services;


import static com.kii.beehive.business.schedule.ProxyJob.APPLICATION_CTX;
import static com.kii.beehive.business.schedule.ProxyJob.BEAN_CLASS;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.ruleengine.EngineTriggerBuilder;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.business.schedule.ProxyJob;
import com.kii.beehive.portal.exception.BusinessException;
import com.kii.beehive.portal.service.MLTaskDetailDao;
import com.kii.beehive.portal.store.entity.MLTaskDetail;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.BusinessObjType;

@Component
public class MLTaskService {
	
	
	private static final JobKey jobKey = JobKey.jobKey("mlPullJob", "mlDataPull");
	
	private Logger log= LoggerFactory.getLogger(MLTaskService.class);
	@Autowired
	private MLDataPullJob job;
	@Autowired
	private MLTaskDetailDao mlTaskDao;
	@Autowired
	private TriggerManager triggerOper;
	@Autowired
	private Scheduler scheduler;
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private EngineTriggerBuilder builder;
	
	private TriggerKey  getTriggerKey(String mlTaskID){
		
		return TriggerKey.triggerKey(mlTaskID,"mlDataPull");
	}
	
	@PostConstruct
	public void initMLTaskUpdate(){
		
		JobDataMap dataMap=new JobDataMap();
		dataMap.put(APPLICATION_CTX,applicationContext);
		dataMap.put(BEAN_CLASS,job.getClass());
		
		JobDetail jobDetail = JobBuilder.newJob()
				.withIdentity(jobKey)
				.setJobData(dataMap)
				.storeDurably(true)
				.ofType(ProxyJob.class).build();
		
		try {
			scheduler.addJob(jobDetail,true);
		} catch (SchedulerException e) {
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
		
		List<MLTaskDetail> allTask=mlTaskDao.getAllExistsEntity();
		
		final Date now=new Date(System.currentTimeMillis()+1000*60);
		
		allTask.forEach((detail)->{
			
			Map<String,Object> currVal=detail.getMlOutput();
			if(currVal.isEmpty()){
				currVal.put("_enable",false);
			}else {
				currVal.put("_enable", detail.getStatus() == PortalEntity.EntityStatus.enable);
			}
			BusinessDataObject obj = new BusinessDataObject(detail.getMlTaskID(), builder.getMlTaskName(), BusinessObjType.Context);
			obj.setData(currVal);
			
			triggerOper.addBusinessData(obj);
			
			
			SimpleTrigger interval = getSimpleTrigger(detail,now);
			
			try {
				scheduler.scheduleJob(interval);
				
				if(detail.getStatus()== PortalEntity.EntityStatus.disable){
					scheduler.pauseTrigger(interval.getKey());
				}
			} catch (SchedulerException e) {
				log.error(e.getMessage());
				mlTaskDao.disableEntity(detail.getMlTaskID());
			}
			
		});
		
	}
	
	private SimpleTrigger getSimpleTrigger(MLTaskDetail detail,Date now) {
		
		TriggerBuilder<SimpleTrigger> builder= TriggerBuilder.newTrigger()
						.withIdentity(getTriggerKey(detail.getMlTaskID()))
						.forJob(jobKey)
						.usingJobData(MLDataPullJob.ML_TASK_ID,detail.getMlTaskID())
						.usingJobData(MLDataPullJob.ML_TASK_URL,detail.getAccessUrl())
						.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(detail.getInterval()));
		if(now!=null){
			builder.startAt(now);
		}
		return builder.build();
	}
	
	public MLTaskDetail getTaskDetailByID(String taskID){
		return mlTaskDao.getObjectByID(taskID);
	}
	
	public void setEnable(String taskID){

		mlTaskDao.enableEntity(taskID);
		
		try {
			scheduler.resumeTrigger(getTriggerKey(taskID));
		} catch (SchedulerException e) {
			throw new BusinessException(e);
		}
		
		updateTriggerData(taskID, true);
		
	}
	
	private void updateTriggerData(String taskID,boolean sign) {
		BusinessDataObject obj = new BusinessDataObject(taskID, builder.getMlTaskName(), BusinessObjType.Context);
		obj.setData(Collections.singletonMap("_enable",sign));
		
		triggerOper.addBusinessData(obj);
	}
	
	
	public void setDisable(String taskID){
	
		mlTaskDao.disableEntity(taskID);
		
		try {
			scheduler.pauseTrigger(getTriggerKey(taskID));
		} catch (SchedulerException e) {
			throw new BusinessException(e);
		}
		
		updateTriggerData(taskID, false);
	}
	
	
	public void updateTask(String taskID,MLTaskDetail detail){
		
		mlTaskDao.updateEntityAll(detail,taskID);
		
		SimpleTrigger interval = getSimpleTrigger(detail,null);
		
		try {
			scheduler.rescheduleJob(getTriggerKey(taskID),interval);
		} catch (SchedulerException e) {
			throw new BusinessException(e);
		}
	}
	
	public void removeTask(String taskID){
		
		mlTaskDao.deleteEntity(taskID);
		
		try {
			scheduler.unscheduleJob(getTriggerKey(taskID));
		} catch (SchedulerException e) {
			throw new BusinessException(e);
		}
		updateTriggerData(taskID, false);
		
	}
	
	public List<MLTaskDetail> getAll(){
		
		return mlTaskDao.getAllEnableEntity();
	}
}
