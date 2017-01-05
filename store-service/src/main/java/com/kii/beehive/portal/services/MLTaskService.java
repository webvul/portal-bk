package com.kii.beehive.portal.services;


import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.beehive.portal.service.MLTaskDetailDao;
import com.kii.beehive.portal.store.entity.MLTaskDetail;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

@Component
public class MLTaskService {
	
	private Logger log= LoggerFactory.getLogger(MLTaskService.class);
	
	
	private ScheduledExecutorService schedule= Executors.newScheduledThreadPool(10);
	
	@Autowired
	private HttpClient http;
	
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MLTaskDetailDao mlTaskDao;
	
	
	@Autowired
	private TriggerManager triggerOper;
	
	
	private String mlServiceUrl;
	
	
	private Map<String,ScheduledFuture> futureMap=new ConcurrentHashMap<>();

	
	
	private  void  doTask(MLTaskDetail detail){
		
		
			HttpUriRequest request=new HttpGet("http://localhost:?taskID="+detail.getMlTaskID());
//
//			String response=http.executeRequest(request);
			
			int seed= (int) (System.currentTimeMillis()%10  - 5);
			Map<String,Object> demo=new HashMap<>();
			
			demo.put("foo",3*seed);
			demo.put("bar",-7*seed);
			
			try {
				
				
				String response=mapper.writeValueAsString(demo);
				
				Map<String,Object> map=mapper.readValue(response, Map.class);
				
				mlTaskDao.updateOutput(map,detail.getMlTaskID());
				
				BusinessDataObject obj=new BusinessDataObject(detail.getMlTaskID(),"mlOutput", BusinessObjType.Context);
				obj.setData(map);
				
				triggerOper.addBusinessData(obj);
				
				
			} catch (Exception e) {
				log.warn("get ML data fail:task id"+detail.getMlTaskID(),e.getMessage());
			}
		
	}
	
	@PostConstruct
	public void initMLTaskUpdate(){
		
		
		List<MLTaskDetail> allTask=mlTaskDao.getAllEntity();
		
		
		allTask.forEach((detail)->{
			
			ScheduledFuture  future=schedule.scheduleAtFixedRate(() -> this.doTask(detail), 1000*10,detail.getInterval(), TimeUnit.MINUTES);
			
			futureMap.put(detail.getMlTaskID(),future);
			
		});
		
	}
	
	public MLTaskDetail getTaskDetailByID(String taskID){
		return mlTaskDao.getObjectByID(taskID);
	}
	
	public void setEnable(String taskID){
		
		
		MLTaskDetail detail=mlTaskDao.enableEntity(taskID);
		
		ScheduledFuture  future=schedule.scheduleAtFixedRate(() -> this.doTask(detail), 1000*10,detail.getInterval(), TimeUnit.MINUTES);
		
		futureMap.put(detail.getMlTaskID(),future);
		
	}
	
	
	public void setdisable(String taskID){
	
		MLTaskDetail detail=mlTaskDao.disableEntity(taskID);
		
		futureMap.get(taskID).cancel(false);
	}
	
	
	public void updateTask(String taskID,MLTaskDetail detail){
		
		mlTaskDao.updateEntityAll(detail,taskID);
		
		futureMap.get(taskID).cancel(false);
		ScheduledFuture  future=schedule.scheduleAtFixedRate(() -> this.doTask(detail), 1000*10,detail.getInterval(), TimeUnit.MINUTES);
		
		futureMap.put(detail.getMlTaskID(),future);
		
		
	}
	
	public void removeTask(String taskID){
		
		mlTaskDao.deleteEntity(taskID);
		
		futureMap.get(taskID).cancel(false);
		
	}
	
	public List<MLTaskDetail> getAll(){
		
		return mlTaskDao.getAllEntity();
	}
}
