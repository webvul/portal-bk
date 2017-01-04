package com.kii.beehive.portal.services;


import static com.kii.extension.ruleengine.schedule.ProxyJob.APPLICATION_CTX;
import static com.kii.extension.ruleengine.schedule.ProxyJob.BEAN_CLASS;

import javax.annotation.PostConstruct;

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

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.service.MLTaskDetailDao;
import com.kii.beehive.portal.store.entity.MLTaskDetail;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.extension.ruleengine.schedule.ProxyJob;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

@Component
public class MLTaskService {
	
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

	private TriggerKey  getTriggerKey(String mlTaskID){
		
		return TriggerKey.triggerKey(mlTaskID,"mlDataPull");
	}
	
	private static final JobKey jobKey=JobKey.jobKey("mlPullJob","mlDataPull");
	
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
		}
		
		List<MLTaskDetail> allTask=mlTaskDao.getAllExistsEntity();
		
		
		allTask.forEach((detail)->{
			
			Map<String,Object> currVal=detail.getMlOutput();
			currVal.put("_enable",detail.getStatus()== PortalEntity.EntityStatus.enable);
			
			BusinessDataObject obj=new BusinessDataObject(detail.getMlTaskID(),"mlOutput", BusinessObjType.Context);
			obj.setData(currVal);
			
			triggerOper.addBusinessData(obj);
			
			if(detail.getStatus()== PortalEntity.EntityStatus.disable){
				return;
			}
			
			SimpleTrigger interval = getSimpleTrigger(detail);
			
			try {
				scheduler.scheduleJob(interval);
			} catch (SchedulerException e) {
				log.error(e.getMessage());
			}
			
		});
		
	}
	
	private SimpleTrigger getSimpleTrigger(MLTaskDetail detail) {
		return TriggerBuilder.newTrigger()
						.withIdentity(getTriggerKey(detail.getMlTaskID()))
						.forJob(jobKey)
						.usingJobData("mlTaskID",detail.getMlTaskID())
						.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(detail.getInterval()))
						.build();
	}
	
	public MLTaskDetail getTaskDetailByID(String taskID){
		return mlTaskDao.getObjectByID(taskID);
	}
	
	public void setEnable(String taskID){

		mlTaskDao.enableEntity(taskID);
		
		try {
			scheduler.resumeTrigger(getTriggerKey(taskID));
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
		
		
	}
	
	
	public void setdisable(String taskID){
	
		mlTaskDao.disableEntity(taskID);
		
		try {
			scheduler.pauseTrigger(getTriggerKey(taskID));
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}
	
	
	public void updateTask(String taskID,MLTaskDetail detail){
		
		mlTaskDao.updateEntityAll(detail,taskID);
		
		SimpleTrigger interval = getSimpleTrigger(detail);
		
		try {
			scheduler.rescheduleJob(getTriggerKey(taskID),interval);
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
	}
	
	public void removeTask(String taskID){
		
		mlTaskDao.deleteEntity(taskID);
		
		try {
			scheduler.unscheduleJob(getTriggerKey(taskID));
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
		
	}
	
	public List<MLTaskDetail> getAll(){
		
		return mlTaskDao.getAllEnableEntity();
	}
}
