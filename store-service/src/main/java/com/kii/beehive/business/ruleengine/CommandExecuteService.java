package com.kii.beehive.business.ruleengine;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.helper.TriggerCreator;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.extension.ruleengine.service.ExecuteResultDao;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.result.CommandResponse;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.tools.CronGeneral;


@Component
public class CommandExecuteService {

//	public static final String SCHEMA = "threaddemo";
//	private static final int SCHEMA_VERSION = 1;

	private Logger log= LoggerFactory.getLogger(CommandExecuteService.class);

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

	@Lazy
	@Autowired
	private TriggerCreator creator;

	@Autowired
	private ExecuteResultDao resultDao;


	private ScheduledExecutorService executeService=new ScheduledThreadPoolExecutor(10);


	public void doCommand(TriggerRecord  record,Map<String,String> params) {

		List<ExecuteTarget> targets=record.getTargets();

		int idx=0;

		for(ExecuteTarget target:targets){

			String delayParam="delay_"+idx;


			Runnable run=new Runnable() {
				@Override
				public void run() {

					switch (target.getType()) {

						case "ThingCommand":
							CommandToThing command=(CommandToThing)target;


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

								sendCmd(command.getCommand(), thing,record.getTriggerID());

							});

							logTool.outputCommandLog(thingList,record);
							break;
						case "HttpApiCall":
							CallHttpApi call=(CallHttpApi)target;

							call.fillParam(params);

							httpCallService.doHttpApiCall(call,record.getTriggerID());

							break;
					}
				}
			};

			String delay=params.get(delayParam);

			if(StringUtils.isBlank(delay)) {
				executeService.submit(run);
			}else{
				long delayInt=Long.parseLong(delay);

				if(target.isDoubleCheck()){
					addNewTrigger(record, (int) delayInt,idx);
				}else {
					executeService.schedule(run, delayInt, TimeUnit.MINUTES);
				}
			}

			idx++;

		}


	}


	private void sendCmd(ThingCommand command, GlobalThingInfo thingInfo,String triggerID) {
		String appID=thingInfo.getKiiAppID();


		command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());


		String cmdResult=thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
		CommandResponse resp=new CommandResponse(cmdResult);
		resp.setTriggerID(triggerID);
		resultDao.addCommandResult(resp);

	}


	private void  addNewTrigger(TriggerRecord  record,int delay,int idx){


		int i=0;
		ExecuteTarget  target=record.getTargets().get(idx);

		if(target.isDoubleCheck()){
			TriggerRecord newRec=BeanUtils.instantiate(record.getClass());
			BeanUtils.copyProperties(record,newRec);

			String newID=record.getTriggerID()+"_delay_"+idx;

			newRec.setPreparedCondition(null);
			newRec.setName(record.getName()+"_delay");
			newRec.setRecordStatus(TriggerRecord.StatusType.enable);

			newRec.setId(newID);

			CronPrefix schedule=new CronPrefix();

			Calendar cal= Calendar.getInstance();

			cal.add(Calendar.MINUTE,delay);

			String cron= CronGeneral.getCurrentCron(cal);

			schedule.setCron(cron);

			RuleEnginePredicate predicate=record.getPredicate();
			predicate.setSchedule(schedule);
			newRec.setPredicate(predicate);

			target.setDelay(null);
			target.setDoubleCheck(false);
			newRec.setTarget(Collections.singletonList(target));

			creator.createTrigger(newRec);
		}
	}

}
