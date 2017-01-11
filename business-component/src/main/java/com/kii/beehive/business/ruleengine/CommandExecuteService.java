package com.kii.beehive.business.ruleengine;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.OperateLogDao;
import com.kii.beehive.portal.store.entity.OperateLog;
import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.schedule.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.task.CallBusinessFunction;
import com.kii.extension.ruleengine.store.trigger.task.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.task.SettingTriggerGroupParameter;
import com.kii.extension.tools.CronGeneral;




@Component
public class CommandExecuteService implements EventCallback {

	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private ThingCommandForTriggerService commandService;


	@Autowired
	private HttpCallService  httpCallService;

	
	@Autowired
	private BusinessFunctionCallService funService;
	
	@Autowired
	private SetParameterExecuteService settingParamService;
	
	
	@Lazy
	@Autowired
	private TriggerOperate creator;


	@Autowired
	private OperateLogDao logTool;


	private ScheduledExecutorService executeService=new ScheduledThreadPoolExecutor(10);
	
	
	
	@Async
	@Override
	public void onTriggerFire(String triggerID,ExecuteParam params) {




		TriggerRecord record=triggerDao.getEnableTriggerRecord(triggerID);

		if(record==null){
			return;
		}


		logTool.triggerLog(record, OperateLog.ActionType.fire);

		List<ExecuteTarget> targets=record.getTargets();

		int idx=0;

		for(ExecuteTarget target:targets){



			Runnable run= () -> {

				switch (target.getType()) {

					case ThingCommand:
						CommandToThing command=(CommandToThing)target;

						commandService.executeCommand(command,params);
						break;
					case HttpApiCall:
						CallHttpApi call=(CallHttpApi)target;

					
						call.fillParam(params.getBusinessParamsInStr());

						httpCallService.doHttpApiCall(call,record.getTriggerID(),params);

						break;
					case CallBusinessFunction:
						
						CallBusinessFunction fun=(CallBusinessFunction)target;
						
						funService.doBusinessFunCall(fun,record.getTriggerID(),params);
						break;
						
					case SettingParameter:
						SettingTriggerGroupParameter settingParam=(SettingTriggerGroupParameter)target;
						
						settingParamService.settingParam(settingParam,params);
						
				}
			};

			String delay=params.getDelayParam(idx);

			if(StringUtils.isBlank(delay)||"null".equals(delay)) {
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





	private void  addNewTrigger(TriggerRecord  record,int delay,int idx){


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
			newRec.setTargets(Collections.singletonList(target));

			triggerDao.addEntity(newRec);
			creator.createTrigger(newRec,false);
		}
	}

}
