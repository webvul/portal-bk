package com.kii.extension.ruleengine.drools;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class DroolsTriggerService {

	@Autowired
	private DroolsRuleService droolsService;

	@Autowired
	protected ResourceLoader loader;

	@Autowired
	private CommandExec exec;



	private  CurrThing curr=new CurrThing();


	private String getDrlContent(String fileName) {

		try {
			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}


	@PostConstruct
	public void initRule(){

		droolsService.initCondition(
				getDrlContent("triggerComm"),
				getDrlContent("groupPolicy"),
				getDrlContent("summaryCompute")
				);

		droolsService.bindWithInstance("exec",exec);

		curr.setThing("NONE");
		droolsService.setGlobal("currThing",curr);
	}


	public void addTrigger(Trigger trigger,String ruleContent){

		droolsService.addCondition("rule"+trigger.getTriggerID(),ruleContent);

		droolsService.addOrUpdateData(trigger);
	}

	public void removeTrigger(String triggerID){

		Trigger trigger=new Trigger();
		trigger.setTriggerID(triggerID);

		droolsService.removeData(trigger);

		droolsService.removeCondition("rule"+triggerID);
	}

	public void updateTrigger(Trigger trigger){
		droolsService.addOrUpdateData(trigger);
	}

	public void addThingStatus(String fullThingID,ThingStatus status){

		ThingStatusInRule newStatus=new ThingStatusInRule();
		newStatus.setThingID(fullThingID);
		newStatus.setValues(status.getFields());

		curr.setThing(fullThingID);
		droolsService.addOrUpdateData(newStatus);

		fireCondition();
	}

	private  void fireCondition(){

		droolsService.fireCondition();


		List<MatchResult> results=droolsService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID()));

	}



}
