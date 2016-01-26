package com.kii.beehive.business;

import static junit.framework.TestCase.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.event.process.BusinessTriggerFireProcess;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.AppInfoManager;
import com.kii.beehive.portal.manager.TagThingManager;
import com.kii.beehive.portal.manager.ThingGroupStateManager;
import com.kii.beehive.portal.manager.ThingStateManager;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerGroupPolicy;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;

//import com.kii.beehive.business.service.TriggerFireCallbackService;

@Transactional

public class TestGroupTrigger extends TestTemplate{

	private Logger log= LoggerFactory.getLogger(TestGroupTrigger.class);

	private Long[] thingIDs={1052l,1054l,1055l,1056l,1057l};

	private String appName="b8ca23d0";

	private Long[] tags={311l,312l,313l,314l,315l};

	private String[] tagNames={"Custom-name0","Custom-name1","Custom-name2","Custom-name3","Custom-name4"};

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ThingIFInAppService thingIFService;


	@Autowired
	private ThingGroupStateManager groupMang;

	@Autowired
	private TagThingManager tagManager;


	@Autowired
	private ThingStateManager thingTagService;



	@Autowired
	private BusinessEventBus eventBus;


	@Autowired
	private AppInfoManager appManager;

	@Commit
	@Test
	public void initThing(){



		for(int i=2;i<6;i++) {
			String vendorID = "a1b2c3d4e5f6"+i;

			OnBoardingParam param = new OnBoardingParam();
			param.setVendorThingID(vendorID);
			param.setThingPassword("12345678");

			OnBoardingResult result = thingIFService.onBoarding(param, appName);


			thingTagService.updateKiicloudRelation(vendorID, appName + "-" + result.getThingID());
		}
	}


	@Test
	public void sendState(){


		for(long thingID:thingIDs) {

			GlobalThingInfo thingInfo = thingTagService.getThingByID(thingID);


			ThingStatus status = new ThingStatus();
			status.setField("foo", -100);
			status.setField("bar",125);

			eventBus.onStatusUploadFire(thingInfo.getFullKiiThingID(),status);
		}
	}

	private String triggerID="6a7337b0-b38b-11e5-8554-00163e007aba";

	@Autowired
	private BusinessTriggerFireProcess process;

	@Test
	public void callback(){

		EventListener  listener=new EventListener();
		listener.setTargetKey(triggerID);
//		listener.set


		process.onEventFire(listener,"c1744915-th.aba700e36100-4558-5e11-6d8b-053cc8e8",TriggerWhen.CONDITION_TRUE_TO_FALSE,true);


//
//		TagSelector selector=new TagSelector();
//		selector.addTag(tagNames[1]);
//		selector.addTag(tagNames[2]);
//		selector.addTag(tagNames[3]);
//
//		List<GlobalThingInfo> things=thingTagService.getThingInfos(selector);
//
//
//		for(GlobalThingInfo th:things) {
//			if(Math.random()>0.5f) {
//				callbackService.onNegativeArrive(th.getFullKiiThingID(),triggerID);
//			}else{
//				callbackService.onPositiveArrive(th.getFullKiiThingID(),triggerID);
//			}
//		}
	}

	@Test
	public void createTrigger() throws IOException {
		
		GroupTriggerRecord record=new GroupTriggerRecord();
		record.addTarget(getTagCmdTarget());

		StatePredicate predicate=new StatePredicate();
		Condition condition= ConditionBuilder.orCondition().less("bar",100).great("foo",0).getConditionInstance();

		predicate.setCondition(condition);
		predicate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);
		record.setPredicate(predicate);

		TriggerGroupPolicy policy=new TriggerGroupPolicy();
		policy.setCriticalNumber(75);
		policy.setGroupPolicy(TriggerGroupPolicy.TriggerGroupPolicyType.Percent);

		record.setPolicy(policy);
		
		TriggerSource source=new TriggerSource();
		TagSelector selector=new TagSelector();
		selector.addTag("Location-3F-room1");
		selector.addTag("Location-2F-room1");

		source.setSelector(selector);
		record.setSource(source);

		log.info(mapper.writeValueAsString(record));

		TriggerRecord rec=mapper.readValue(mapper.writeValueAsBytes(record),TriggerRecord.class);

		assertTrue(rec instanceof GroupTriggerRecord);


		groupMang.createThingGroup(record);


	}



	private TriggerTarget getTagCmdTarget() {
		TriggerTarget target=new TriggerTarget();

		TagSelector selector=new TagSelector();

		selector.addTag(tagNames[2]);
		selector.addTag(tagNames[3]);
		selector.setAndExpress(true);

		TargetAction action = getTargetAction("powerOn","power",true);
		target.setCommand(action);
		return target;
	}

	private TargetAction getTargetAction(String name,String actName,Object value) {
		TargetAction action=new TargetAction();
		ThingCommand cmd=new ThingCommand();

		Action act=new Action();
		act.setField(actName,value);

		cmd.addAction(name,act);
		action.setCommand(cmd);
		return action;
	}

}
