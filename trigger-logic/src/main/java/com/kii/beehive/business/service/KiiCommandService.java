package com.kii.beehive.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.ClientTriggerResultDao;
import com.kii.beehive.portal.store.entity.trigger.ClientTriggerResult;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.service.ServiceExtensionService;

@Component
public class KiiCommandService {

	@Autowired
	private ThingIFInAppService thingIFService;


	@Autowired
	private AppInfoManager appInfoManager;


	@Autowired
	private ThingTagService thingTagService;

	@Autowired
	private ServiceExtensionDeployService extensionService;

	@Autowired
	private ClientTriggerResultDao  resultDao;

	public  void sendCmdToThing(GlobalThingInfo thingInfo,TargetAction target,String triggerID){



		if(target.getCommand()!=null) {

			sendCmd(target.getCommand(), thingInfo);
		}

		if(target.getServiceCode()!=null){
			callServiceCode(target.getServiceCode(),triggerID);
		}

	}

	private void callServiceCode(ServiceCode serviceCode, String triggerID) {

		String serviceName=serviceCode.getEndpoint();
		Object param=serviceCode.getParameters();
		JsonNode result=extensionService.callServiceExtension(serviceCode.getTargetAppID(),serviceName,param,JsonNode.class);

		ClientTriggerResult  clientResult=new ClientTriggerResult();

		clientResult.setResult(result);
		clientResult.setServiceName(serviceCode.getEndpoint());
		clientResult.setTriggerID(triggerID);

		resultDao.addEntity(clientResult);

	}

	private void sendCmd(ThingCommand command, GlobalThingInfo thingInfo) {
		String appID=thingInfo.getKiiAppID();

//		resolver.setAppInfoDirectly(appInfoDao.getAppInfoByID(appID).getAppInfo());

		command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());
		command.setSchema(thingInfo.getSchema());
		command.setSchemaVersion(thingInfo.getSchemaVersion());

		thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
	}


}
