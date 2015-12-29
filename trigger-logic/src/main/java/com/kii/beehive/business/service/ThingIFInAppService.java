package com.kii.beehive.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.service.ThingIFService;
import com.kii.extension.sdk.service.TriggerService;

@Component
public class ThingIFInAppService {

	@Autowired
	private ThingIFService  service;


	@Autowired
	private TriggerService triggerService;

	@Autowired
	private AppBindToolResolver resolver;

	private String getRealThingID(String fullThingID){
		String[] param= ThingIDTools.splitFullKiiThingID(fullThingID);

		resolver.setAppName(param[0]);

		return param[1];
	}

	public void putStatus(String fullThingID,ThingStatus status){

		service.putStatus(getRealThingID(fullThingID),status);

	}

	public ThingStatus getStatus(String fullThingID){

		return service.getStatus(getRealThingID(fullThingID));

	}

	public OnBoardingResult onBoarding(OnBoardingParam param,String appID){
		resolver.setAppName(appID);
		return service.onBoarding(param);
	}

	public String sendCommand(ThingCommand  command,String fullThingID){

		return service.sendCommand(getRealThingID(fullThingID),command);

	}

	public String createTrigger(String fullThingID,ThingTrigger triggerInfo){

		String thingID=getRealThingID(fullThingID);
		return triggerService.createTrigger(thingID,triggerInfo);

	};

	public void removeTrigger(String fullThingID,String triggerID){
		String thingID=getRealThingID(fullThingID);
		triggerService.deleteTrigger(thingID,triggerID);
	}


}
