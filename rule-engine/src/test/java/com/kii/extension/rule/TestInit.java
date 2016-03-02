package com.kii.extension.rule;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.store.trigger.TargetAction;
import com.kii.extension.store.trigger.ExecuteTarget;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:scheduleCtx.xml"})
public class TestInit {


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
}
