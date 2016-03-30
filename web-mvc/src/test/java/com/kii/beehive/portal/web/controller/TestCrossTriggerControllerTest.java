package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.extension.ruleengine.store.trigger.*;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Jason on 29/3/2016.
 */
public class TestCrossTriggerControllerTest extends WebTestTemplate {


	@Autowired
	private CrossTriggerController crossTriggerController;

	@Before
	public void before() {
		super.before();

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createTrigger() throws Exception {
		TagSelector ts = new TagSelector();
		Long[] thingArray = {1124L};
		ts.setThingList(Arrays.asList(thingArray));

		ThingCommand tc = new ThingCommand();
		tc.setTitle("JasonTestTitle");
		tc.setSchema("SmartLight");

		Action action = new Action();
		action.setField("power", false);
		tc.addAction("setPower", action);

		TargetAction ta = new TargetAction();
		ta.setCommand(tc);

		ExecuteTarget et = new ExecuteTarget();
		et.setSelector(ts);
		et.setCommand(ta);


		RuleEnginePredicate ruleEnginePredicate = new RuleEnginePredicate();
		Equal e = new Equal();
		e.setField("power");
		e.setValue(true);
		ruleEnginePredicate.setCondition(e);
		ruleEnginePredicate.setTriggersWhen(WhenType.CONDITION_CHANGED);

		SimpleTriggerRecord tr = new SimpleTriggerRecord();
		tr.setName("JasonTest");
		tr.setPredicate(ruleEnginePredicate);
		tr.addTarget(et);
		SimpleTriggerRecord.ThingID tId = new SimpleTriggerRecord.ThingID();
		tId.setThingID(1081L);
		tr.setSource(tId);
		Map<String, Object> result = crossTriggerController.createTrigger(tr);

		String triggerID = result.get("triggerID").toString();

		System.out.println(triggerID);

		TriggerRecord triggerRecord = crossTriggerController.getTriggerById(triggerID);

		assertEquals(triggerRecord.getTriggerID(), triggerID);
		assertEquals(triggerRecord.getName(), tr.getName());
	}
}