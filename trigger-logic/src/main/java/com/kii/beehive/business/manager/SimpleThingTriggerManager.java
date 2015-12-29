package com.kii.beehive.business.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.service.ThingTagService;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;

@Component
public class SimpleThingTriggerManager {



	@Autowired
	private TriggerRecordDao triggerDao;

	@Autowired
	private ThingTagService thingTagService;

	@Autowired
	private ThingIFInAppService thingIFService;



	public String  createSimpleTrigger(SimpleTriggerRecord record){


		String triggerID=triggerDao.addEntity(record).getObjectID();

		SimpleTriggerRecord.ThingID thingID=record.getSource();

		GlobalThingInfo thing=thingTagService.getThingByID(thingID.getThingID());
		registSingleTrigger(thing.getFullKiiThingID(),record.getPerdicate(),triggerID);

		return triggerID;
	}


	private static ServiceCode getSimpleServiceCode(String thingID, String triggerID){

		ServiceCode serviceCode=new ServiceCode();

		serviceCode.setEndpoint(EndPointNameConstant.SimpleTriggerEndPoint);
		serviceCode.addParameter("thingID",thingID);
		serviceCode.addParameter("triggerID",triggerID);

		return serviceCode;
	}

	public void registSingleTrigger(String thingID, StatePredicate predicate, String triggerID){

//		GlobalThingInfo thing=thingService.getThingByVendorThingID(thingID);

		ThingTrigger triggerInfo=new ThingTrigger();


		triggerInfo.setPredicate(predicate);
		triggerInfo.setServiceCode(getSimpleServiceCode(thingID,triggerID));

		thingIFService.createTrigger(thingID,triggerInfo);

	}
}
