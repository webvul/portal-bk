package com.kii.beehive.business.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.manager.ThingStateManager;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
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

	@Autowired
	private AppInfoDao  appInfoDao;

	@Autowired
	private BusinessEventBus eventBus;

	@Autowired
	private ThingStateManager thingTagManager;

	@Async
	public void onTagIDsChangeFire(List<Long> tagIDList, boolean b) {

		Set<String> tags= thingTagManager.getTagNamesByIDs(tagIDList);

		tags.forEach(name->eventBus.onTagChangeFire(name,b));
	}

	@Async
	public void onTagChangeFire(String tagName,boolean b){

		eventBus.onTagChangeFire(tagName,b);
	}


	private String getRealThingID(String fullThingID){
		ThingIDTools.ThingIDCombine combine = ThingIDTools.splitFullKiiThingID(fullThingID);

		resolver.setAppName(combine.kiiAppID);

		return combine.kiiThingID;
	}

	public void putStatus(String fullThingID,ThingStatus status){


		service.putStatus(getRealThingID(fullThingID),status);

	}

	public ThingStatus getStatus(String fullThingID){

		return service.getStatus(getRealThingID(fullThingID));

	}

	public OnBoardingResult onBoarding(OnBoardingParam param,String appID){

		KiiAppInfo info=appInfoDao.getAppInfoByID(appID);
		param.setUserID(info.getFederatedAuthResult().getUserID());

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
