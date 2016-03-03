package com.kii.extension.ruleengine;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.sdk.entity.thingif.ThingStatus;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerGroupPolicy;

@Component
public class EngineService {


	@Autowired
	private DroolsTriggerService  droolsTriggerService;

	@Autowired
	private RuleGeneral  ruleGeneral;




	public void createSummaryTrigger(SummaryTriggerRecord  record){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(record.getId());
		trigger.setType(TriggerType.summary);
		trigger.setWhen(record.getPredicate().getTriggersWhen());
		trigger.setStream(false);

		String 	rule = ruleGeneral.generDrlConfig(record.getId(), TriggerType.group, record.getPredicate());

		droolsTriggerService.addTrigger(trigger,rule);


		record.getSummarySource().forEach((k,v)->{

			v.getExpressList().forEach((exp)->{

				Summary summary=new Summary();
				summary.setTriggerID(trigger.getTriggerID());
				summary.setFieldName(exp.getStateName());
				summary.setFunName(exp.getFunction().name());
				summary.setSummaryField(k+"."+exp.getSummaryAlias());
				droolsTriggerService.addSummary(summary);
			});

		});


	}

	public void createGroupTrigger(Set<String> thingIDs, TriggerGroupPolicy policy, String triggerID, RuleEnginePredicate predicate){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(triggerID);
		trigger.setType(TriggerType.group);

		trigger.setPolicy(policy.getGroupPolicy());
		trigger.setNumber(policy.getCriticalNumber());


		trigger.setStream(false);
		trigger.setWhen(predicate.getTriggersWhen());

		trigger.setThings(thingIDs);


		String rule=ruleGeneral.generDrlConfig(triggerID,TriggerType.group,predicate);

		droolsTriggerService.addTrigger(trigger,rule);

	}


	public void createSimpleTrigger(String thingID, String triggerID, RuleEnginePredicate predicate){


		Trigger trigger=new Trigger();

		trigger.setTriggerID(triggerID);
		trigger.setType(TriggerType.simple);
		trigger.setStream(false);
		trigger.setWhen(predicate.getTriggersWhen());

		if(!StringUtils.isEmpty(thingID)) {
			trigger.setThings(Collections.singleton(thingID));
		}

		String rule=ruleGeneral.generDrlConfig(triggerID,TriggerType.simple,predicate);

		droolsTriggerService.addTrigger(trigger,rule);

	}

	public void changeThingsInTrigger(String triggerID,Set<String> newThings){

		droolsTriggerService.updateThingsInTrigger(triggerID,newThings);

	}


	public void changeThingsInSummary(String triggerID,String summaryName,Set<String> newThings){

		droolsTriggerService.updateThingsInSummary( triggerID,summaryName,newThings);

	}
	
	public void updateThingStatus(String thingID,ThingStatus status) {

		droolsTriggerService.addThingStatus(thingID,status);
	}
	
	public void disableTrigger(String triggerID) {

		droolsTriggerService.disableTrigger(triggerID);

	}


	public void enableTrigger(String triggerID) {

		droolsTriggerService.enableTrigger(triggerID);

	}
}
