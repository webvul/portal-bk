package com.kii.beehive.business.ruleengine;

import javax.annotation.PostConstruct;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.helper.TriggerCreator;
import com.kii.extension.ruleengine.EventCallback;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.tools.CronGeneral;


@Component
public class CommandExecuteService implements EventCallback {

	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private ThingCommandForTriggerService commandService;


	@Autowired
	private HttpCallService  httpCallService;

	@Lazy
	@Autowired
	private TriggerCreator creator;



	private ScheduledExecutorService executeService=new ScheduledThreadPoolExecutor(10);


	@PostConstruct
	public  void init(){


	}


	@Async
	@Override
	public void onTriggerFire(String triggerID,Map<String,String> params) {

		TriggerRecord trigger=triggerDao.getEnableTriggerRecord(triggerID);

		if(trigger==null){
			return;
		}

		doCommand(trigger,params);
	}


	private  void doCommand(TriggerRecord  record,Map<String,String> params) {

		List<ExecuteTarget> targets=record.getTargets();

		int idx=0;

		for(ExecuteTarget target:targets){

			String delayParam="delay_"+idx;


			Runnable run= () -> {

				switch (target.getType()) {

					case "ThingCommand":
						CommandToThing command=(CommandToThing)target;

						commandService.executeCommand(record.getTriggerID(),command,params);
						break;
					case "HttpApiCall":
						CallHttpApi call=(CallHttpApi)target;

						httpCallService.doHttpApiCall(call,record.getTriggerID(),params);

						break;
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
