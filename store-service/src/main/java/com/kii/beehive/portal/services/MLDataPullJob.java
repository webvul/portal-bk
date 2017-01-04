package com.kii.beehive.portal.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.beehive.portal.service.MLTaskDetailDao;
import com.kii.extension.ruleengine.schedule.JobInSpring;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

@Component
public class MLDataPullJob implements JobInSpring {
	
	
	private Logger log= LoggerFactory.getLogger(MLDataPullJob.class);
	
	
	@Autowired
	private HttpClient http;
	
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MLTaskDetailDao mlTaskDao;
	
	
	@Autowired
	private TriggerManager triggerOper;
	
	
	
	
	private String mlServiceUrl;
	
	
	@Override
	public void execute(JobDataMap paramMap) {
		
		String taskID=paramMap.getString("mlTaskID");
		
		doTask(taskID);
		
	}
	
	
	private  void  doTask(String taskID){
		
		
		HttpUriRequest request=new HttpGet("http://localhost:?taskID="+taskID);
//
//			String response=http.executeRequest(request);
		
		int seed= (int) (System.currentTimeMillis()%10  - 5);
		Map<String,Object> demo=new HashMap<>();
		
		demo.put("foo",3*seed);
		demo.put("bar",-7*seed);
		
		try {
			
			
			String response=mapper.writeValueAsString(demo);
			
			Map<String,Object> map=mapper.readValue(response, Map.class);
			
			mlTaskDao.updateOutput(map,taskID);
			
			BusinessDataObject obj=new BusinessDataObject(taskID,"mlOutput", BusinessObjType.Context);
			obj.setData(map);
			
			triggerOper.addBusinessData(obj);
			
			
		} catch (Exception e) {
			log.warn("get ML data fail:task id"+taskID,e.getMessage());
		}
		
	}
}
