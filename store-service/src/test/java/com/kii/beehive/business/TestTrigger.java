package com.kii.beehive.business;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.business.service.ThingStateNotifyCallbackService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TargetAction;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

public class TestTrigger extends com.kii.beehive.portal.store.TestInit {


	@Autowired
	private TriggerManager  manager;


	@Autowired
	private ThingStateNotifyCallbackService stateNotifyService;

	public void sendGoodThingStatus(String id){
		ThingStatus status=new ThingStatus();
		status.setField("foo",101);
		status.setField("bar",-13);

		sendThingStatus(id,status);

	}
	
	private void sendThingStatus(String id, ThingStatus status) {

		ThingIDTools.ThingIDCombine combine=ThingIDTools.splitFullKiiThingID(id);

		stateNotifyService.onThingStateChange(combine.kiiAppID,combine.kiiThingID,status);

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


	@Test
	public void testSend() throws IOException {

		sendGoodThingStatus(kiiThingID);

		sendBadThingStatus(kiiThingID);


		sendGoodThingStatus(kiiThingID);

		sendBadThingStatus(kiiThingID);

		sendGoodThingStatus(kiiThingID);

		sendBadThingStatus(kiiThingID);

		System.in.read();
	}

	String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

	long thingID=1052l;

	public void insertTrigger(){


		SimpleTriggerRecord record=new SimpleTriggerRecord();

		SimpleTriggerRecord.ThingID id=new SimpleTriggerRecord.ThingID();
		id.setThingID(thingID);

		record.setSource(id);

		record.addTarget(getTarget() );

		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_FALSE_TO_TRUE);


		record.setPredicate(perdicate);

	}

	@Test
	public void testCRUD(){



		SimpleTriggerRecord record=new SimpleTriggerRecord();

		SimpleTriggerRecord.ThingID id=new SimpleTriggerRecord.ThingID();
		id.setThingID(thingID);

		record.setSource(id);

		record.addTarget(getTarget() );

		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_FALSE_TO_TRUE);


		record.setPredicate(perdicate);

		String triggerID=manager.createTrigger(record);


		TriggerRecord newRecord=manager.getTriggerByID(triggerID);

		assertEquals(newRecord.getType(),record.getType());

		manager.disableTrigger(triggerID);


		try {
			manager.getTriggerByID(triggerID);
			fail();
		}catch(EntryNotFoundException e){


		}

		manager.enableTrigger(triggerID);


		newRecord=manager.getTriggerByID(triggerID);
//
		assertEquals(newRecord.getRecordStatus(), TriggerRecord.StatusType.enable);
//
		manager.deleteTrigger(triggerID);

		try {
			manager.getTriggerByID(triggerID);
			fail();
		}catch(EntryNotFoundException e){


		}



	}


}
