package com.kii.extension.ruleengine;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicy;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class EngineService {


	@Autowired
	private DroolsTriggerService  droolsTriggerService;

	@Autowired
	private RuleGeneral  ruleGeneral;

	private Set<String>  scheduleSet=new ConcurrentSkipListSet<>();


	//TODO:need been finish
	public void createMultipleSourceTrigger(MultipleSrcTriggerRecord record){


		Trigger trigger=new Trigger();
		trigger.setType(TriggerType.multiple);
		trigger.setTriggerID(record.getId());
		trigger.setStream(false);

	}

	public void createSummaryTrigger(SummaryTriggerRecord record, Map<String,Set<String> > summaryMap,boolean isStream){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(record.getId());
		trigger.setType(TriggerType.summary);
		trigger.setWhen(record.getPredicate().getTriggersWhen());
		trigger.setStream(isStream);
		trigger.setEnable(record.getRecordStatus()== TriggerRecord.StatusType.enable);

		String 	rule = ruleGeneral.generDrlConfig(record.getId(), TriggerType.summary, record.getPredicate());

		droolsTriggerService.addSummaryTrigger(trigger,rule);

		record.getSummarySource().forEach((k,v)->{

			v.getExpressList().forEach((exp)->{

				Summary summary=new Summary();
				summary.setTriggerID(trigger.getTriggerID());
				summary.setFieldName(exp.getStateName());

				summary.setSummaryField(k+"."+exp.getSummaryAlias());
				summary.setThings(summaryMap.get(k));

				if(exp.getSlideFuntion()!=null){
					String drl=ruleGeneral.generSlideConfig(trigger.getTriggerID(),k,exp);
					summary.setFunName(exp.getFunction().name());
					droolsTriggerService.addSummary(summary,drl);

				}else{
					summary.setFunName(exp.getFunction().name());
					droolsTriggerService.addSummary(summary);
				}

			});

		});

		if(record.getPredicate().getSchedule()!=null) {
			scheduleSet.add(record.getId());
			droolsTriggerService.fireCondition();
		}

	}

	public void createGroupTrigger(Collection<String> thingIDs, GroupTriggerRecord record){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(record.getId());
		trigger.setType(TriggerType.group);

		TriggerGroupPolicy policy=record.getPolicy();
		trigger.setPolicy(policy.getGroupPolicy());
		trigger.setNumber(policy.getCriticalNumber());

		trigger.setStream(false);

		RuleEnginePredicate predicate=record.getPredicate();

		trigger.setWhen(predicate.getTriggersWhen());

		trigger.setThings(thingIDs);

		trigger.setEnable(record.getRecordStatus()== TriggerRecord.StatusType.enable);

		//TODO:
		predicate.setSchedule(null);

		String rule=ruleGeneral.generDrlConfig(record.getId(),TriggerType.group,predicate);

		droolsTriggerService.addTrigger(trigger,rule);

		if(predicate.getSchedule()!=null) {
			scheduleSet.add(record.getId());
			droolsTriggerService.fireCondition();
		}

	}


	public void createSimpleTrigger(String thingID, String triggerID, SimpleTriggerRecord record){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(triggerID);
		trigger.setType(TriggerType.simple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		trigger.setEnable(TriggerRecord.StatusType.enable == record.getRecordStatus());

		if(!StringUtils.isEmpty(thingID)) {
			trigger.setThings(Collections.singleton(thingID));
		}

		String rule=ruleGeneral.generDrlConfig(triggerID,TriggerType.simple,record.getPredicate());


		droolsTriggerService.addTrigger(trigger,rule);

		if(record.getPredicate().getSchedule()!=null) {
			scheduleSet.add(record.getId());
			droolsTriggerService.fireCondition();
		}


	}

	public void changeThingsInTrigger(String triggerID,Set<String> newThings){

		droolsTriggerService.updateThingsInTrigger(triggerID,newThings);

	}


	public void changeThingsInSummary(String triggerID,String summaryName,Set<String> newThings){

		droolsTriggerService.updateThingsInSummary( triggerID,summaryName,newThings);

	}


	public void initThingStatus(String thingID,ThingStatus status,Date date) {

		droolsTriggerService.initThingStatus(thingID,status,date);
	}

	public void updateThingStatus(String thingID,ThingStatus status,Date time) {

		droolsTriggerService.addThingStatus(thingID,status,time);
	}
	
	public void disableTrigger(String triggerID) {

		droolsTriggerService.disableTrigger(triggerID);

	}


	public void enableTrigger(String triggerID) {

		droolsTriggerService.enableTrigger(triggerID);

		if(scheduleSet.contains(triggerID)) {
			droolsTriggerService.fireCondition();
		}
	}

	public void removeTrigger(String triggerID){

		droolsTriggerService.removeTrigger(triggerID);
		scheduleSet.remove(triggerID);

	}
}
