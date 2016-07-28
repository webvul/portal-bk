package com.kii.extension.ruleengine.service;


import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.result.CommandResponse;
import com.kii.extension.ruleengine.store.trigger.result.ExceptionResponse;
import com.kii.extension.ruleengine.store.trigger.result.HttpCallResponse;
import com.kii.extension.ruleengine.store.trigger.result.TriggerResult;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName = "portal", appBindSource = "propAppBindTool")
public class ExecuteResultDao extends AbstractDataAccess<TriggerResult> {


	@Override
	protected Class<TriggerResult> getTypeCls() {
		return TriggerResult.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerExecuteResult");
	}


	public void addException(ExceptionResponse  response){
		super.addEntity(response,response.getTriggerID());
	}


	public void addResponse(HttpCallResponse response){
		super.addEntity(response,response.getTriggerID());
	}

	public void addCommandResult(CommandResponse commandResponse) {

		super.addEntity(commandResponse,commandResponse.getTriggerID());
	}
}
