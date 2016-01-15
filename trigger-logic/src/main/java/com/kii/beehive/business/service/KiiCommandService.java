package com.kii.beehive.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.AppInfoManager;
import com.kii.beehive.portal.manager.ThingTagManager;
import com.kii.beehive.portal.service.ClientTriggerResultDao;
import com.kii.beehive.portal.service.TriggerRecordDao;
import com.kii.beehive.portal.store.entity.trigger.ClientTriggerResult;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

@Component
public class KiiCommandService {

	public static final String SCHEMA = "demo";
	private static final int SCHEMA_VERSION = 1;
	@Autowired
	private ThingIFInAppService thingIFService;

	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private AppInfoManager appInfoManager;


	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private ServiceExtensionDeployService extensionService;

	@Autowired
	private ClientTriggerResultDao  resultDao;


	public void doCommand(TriggerRecord  record) {
		List<TriggerTarget> targets=record.getTargets();

		targets.forEach(target->{

			TargetAction action=target.getCommand();


			List<GlobalThingInfo>  thingList=thingTagService.getThingInfos(target.getSelector());

			thingList.forEach(thing->{

				sendCmdToThing(thing,action,record.getId());
			});

		});

	}

	public void doCommand(String triggerID){

		TriggerRecord record=triggerDao.getTriggerRecord(triggerID);

		if(record==null){
			return;
		}

		doCommand(record);
	}


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
		command.setSchema(SCHEMA);
		command.setSchemaVersion(SCHEMA_VERSION);

		thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
	}


}
