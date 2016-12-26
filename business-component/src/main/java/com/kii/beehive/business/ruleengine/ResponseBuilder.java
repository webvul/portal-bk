package com.kii.beehive.business.ruleengine;


import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.store.trigger.task.BusinessFunResponse;
import com.kii.extension.ruleengine.store.trigger.task.CommandResponse;
import com.kii.extension.ruleengine.store.trigger.task.HttpCallResponse;
import com.kii.extension.ruleengine.store.trigger.task.SettingParameterResponse;
import com.kii.extension.ruleengine.store.trigger.task.TriggerResult;

@Component
public class ResponseBuilder {
	
	
	public SettingParameterResponse getSettingParamResponse(ExecuteParam param){
		
		SettingParameterResponse resp=new SettingParameterResponse();
		fillResult(resp,param);
		resp.setInputParam(param.getBusinessParams());
		
		return resp;
		
	}
	

	public BusinessFunResponse getBusinessFunResponse(ExecuteParam param){
		
		BusinessFunResponse resp=new BusinessFunResponse();
		fillResult(resp,param);
		
		return resp;
		
	}
	
	public CommandResponse getCmdResponse(ExecuteParam param){

		CommandResponse resp = new CommandResponse();

		fillResult(resp,param);

		return resp;
	}


	public HttpCallResponse getHttpResponse(String triggerID,ExecuteParam param){

		HttpCallResponse resp = new HttpCallResponse();

		resp.setTriggerID(triggerID);
		fillResult(resp,param);

		return resp;
	}



	private void fillResult(TriggerResult  result,ExecuteParam  params){

		result.setFireSource(params.getFireSource());
		result.setRelationThing(params.getRelationThing());
		result.setFireTime(params.getFireTime());
		result.setBusinessParam(params.getBusinessParams());

		result.setTriggerID(params.getTriggerID());

		return;

	}

}
