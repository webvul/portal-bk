package com.kii.beehive.portal.services;

import java.util.Date;
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
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.beehive.portal.service.MLTaskDetailDao;
import com.kii.beehive.portal.store.entity.MLTaskErrorInfo;
import com.kii.extension.ruleengine.schedule.JobInSpring;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;

@Component
public class MLDataPullJob implements JobInSpring {
	
	
	public  static final String ML_TASK_ID = "mlTaskID";
	public  static final String ML_TASK_URL="mlTaskUrl";
	
	private Logger log= LoggerFactory.getLogger(MLDataPullJob.class);
	
	
	@Autowired
	private HttpClient http;
	
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MLTaskDetailDao mlTaskDao;
	
	
	@Autowired
	private TriggerManager triggerOper;
	
	
	@Override
	public void execute(JobDataMap paramMap) {
		
		String taskID=paramMap.getString(ML_TASK_ID);
		
		String url=paramMap.getString(ML_TASK_URL);
		doTask(taskID,url);
		
	}
	
	
	private  void  doTask(String taskID,String url){
		
		String fullUrl= StrTemplate.generUrl(url,taskID);
		HttpUriRequest request=new HttpGet(fullUrl);

		try {
			
			String response=http.executeRequest(request);
			
			Map<String,Object> map=mapper.readValue(response, Map.class);
			
			mlTaskDao.updateOutput(map,taskID);
			
			BusinessDataObject obj=new BusinessDataObject(taskID,"mlOutput", BusinessObjType.Context);
			obj.setData(map);
			
			triggerOper.addBusinessData(obj);
			
			
		} catch (Exception e) {
			log.warn("get ML data fail:task id"+taskID,e.getMessage());
			
			Throwable exception=e.getCause();
			if(exception==null){
				exception=e;
			}
			MLTaskErrorInfo error=new MLTaskErrorInfo();
			error.setTime(new Date());
			error.setErrorMessage(exception.getMessage());
			error.setErrorCode(exception.getClass().getName());
			
			mlTaskDao.updateErrorInfo(error,taskID);
		}
		
	}
}
