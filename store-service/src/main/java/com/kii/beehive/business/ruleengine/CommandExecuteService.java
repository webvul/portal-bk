package com.kii.beehive.business.ruleengine;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;



@Component
public class CommandExecuteService {

	public static final String SCHEMA = "threaddemo";
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
	private HttpCallService  httpCallService;

	@Autowired
	private TriggerLogTools  logTool;

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private AppBindToolResolver resolver;

	private AtomicBoolean  sign=new AtomicBoolean(true);
	public void enable(){

		sign.set(true);
	}

	public void disable(){

		sign.set(false);
	}



	public void doCommand(TriggerRecord  record,Map<String,String> params) {

		if(!sign.get()){
			return;
		}
		List<ExecuteTarget> targets=record.getTargets();

		targets.forEach(target-> {

					switch (target.getType()) {

						case "ThingCommand":
							CommandToThing command=(CommandToThing)target;

							Set<GlobalThingInfo> thingList = thingTagService.getThingInfos(command.getSelector());

							thingList.stream().filter((th) -> !StringUtils.isEmpty(th.getFullKiiThingID())).forEach(thing -> {

								for (Map<String, Action> actionMap : command.getCommand().getActions()) {

									actionMap.values().forEach((act) -> {
										act.getFields().forEach((n, v) -> {
											act.setField(n, StrTemplate.generByMap(n, params));
										});
									});
								}
								sendCmd(command.getCommand(), thing);

							});
							logTool.outputCommandLog(thingList,record);
							break;
						case "HttpApiCall":
							CallHttpApi call=(CallHttpApi)target;

							call.fillParam(params);

							httpCallService.doHttpApiCall(call);

							break;
					}

		});

	}


	private void sendCmd(ThingCommand command, GlobalThingInfo thingInfo) {
		String appID=thingInfo.getKiiAppID();


		command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());
		command.setSchema(SCHEMA);
		command.setSchemaVersion(SCHEMA_VERSION);

		thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
	}


}
