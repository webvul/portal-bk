package com.kii.beehive.business.ruleEngine;


import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.drools.CommandExec;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.schedule.IntervalPrefix;
import com.kii.extension.ruleengine.store.trigger.schedule.TimerUnitType;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/portal/store/testStoreServiceContext.xml" })
public class TestSimple {

	@Autowired
	private TriggerManager manager;




	@Autowired
	protected CommandExec exec;


//	@Autowired
//	private RuleEngineInputCallback statusChangeCallback;


	public ExecuteTarget getTarget(){

		CommandToThing target=new CommandToThing();
		ThingCommand command=new ThingCommand();
		Action thingAction=new Action();
		thingAction.setField("power",true);
		command.addAction("ON",thingAction);

		target.setCommand(command);

		return target;
	}

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

	private void sendThingStatus(String id, ThingStatus status){

//		statusChangeCallback.onEventFire("",status,id,new Date());

	}

	@Test
	public void test() throws IOException {

		ThingStatus status=new ThingStatus();
		status.setField("power","false");
		String id="192b49ce-th.f83120e36100-2939-5e11-cd5e-02a7cefb";
//		statusChangeCallback.onEventFire(status,id,new Date());

		status.setField("power","true");
//		statusChangeCallback.onEventFire("",status,id,new Date());

		System.in.read();
	}

	@Test
	public void testSchedule() throws InterruptedException, IOException {

		String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

		SimpleTriggerRecord record=new SimpleTriggerRecord();
		
		SingleThing thingID=new SingleThing();
		thingID.setThingID(1052l);

		record.setSource(thingID);

		record.addTarget(getTarget() );


		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);
		
		IntervalPrefix prefix=new IntervalPrefix();
		prefix.setTimeUnit(TimerUnitType.Second);
		prefix.setInterval(30);
		perdicate.setSchedule(prefix);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);

		record.setPredicate(perdicate);

		String triggerID=manager.createTrigger(record).getTriggerID();

		manager.enableTrigger(triggerID);

		sendGoodThingStatus(kiiThingID);

		Thread.sleep(40*1000);
		assertEquals(2,exec.getHitCount(triggerID));

		sendBadThingStatus(kiiThingID);

		Thread.sleep(40*1000);
		assertEquals(2,exec.getHitCount(triggerID));

		sendGoodThingStatus(kiiThingID);

		Thread.sleep(40*1000);
		assertEquals(4,exec.getHitCount(triggerID));

		System.in.read();

	}

	@Test
	public void testGroup(){


		String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

		GroupTriggerRecord record=new GroupTriggerRecord();

		record.addTarget(getTarget() );

		manager.createTrigger(record);


	}


	@Test
	public void testSimpleTrigger(){

		String kiiThingID="0af7a7e7-th.f83120e36100-a269-5e11-e5bb-0bc2e136";

		SimpleTriggerRecord record=new SimpleTriggerRecord();
		
		SingleThing thingID=new SingleThing();
		thingID.setThingID(1052l);

		record.setSource(thingID);

		record.addTarget(getTarget() );

		RuleEnginePredicate perdicate=new RuleEnginePredicate();
		Condition condition= TriggerConditionBuilder.andCondition().great("foo",0).less("bar",0).getConditionInstance();
		perdicate.setCondition(condition);

		perdicate.setTriggersWhen(WhenType.CONDITION_TRUE);

		record.setPredicate(perdicate);

		String triggerID=manager.createTrigger(record).getTriggerID();

		manager.enableTrigger(triggerID);

		sendGoodThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendBadThingStatus(kiiThingID);

		assertEquals(1,exec.getHitCount(triggerID));

		sendGoodThingStatus(kiiThingID);

		assertEquals(2,exec.getHitCount(triggerID));

	}


}
