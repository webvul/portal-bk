package com.kii.extension.ruleengine.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.PortalTokenBindTool;
import com.kii.extension.ruleengine.store.trigger.task.CommandResponse;
import com.kii.extension.ruleengine.store.trigger.task.TriggerResult;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
public class ExecuteResultDao extends AbstractDataAccess<TriggerResult> {


	@Override
	protected Class<TriggerResult> getTypeCls() {
		return TriggerResult.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerExecuteResult");
	}


	public void addTaskResult(TriggerResult commandResponse) {

		super.addEntity(commandResponse);
	}


	public CommandResponse getCommandResultByID(String command){


		QueryParam param= ConditionBuilder.newCondition().equal("task",command).getFinalQueryParam();

		List<TriggerResult>  list=super.fullQuery(param);

		if(list.isEmpty()){
			return null;
		}else{
			return (CommandResponse)list.get(0);
		}

	}

	public void updateCommandResult(ThingCommand command,String id){

		Map<String,Object> params=new HashMap<>();
		params.put("command",command);

		super.updateEntity(params,id);
	}
	
	
}
