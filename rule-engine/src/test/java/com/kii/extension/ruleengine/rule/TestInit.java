package com.kii.extension.ruleengine.rule;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.TargetAction;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:ruleEngineCtx.xml"})
public class TestInit {

	@Autowired
	protected EngineService engine;


	public void sendGoodThingStatus(String id){
		ThingStatus status=new ThingStatus();
		status.setField("foo",101);
		status.setField("bar",-13);

		sendThingStatus(id,status);

	}


	public void sendBadThingStatus(String id){
		ThingStatus status=new ThingStatus();
		status.setField("foo",-97);
		status.setField("bar",17);

		sendThingStatus(id,status);

	}

	public void sendRandomThingStatus(String id){
		ThingStatus status=new ThingStatus();
		status.setField("foo",Math.random()*100-50);
		status.setField("bar",Math.random()*100-50);

		sendThingStatus(id,status);

	}

	private void sendThingStatus(String id, ThingStatus status){

		engine.updateThingStatus(id,status);

	}

	public ExecuteTarget getTarget(){

		ExecuteTarget target=new ExecuteTarget();
		TargetAction action=new TargetAction();
		ThingCommand command=new ThingCommand();
		Action thingAction=new Action();
		thingAction.setField("power",true);
		command.addAction("ON",thingAction);

		action.setCommand(command);
		target.setCommand(action);

		return target;
	}
}
