package com.kii.beehive.business.ruleengine;


import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.store.trigger.target.BusinessFunResponse;
import com.kii.extension.ruleengine.store.trigger.target.CommandResponse;
import com.kii.extension.ruleengine.store.trigger.target.HttpCallResponse;
import com.kii.extension.ruleengine.store.trigger.target.TriggerResult;

@Component
public class ResponseBuilder {



	public BusinessFunResponse getBusinessFunResponse(String triggerID, ExecuteParam param){
		
		BusinessFunResponse resp=new BusinessFunResponse();
		fillResult(resp,param);
		
		return resp;
		
	}
	
	public CommandResponse getCmdResponse(String triggerID,ExecuteParam param){

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