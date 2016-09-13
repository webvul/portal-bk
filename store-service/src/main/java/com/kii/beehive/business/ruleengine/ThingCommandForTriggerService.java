package com.kii.beehive.business.ruleengine;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.service.ExecuteResultDao;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.result.CommandResponse;
import com.kii.extension.ruleengine.store.trigger.result.ExceptionResponse;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

@Component
public class ThingCommandForTriggerService {


	private Logger log= LoggerFactory.getLogger(ThingCommandForTriggerService.class);


	@Autowired
	private ThingIFInAppService thingIFService;


	@Autowired
	private AppInfoManager appInfoManager;


	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private ExecuteResultDao resultDao;


	@Autowired
	private TriggerLogTools  logTool;

	public void executeCommand(String triggerID,CommandToThing command,Map<String,String> params){


		for (Map<String, Action> actionMap : command.getCommand().getActions()) {

			actionMap.values().forEach((act) -> {
				act.getFields().forEach((n, v) -> {

					if(v instanceof  String){
						act.setField(n, StrTemplate.generByMap((String)v, params));
					}

				});
			});
		}

		Set<GlobalThingInfo> thingList = thingTagService.getThingInfos(command.getSelector());

		thingList.stream().filter((th) -> !StringUtils.isEmpty(th.getFullKiiThingID())).forEach(thing -> {

			ThingCommand  cmd=command.getCommand();
			String version=thing.getSchemaVersion();
			int ver=1;
			try {
				ver = Integer.parseInt(version);
			}catch(Exception e){
				log.error("SchemaVersion invalid",e);
			}
			cmd.setSchemaVersion(ver);
			cmd.setSchema(thing.getSchemaName());

			sendCmd(command.getCommand(), thing,triggerID);

		});


	}

	private void sendCmd(ThingCommand command, GlobalThingInfo thingInfo, String triggerID) {

		CommandResponse resp = new CommandResponse();
		resp.setCommand(command);

		try {
			String appID = thingInfo.getKiiAppID();


			command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());


			String cmdResult = thingIFService.sendCommand(command, thingInfo.getFullKiiThingID());

			resp.setResult(cmdResult);
			resp.setTriggerID(triggerID);
		}catch(Exception ex){

			resultDao.addException(new ExceptionResponse(ex.getCause()));
		}

		resultDao.addCommandResult(resp);

	}


}
