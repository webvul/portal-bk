package com.kii.extension.ruleengine.drools;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class DroolsTriggerService {

	@Autowired
	@Qualifier("cloudDroolsService")
	private DroolsRuleService cloudService;

	@Autowired
	@Qualifier("streamDroolsService")
	private DroolsRuleService streamService;

	@Autowired
	private CommandExec exec;



	private Map<String, Trigger> triggerMap=new ConcurrentHashMap<>();

	private  CurrThing curr=new CurrThing();

	@PostConstruct
	public void initRule(){

		curr.setThing("NONE");

		cloudService.setGlobal("currThing",curr);
		streamService.setGlobal("currThing",curr);
	}


	private DroolsRuleService getService(Trigger trigger){

		if(trigger.isStream()){
			return streamService;
		}else{
			return cloudService;
		}

	}

	public void addTrigger(Trigger triggerInput,String ruleContent){

		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);

		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);

	}

	public void removeTrigger(String triggerID){

		Trigger trigger=triggerMap.get(triggerID);

		getService(trigger).removeData(trigger);
		getService(trigger).removeCondition("rule"+triggerID);

	}

	public void enableTrigger(String triggerID) {

		Trigger trigger=triggerMap.get(triggerID);

		trigger.setEnable(true);

		getService(trigger).addOrUpdateData(trigger);
	}

	public void disableTrigger(String triggerID) {

		Trigger trigger=triggerMap.get(triggerID);

		trigger.setEnable(false);

		getService(trigger).addOrUpdateData(trigger);
	}

	public void addThingStatus(String fullThingID,ThingStatus status){

		ThingStatusInRule newStatus=new ThingStatusInRule();
		newStatus.setThingID(fullThingID);
		newStatus.setValues(status.getFields());

		curr.setThing(fullThingID);

		cloudService.addOrUpdateData(newStatus);
		streamService.addOrUpdateData(newStatus);

		fireCondition();
	}

	private  void fireCondition(){

		List<MatchResult> results=cloudService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID()));

		results=streamService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID()));

	}
	

}
