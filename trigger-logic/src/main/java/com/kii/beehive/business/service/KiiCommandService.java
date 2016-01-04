package com.kii.beehive.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

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
	private AppInfoDao appInfoDao;

	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private ThingTagService thingTagService;

	@Autowired
	private ServiceExtensionDeployService extensionService;

	@Autowired
	private ClientTriggerResultDao  resultDao;

	public  void sendCmdToThing(long globalThingID,TargetAction target,String triggerID){



		if(target.getCommand()!=null) {
			GlobalThingInfo thingInfo=thingTagService.getThingByID(globalThingID);

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

		resolver.setAppInfoDirectly(appInfoDao.getAppInfoByID(appID).getAppInfo());

		thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
	}


	public void sendCmdToTagExpress(boolean isAnd, List<String>  tagCollect, TargetAction action,String triggerID){


		List<GlobalThingInfo>  thingList=thingTagService.queryThingByTagExpress(isAnd,tagCollect);

		thingList.forEach(thing->{

			if(action.getCommand()!=null){
				sendCmd(action.getCommand(),thing);
			}else if(action.getServiceCode()!=null){
				callServiceCode(action.getServiceCode(),triggerID);
			}

		});
	}
}
