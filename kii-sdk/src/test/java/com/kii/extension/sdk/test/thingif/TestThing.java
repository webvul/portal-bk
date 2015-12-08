package com.kii.extension.sdk.test.thingif;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.factory.LocalPropertyBindTool;
import com.kii.extension.sdk.context.AdminTokenBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.LayoutPosition;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.Predicate;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.TargetCommand;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.ThingTrigger;
import com.kii.extension.sdk.entity.thingif.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.entity.thingif.conditions.EqualTriggerCondition;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.ThingIFService;
import com.kii.extension.sdk.service.TriggerService;
import com.kii.extension.sdk.test.TestTemplate;

public class TestThing extends TestTemplate {

	@Autowired
	private ThingIFService  service;

	@Autowired
	private TriggerService triggerService;

	@Autowired
	private AppBindToolResolver bindTool;

	@Autowired
	private TokenBindToolResolver tokenResolver;

	@Before
	public void before(){

		bindTool.setAppName("test-slave-3");
	}

	@Test
	public void testThingAdd(){

		OnBoardingParam param=new OnBoardingParam();

		param.setVendorThingID("test-trigger-id-00001");

		param.setLayoutPosition(LayoutPosition.STANDALONE);

		param.setThingPassword("qwerty");
		param.setThingType("demo");

		service.onBoarding(param);
	}

	private String thingID="th.f83120e36100-2cc9-5e11-b6d9-02c968ae";

	private String thingID2="th.f83120e36100-2cc9-5e11-e7d9-08e1b5e9";

	private String userID="f83120e36100-2cc9-5e11-44a9-045a59eb";
	@Test
	public void testThingSendCmd(){


		ThingCommand cmd=new ThingCommand();

		cmd.setUserID(userID);
		cmd.addMetadata("foo","bar");
		Action action=new Action();
		action.setField("power",true);
		action.setField("lightness",50);

		cmd.addAction("open",action);
		cmd.setSchema("demo");
		cmd.setSchemaVersion(0);

		service.sendCommand(thingID,cmd);
	}

	@Test
	public void testRegistTrigger(){

		ThingTrigger trigger=new ThingTrigger();

		trigger.addMetadata("foo","bar");
		trigger.setTarget(TriggerTarget.COMMAND);

		trigger.setTitle("test-light");

		TargetCommand command=new TargetCommand();
		command.setUserID(userID);
		command.setThingID(thingID);
		Action action=new Action();
		action.setField("power",true);
		action.setField("lightness",99);
		command.addAction("trigger",action);
		command.addMetadata("source","trigger");
		command.setSchema("demo");

		trigger.setCommand(command);

		StatePredicate predicate =new StatePredicate();
		predicate.setTriggersWhen(TriggerWhen.CONDITION_CHANGED);

		Condition condition=ConditionBuilder.newCondition().great("temperature",100).getConditionInstance();
		predicate.setCondition(condition);

		trigger.setPredicate(predicate);

		triggerService.createTrigger(thingID2,trigger);


	}

	private String triggerID="9ff1ed50-9d8d-11e5-9cc2-00163e02138f";

	@Test
	public void fireTrigger(){


		ThingStatus status=new ThingStatus();
		for(int i=0;i<10;i++) {
			if(i%2==0) {
				status.setField("temperature", 100 + i);
			}else{
				status.setField("temperature", 100 - i);

			}
			service.putStatus(thingID2, status);
		}
	}

}
