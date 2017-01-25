package com.kii.beehive.business.ruleengine;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.service.ExecuteResultDao;
import com.kii.extension.ruleengine.store.trigger.task.CommandResponse;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.task.ExceptionInfo;
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
	private ResponseBuilder  builder;

	public void saveComandResponse(ThingCommand  command){



		CommandResponse resp=resultDao.getCommandResultByID(command.getCommandID());

		if(resp!=null) {
			resultDao.updateCommandResult(command, resp.getId());
		}
	}


	public void executeCommand( CommandToThing command, ExecuteParam params){

		for (Map<String, Action> actionMap : command.getCommand().getActions()) {

			actionMap.values().forEach((act) -> {
				act.getFields().forEach((n, v) -> {

					if(v instanceof  String){
						act.setField(n, StrTemplate.generByMap((String)v, params.getBusinessParamsInStr()));
					}

				});
			});
		}
		
		Set<GlobalThingInfo> thingList=new HashSet<>();
		
		if(command.getSelector().notEmpty()){
			thingList.addAll(thingTagService.getThingInfos(command.getSelector()));
		}
		
		if(command.getThingList()!=null){
			thingList.addAll(thingTagService.getThingInfosByIDs(command.getThingList().stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toSet())));
		}
		
		
		Set<GlobalThingInfo> errThingIDs=thingList.stream().filter((th) -> StringUtils.isEmpty(th.getFullKiiThingID())).collect(Collectors.toSet());

		errThingIDs.parallelStream().forEach(th->{
			CommandResponse resp =  builder.getCmdResponse(params);
			resp.setThingID(th.getId());
			
			ExceptionInfo info=new ExceptionInfo();
			info.setMessage("KiiCloud thing id not exist");
			resp.setExceptionInfo(info);
			
			resultDao.addTaskResult(resp);
		});
		
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

			CommandResponse resp =  builder.getCmdResponse(params);

			resp.setThingID(thing.getId());
			resp.setKiiThingID(thing.getFullKiiThingID());

			resp.setCommand(cmd);

			try {
				String appID = thing.getKiiAppID();


				cmd.setUserID(appInfoManager.getDefaultOwer(appID));


				String cmdResult = thingIFService.sendCommand(cmd, thing.getFullKiiThingID());

				resp.setResult(cmdResult);
			}catch(Exception ex){

				resp.bindException(ex);
			}

			resultDao.addTaskResult(resp);

		});

		
		
		return;
	}



}
