package com.kii.extension.sdk.test.thingif;

import static junit.framework.TestCase.assertEquals;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.factory.LocalPropertyBindTool;
import com.kii.extension.sdk.context.AdminTokenBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.FederatedAuthResult;
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
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.FederatedAuthService;
import com.kii.extension.sdk.service.ThingIFService;
import com.kii.extension.sdk.service.TriggerService;
import com.kii.extension.sdk.test.TestTemplate;

public class TestThing extends TestTemplate {

	public static final String KEY_FIELD = "temperature";
	@Autowired
	private ThingIFService  service;

	@Autowired
	private TriggerService triggerService;

	@Autowired
	private AppBindToolResolver bindTool;

	@Autowired
	private TokenBindToolResolver tokenResolver;


	@Autowired
	private FederatedAuthService federService;

	private static  String DEFAULT_NAME="default_owner_id";

	private static String DEFAULT_PWD= DigestUtils.sha1Hex(DEFAULT_NAME+"_default_owner_beehive");


	private static String appID="test-slave-3";

//	private FederatedAuthResult result;


	private String token="a6l-d5HbfnEHu9FB3MWmgVuBqzldiHZzP77iEc0QY4M";

	private String userID="f83120e36100-2cc9-5e11-44a9-045a59eb";
	@Before
	public void before(){

//		result=federService.loginSalveApp(appID,DEFAULT_NAME,DEFAULT_PWD);
		bindTool.setAppName(appID);

		tokenResolver.bindToken(token);
	}

	private String triggerThingID="th.f83120e36100-2cc9-5e11-e7d9-08e1b5e9";

	private String testThingID;

	private String triggerID="a8b20b40-9e24-11e5-a93c-00163e007aba";

	@Test
	public void testThingAdd(){

		OnBoardingParam param=new OnBoardingParam();

		param.setVendorThingID("test-business-id-00001");

		param.setLayoutPosition(LayoutPosition.STANDALONE);

		param.setThingPassword("qwerty");
		param.setThingType("demo");
		param.setUserID(userID);
		param.addThingProperty("foo","bar");



		triggerThingID=service.onBoarding(param).getThingID();
	}

	@Test
	public void testThingSendCmd(){

//
//		ThingCommand cmd=new ThingCommand();
//
//		cmd.setUserID(result.getUserID());
//		cmd.addMetadata("foo","bar");
//		Action action=new Action();
//		action.setField("power",true);
//		action.setField("lightness",50);
//
//		cmd.addAction("open",action);
//		cmd.setSchema("demo");
//		cmd.setSchemaVersion(0);

		service.sendCommand(triggerThingID,getTargetCommand());
	}

	@Test
	public void testRegistTrigger(){

		ThingTrigger trigger=new ThingTrigger();

		trigger.setDescription("desc");

		trigger.addMetadata("foo","bar");
		trigger.setTarget(TriggerTarget.COMMAND);

		trigger.setTitle("test-light");

		TargetCommand command = getTargetCommand();

		trigger.setCommand(command);

		StatePredicate predicate =new StatePredicate();
		predicate.setTriggersWhen(TriggerWhen.CONDITION_CHANGED);

		Condition condition=ConditionBuilder.newCondition().equal(KEY_FIELD,100).getConditionInstance();
		predicate.setCondition(condition);

		trigger.setPredicate(predicate);

		triggerID=triggerService.createTrigger(triggerThingID,trigger);

	}

	@Test
	public void updateTrigger(){

		ThingTrigger trigger=new ThingTrigger();

		trigger.addMetadata("foo","bar");
		trigger.setTarget(TriggerTarget.COMMAND);

		trigger.setTitle("test-light");

		TargetCommand command = getTargetCommand();

		trigger.setCommand(command);

		StatePredicate predicate =new StatePredicate();
		predicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);
		Condition condition=ConditionBuilder.newCondition().great("temperature",100).getConditionInstance();
		predicate.setCondition(condition);

		trigger.setPredicate(predicate);


		triggerService.updateTrigger(triggerThingID,triggerID,trigger);
	}

	private TargetCommand getTargetCommand() {
		TargetCommand command=new TargetCommand();
		command.setUserID(userID);
		command.setThingID(triggerThingID);
		Action action=new Action();
		action.setField("power",true);
		action.setField("lightness",99);
		command.addAction("business",action);
		command.addMetadata("source","business");
		command.setSchema("demo");

		return command;
	}


	@Test
	public void fireTrigger(){


		ThingStatus status=new ThingStatus();
		for(int i=0;i<10;i++) {
			if(i%2==0) {
				status.setField(KEY_FIELD, 100 + i);
			}else{
				status.setField(KEY_FIELD, 100 - i);

			}
			service.putStatus(triggerThingID, status);

			ThingStatus resultStatus=service.getStatus(triggerThingID);
			assertEquals(resultStatus.getFields().get(KEY_FIELD),status.getFields().get(KEY_FIELD));
		}
	}



}
