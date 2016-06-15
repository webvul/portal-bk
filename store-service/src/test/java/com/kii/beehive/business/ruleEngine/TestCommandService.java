package com.kii.beehive.business.ruleEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.ruleengine.CommandExecuteService;
import com.kii.beehive.portal.store.TestInit;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TargetAction;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

public class TestCommandService extends TestInit {

	@Autowired
	private CommandExecuteService cmdService;


	@Test
	public void doCmd(){

		TriggerRecord trigger=new SimpleTriggerRecord();
		ExecuteTarget target=new ExecuteTarget();

		TagSelector sele=new TagSelector();
		List<Long> things=new ArrayList<>();
		things.add(1018l);
		sele.setThingList(things);

		target.setSelector(sele);
		
		TargetAction action=new TargetAction();
		ThingCommand cmd=new ThingCommand();
		Action power=new Action();
		power.setField("foo","bar");

		cmd.addAction("foo",power);
		action.setCommand(cmd);
		target.setCommand(action);

		List<ExecuteTarget> targets=new ArrayList<>();
		targets.add(target);

		trigger.setTarget(targets);

		cmdService.doCommand(trigger,new HashMap<>());

	}

}
