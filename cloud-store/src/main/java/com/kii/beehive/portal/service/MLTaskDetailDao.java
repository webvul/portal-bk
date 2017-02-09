package com.kii.beehive.portal.service;


import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.MLTaskDetail;
import com.kii.beehive.portal.store.entity.MLTaskErrorInfo;
import com.kii.extension.sdk.entity.BucketInfo;

@Component
public class MLTaskDetailDao extends BaseKiicloudDao<MLTaskDetail>{
	
	
	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("machineLearnTask");
	}
	
	
	public void addMlTask(MLTaskDetail detail){
		
		super.addEntity(detail,detail.getMlTaskID());
	}
	
	public void updateOutput(Map<String,Object> values,String mlTaskID){
		
		super.updateEntity(Collections.singletonMap("mlOutput",values),mlTaskID);
	}
	
	public void updateErrorInfo(MLTaskErrorInfo  errorInfo,String mlTaskID){
		super.updateEntity(Collections.singletonMap("lastError",errorInfo),mlTaskID);
	}
	
}
