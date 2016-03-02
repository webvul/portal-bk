package com.kii.extension.rule;


import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.EngineService;
import com.kii.extension.drools.CommandExec;
import com.kii.extension.store.trigger.SimpleTriggerRecord;

public class TestSimpleTrigger extends TestInit{



	@Autowired
	private EngineService service;


	@Autowired
	private CommandExec  exec;


	public void testSimpleTrigger(){

		SimpleTriggerRecord record=new SimpleTriggerRecord();

		SimpleTriggerRecord.ThingID thingID=new SimpleTriggerRecord.ThingID();
		thingID.setThingID(1049);

		record.setSource(thingID);

		record.addTarget(getTarget() );



	}
}
