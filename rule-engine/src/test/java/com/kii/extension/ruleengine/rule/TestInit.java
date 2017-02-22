package com.kii.extension.ruleengine.rule;

import java.util.Date;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.drools.CommandExec;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.beehive.portal.store.entity.trigger.task.CommandToThing;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:ruleEngineCtx.xml"})
public class TestInit {

	@Autowired
	protected BeehiveTriggerService engine;




	@Autowired
	protected CommandExec exec;

//	@Autowired
//	protected TriggerRecordDao dao;


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
		
		BusinessDataObject obj=new BusinessDataObject(id,null, BusinessObjType.Thing);
		obj.setData(status.getFields());
		obj.setCreated(new Date());
		
		engine.updateBusinessData(obj);

	}

	public ExecuteTarget getTarget(){

		CommandToThing target=new CommandToThing();

		ThingCommand command=new ThingCommand();
		Action thingAction=new Action();
		thingAction.setField("power",true);
		command.addAction("ON",thingAction);

		target.setCommand(command);

		return target;
	}
}
