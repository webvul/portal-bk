package com.kii.beehive.business;

import java.util.Collections;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.business.manager.SimpleThingTriggerManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.manager.ThingGroupStateManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.service.TriggerFireCallbackService;
import com.kii.beehive.portal.store.entity.trigger.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerGroupPolicy;
import com.kii.beehive.portal.store.entity.trigger.TriggerSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;

@Transactional

public class TestGroupTrigger extends TestTemplate{

	private Logger log= LoggerFactory.getLogger(TestGroupTrigger.class);

	private Long[] thingIDs={575l,576l,577l,578l,579l,580l,581l,582l,583l,584l};

	private String appName="b8ca23d0";

	private Long[] tags={311l,312l,313l,314l,315l};

	private String[] tagNames={"Custom-name0","Custom-name1","Custom-name2","Custom-name3","Custom-name4"};



	@Autowired
	private TriggerFireCallbackService callbackService;


	@Autowired
	private ThingIFInAppService thingIFService;


	@Autowired
	private ThingGroupStateManager groupMang;

	@Autowired
	private TagThingManager tagManager;

	@Test
	public void fireTagChange(){

		tagManager.bindTagToThing(Collections.singletonList(String.valueOf(tags[3])),thingIDs[0]);

	}


	@Test
	public void createTrigger(){
		
		GroupTriggerRecord record=new GroupTriggerRecord();
		record.addTarget(getTagCmdTarget());

		StatePredicate preidcate=new StatePredicate();
		Condition condition= ConditionBuilder.orCondition().less("bar",100).great("foo",0).getConditionInstance();
		preidcate.setCondition(condition);
		preidcate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);
		record.setPerdicate(preidcate);

		TriggerGroupPolicy policy=new TriggerGroupPolicy();
		policy.setCriticalNumber(75);
		policy.setGroupPolicy(TriggerGroupPolicy.TriggerGroupPolicyType.Percent);

		record.setPolicy(policy);
		
		TriggerSource source=new TriggerSource();
		TagSelector selector=new TagSelector();
		selector.addTag(tagNames[1]);
		selector.addTag(tagNames[2]);
		selector.addTag(tagNames[3]);

		source.setSelector(selector);
		record.setSource(source);

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
