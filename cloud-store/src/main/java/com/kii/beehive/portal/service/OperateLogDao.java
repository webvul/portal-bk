package com.kii.beehive.portal.service;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.OperateLog;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind= TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
@Component
public class OperateLogDao extends AbstractDataAccess<OperateLog>{


	@Override
	protected Class getTypeCls() {
		return OperateLog.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return  new BucketInfo("operateLog");
	}


	private OperateLog getLogInstance(TriggerRecord record){
		OperateLog log=new OperateLog();

		log.setSource(record.getTriggerID());

		log.setSourceType(OperateLog.OperateType.trigger);

		/*
		          time:237418735192733,//精确到毫秒
          userId:”456-6536-3-63456-3455432”,
          triggerId:”230c77e0-47f8-11e6-9c73-00163e007aba”
		 */

		log.addField("triggerId",record.getTriggerID());
		log.addField("userId", record.getUserID());

		log.addField("time",log.getTimestamp());
		log.addField("type",record.getType().name());
		log.addField("withPeriod",record.getPreparedCondition()!=null);
		log.addField("withSchedule",record.getPredicate().getSchedule()!=null);

		return log;

	}

	public void triggerLog(TriggerRecord record,OperateLog.ActionType  type){

		OperateLog  log=getLogInstance(record);
		log.setAction(type);

		super.addEntity(log);

	};
}
