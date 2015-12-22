package com.kii.beehive.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.business.transaction.ThingTagService;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.condition.NotLogic;
import com.kii.extension.sdk.service.TriggerService;

@Component
public class KiiTriggerRegistService {

	@Autowired
	private TriggerService triggerService;

	@Autowired
	private ThingTagService thingService;

	@Autowired
	private AppInfoDao appInfoDao;


	private static ServiceCode getSimpleServiceCode(String thingID,String triggerID){

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint("commonTriggerEndPoint");
		serviceCode.addParameter("thingID",thingID);
		serviceCode.addParameter("triggerID",triggerID);

		return serviceCode;
	}


	private static ServiceCode getPositiveServiceCode(String thingID,String triggerID){

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint("positiveTriggerEndPoint");
		serviceCode.addParameter("thingID",thingID);
		serviceCode.addParameter("triggerID",triggerID);

		return serviceCode;
	}

	private static ServiceCode getNegativeServiceCode(String thingID,String triggerID){

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint("negativeTriggerEndPoint");
		serviceCode.addParameter("thingID",thingID);
		serviceCode.addParameter("triggerID",triggerID);

		return serviceCode;
	}


	public void registSingleTrigger(String thingID, StatePredicate predicate,String triggerID){

//		GlobalThingInfo thing=thingService.getThingByVendorThingID(thingID);

		ThingTrigger triggerInfo=new ThingTrigger();


		triggerInfo.setPredicate(predicate);
		triggerInfo.setServiceCode(getSimpleServiceCode(thingID,triggerID));

		triggerService.createTrigger(thingID,triggerInfo);

	}



	public void registDoubleTrigger(String thingID, Condition condition, String triggerID){

		StatePredicate positivePredicate=new StatePredicate();
		positivePredicate.setCondition(condition);
		positivePredicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);

		ThingTrigger triggerTrue=new ThingTrigger();

		triggerTrue.setPredicate(positivePredicate);
		triggerTrue.setServiceCode(getPositiveServiceCode(thingID,triggerID));
		triggerService.createTrigger(thingID,triggerTrue);


		StatePredicate negativePredicate=new StatePredicate();
		negativePredicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);
		NotLogic negCond=new NotLogic();
		negCond.setClause(condition);
		negativePredicate.setCondition(ConditionBuilder.notCondition(condition).getConditionInstance());

		ThingTrigger triggerFalse=new ThingTrigger();

		triggerFalse.setPredicate(negativePredicate);
		triggerFalse.setServiceCode(getNegativeServiceCode(thingID,triggerID));

		triggerService.createTrigger(thingID,triggerFalse);

	}
}
