package com.kii.beehive.business.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.service.ThingTagService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerTarget;

@Component
public class SimpleThingTriggerManager {



	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private ThingTagService thingTagService;

	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private AppInfoManager appInfoManager;


	public String  createSimpleTrigger(SimpleTriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();

		SimpleTriggerRecord.ThingID thingID=record.getSource();

		GlobalThingInfo thing=thingTagService.getThingByID(thingID.getThingID());

		registSingleTrigger(thing.getFullKiiThingID(),record.getPerdicate(),triggerID);

		return triggerID;
	}


	private  ServiceCode getSimpleServiceCode(String thingID, String triggerID){

		ThingIDTools.ThingIDCombine  info= ThingIDTools.splitFullKiiThingID(thingID);

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint(EndPointNameConstant.SimpleTriggerEndPoint);
		serviceCode.addParameter("thingID",info.kiiThingID);
		serviceCode.addParameter("triggerID",triggerID);
//		serviceCode.setTargetAppID(info.kiiAppID);

		FederatedAuthResult result=appInfoManager.getDefaultOwer(info.kiiAppID);
		serviceCode.setExecutorAccessToken(result.getAppAuthToken());

		return serviceCode;
	}

	public void registSingleTrigger(String thingID, StatePredicate predicate, String triggerID){

		ThingTrigger triggerInfo=new ThingTrigger();

		triggerInfo.setTitle(triggerID);

		triggerInfo.setTarget(TriggerTarget.SERVER_CODE);
		triggerInfo.setPredicate(predicate);
		triggerInfo.setServiceCode(getSimpleServiceCode(thingID,triggerID));

		thingIFService.createTrigger(thingID,triggerInfo);

	}
}