package com.kii.beehive.business.ruleengine;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.kii.beehive.business.helper.OpLogTools;
import com.kii.beehive.portal.auth.AuthInfoStore;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.TargetAction;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.sdk.entity.thingif.ThingCommand;



@Component
public class CommandExecuteService {

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
	private OpLogTools logTool;




	public void doCommand(TriggerRecord  record) {
		List<ExecuteTarget> targets=record.getTargets();

		targets.forEach(target->{

			TargetAction action=target.getCommand();


			List<GlobalThingInfo>  thingList=thingTagService.getThingInfos(target.getSelector());


			thingList.forEach(thing->{

				sendCmdToThing(thing,action,record.getId());
				//日期时间+当前用户ID+"trigger”+trigger type(simple/group/summary)+”fire"+当前triggerID+触发源
				List<String> list = new LinkedList<>();
				list.add(AuthInfoStore.getUserID());
				list.add("trigger");
				list.add(record.getType().name());
				list.add("exec");
				list.add(record.getTriggerID());
				//触发目标
				list.add(thing.getId()+"");
				logTool.write(list);
			});

		});

	}

	public void doCommand(String triggerID){

		TriggerRecord record=triggerDao.getEnableTriggerRecord(triggerID);

		if(record==null){
			return;
		}

		doCommand(record);
	}


	public  void sendCmdToThing(GlobalThingInfo thingInfo,TargetAction target,String triggerID){


		// send command only when command is not null and thing completed onboarding
		if(target.getCommand()!=null && !Strings.isBlank(thingInfo.getFullKiiThingID())) {

			sendCmd(target.getCommand(), thingInfo);
		}

		//TODO:need think about it again.
//		if(target.getServiceCode()!=null){
//			callServiceCode(target.getServiceCode(),triggerID);
//		}

	}

//	private void callServiceCode(ServiceCode serviceCode, String triggerID) {
//
//		String serviceName=serviceCode.getEndpoint();
//		Object param=serviceCode.getParameters();
//		JsonNode result=extensionService.callServiceExtension(serviceCode.getTargetAppID(),serviceName,param,JsonNode.class);
//
//		ClientTriggerResult  clientResult=new ClientTriggerResult();
//
//		clientResult.setResult(result);
//		clientResult.setServiceName(serviceCode.getEndpoint());
//		clientResult.setTriggerID(triggerID);
//
//		resultDao.addEntity(clientResult);
//
//	}

	private void sendCmd(ThingCommand command, GlobalThingInfo thingInfo) {
		String appID=thingInfo.getKiiAppID();

//		resolver.setAppInfoDirectly(appInfoDao.getAppInfoByID(appID).getAppInfo());

		command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());
		command.setSchema(SCHEMA);
		command.setSchemaVersion(SCHEMA_VERSION);

		thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
	}


}
