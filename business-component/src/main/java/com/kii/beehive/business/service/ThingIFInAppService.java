package com.kii.beehive.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.CommandDetail;
import com.kii.extension.sdk.entity.thingif.EndNodeOfGateway;
import com.kii.extension.sdk.entity.thingif.GatewayOfKiiCloud;
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
	private ThingTagManager thingTagManager;

	@Async
	public void onTagIDsChangeFire(List<Long> tagIDList, boolean b) {

		Set<String> tags= thingTagManager.getTagNamesByIDs(tagIDList);

		tags.forEach(name->eventBus.onTagChangeFire(name,b));
	}

	@Async
	public void onTagChangeFire(String tagName,boolean b){

		eventBus.onTagChangeFire(tagName,b);
	}


	private <T>  T doExecWithRealThingID(String fullThingID,Function<String,T> function){
		ThingIDTools.ThingIDCombine combine = ThingIDTools.splitFullKiiThingID(fullThingID);

		resolver.pushAppNameDirectly(combine.kiiAppID);

		T result=function.apply(combine.kiiThingID);

		resolver.pop();

		return result;
	}

	private <T>  T doExecWithRealThingID(String kiiAppID, Supplier<T> function){

		resolver.pushAppNameDirectly(kiiAppID);

		T result=function.get();

		resolver.pop();

		return result;
	}
	public void putStatus(String fullThingID,ThingStatus status){


		doExecWithRealThingID(fullThingID,(th)->{
			 service.putStatus(th,status);
			return 0;
		});

	}

	public ThingStatus getStatus(String fullThingID){


		return doExecWithRealThingID(fullThingID,(th)-> service.getStatus(th));

	}

	public OnBoardingResult onBoarding(OnBoardingParam param,@AppBindParam  String appID){

		KiiAppInfo info=appInfoDao.getAppInfoByID(appID);
		param.setUserID(info.getFederatedAuthResult().getUserID());

		return service.onBoarding(param);
	}

	public String sendCommand(ThingCommand  command,String fullThingID){

		return doExecWithRealThingID(fullThingID,(th)-> service.sendCommand(th,command));

	}

	public CommandDetail readCommand(String fullThingID, String commandID) {

		return doExecWithRealThingID(fullThingID, (th) -> service.readCommand(th, commandID));

	}

	public String createTrigger(String fullThingID,ThingTrigger triggerInfo){

		return doExecWithRealThingID(fullThingID,(th)-> triggerService.createTrigger(th,triggerInfo));

	};

	public void removeTrigger(String fullThingID,String triggerID){
		doExecWithRealThingID(fullThingID,(th)-> {
			triggerService.deleteTrigger(th,triggerID);
			return 0;
		});
	}

	/**
	 * remove thing
	 *
	 * @param fullThingID
     */
	public void removeThing(String fullThingID) {

		doExecWithRealThingID(fullThingID,(th)->{
			service.removeThing(th);
			return 0;
		});
	}

	/**
	 * get all endnodes of gateway
	 *
	 * @param fullThingID
	 * @return example
	 * 	[ {"thingID": "121323","vendorThingID":"e4746a0b"},
	 *	{"thingID": "134434","vendorThingID":"f4746a0b"} ]
	 */
	public List<EndNodeOfGateway> getAllEndNodesOfGateway(String fullThingID) {

		return doExecWithRealThingID(fullThingID,(th)-> service.getAllEndNodesOfGateway(th));
	}




	public List<GatewayOfKiiCloud> getAllEGateway() {
		List<GatewayOfKiiCloud> result = new ArrayList<>();
		appInfoDao.getSlaveAppList().forEach(appInfo->{
			List<GatewayOfKiiCloud> list = doExecWithRealThingID(appInfo.getAppID(),()-> service.getAllGateway());
			list.forEach(gatewayOfKiiCloud -> {
				gatewayOfKiiCloud.setKiiAppID(appInfo.getAppID());
				gatewayOfKiiCloud.setFullKiiThingID(ThingIDTools.joinFullKiiThingID(appInfo.getAppID(), gatewayOfKiiCloud.getThingID()));
			});
			result.addAll(list);
		});


		return result;
	}


}
