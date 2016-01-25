package com.kii.beehive.business;

import static junit.framework.TestCase.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.TagThingManager;
import com.kii.beehive.portal.manager.ThingGroupStateManager;
import com.kii.beehive.portal.manager.ThingTagManager;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerGroupPolicy;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.Action;
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

	private Long[] thingIDs={575l,576l,577l,578l,579l,580l,581l,582l,583l,584l};

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
	private ThingTagManager thingTagService;

//	@Autowired
//	private TriggerFireCallbackService callbackService;



	@Autowired
	private TagThingManager thingTagManager;


	@Autowired
	private BusinessEventBus eventBus;


//	@Commit
//	@Test
//	public void fireTagChange() throws IOException {
//
//		List<Long> tagIDList=new ArrayList<>();
//		tagIDList.add(1001l);
//		tagIDList.add(1041l);
//
//
//		List<Long> thingIDList=new ArrayList<>();
//		thingIDList.add(1052l);
//		thingIDList.add(1054l);
//		thingIDList.add(1055l);
//		thingIDList.add(1056l);
//
//
////		thingTagManager.bindTagToThing(tagIDList, thingIDList);
//
//		eventBus.onTagIDsChangeFire(tagIDList,true);
////
////
//		System.in.read();
//
////		tagManager.unbindTagToThing(Collections.singletonList(String.valueOf(tags[3])),thingIDs[0]);
//
//	}



	@Test
	public void sendState(){


		for(long thingID:thingIDs) {

			GlobalThingInfo thingInfo = thingTagService.getThingByID(thingIDs[8]);


			ThingStatus status = new ThingStatus();
			status.setField("foo", -100);
			status.setField("bar",125);

			thingIFService.putStatus(thingInfo.getFullKiiThingID(), status);
		}
	}

	private String triggerID="6a7337b0-b38b-11e5-8554-00163e007aba";
//	@Test
//	public void callback(){
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
//	}

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
