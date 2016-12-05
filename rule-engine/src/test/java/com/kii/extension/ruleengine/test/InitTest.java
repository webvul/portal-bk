package com.kii.extension.ruleengine.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.drools.CommandExec;
import com.kii.extension.ruleengine.drools.DroolsRuleService;
import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.SingleThing;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:./ruleConsoleCtx.xml"})
@Ignore
public class InitTest {


	@Autowired
	@Qualifier("cloudDroolsService")
	protected DroolsRuleService ruleLoader;

	@Autowired
	protected CommandExec exec;
//
//


	@Autowired
	protected ResourceLoader loader;

	CurrThing curr=new CurrThing();

	Map<String,Object> paramOk=new HashMap<>();

	public InitTest(){

		paramOk.put("foo",100);
		paramOk.put("bar",-10);

		paramNo.put("foo",-100);
		paramNo.put("bar",10);

	}


	protected boolean isMatch(String triggerID){


//		List<MatchResult> results=ruleLoader.doQuery("get Match Result by TriggerID");
//
//
//		return results.stream().filter(  (r)->r.getTriggerID()==triggerID ).findAny().isPresent();


		return true;
	}


	Map<String,Object> paramNo=new HashMap<>();


	protected void initGlobal() throws IOException {


		ruleLoader.addOrUpdateData(curr,true);

		ruleLoader.bindWithInstance("exec",exec);

	}

	protected String getDrlContent(String fileName) {

		try {
			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}




	protected  void updateThingState(String thingID,Map<String,Object> values){
		ThingStatusInRule status=new ThingStatusInRule(thingID);
//		status.setThingID(thingID);
		status.setCreateAt(new Date());
		status.setValues(new HashMap<>(values));

		addThingStatus(thingID, status);

	}

	private void addThingStatus(String thingID, ThingStatusInRule status) {


		ruleLoader.addOrUpdateData(status,true);


		ruleLoader.addOrUpdateData(curr,true);

	}

	protected void updateThingState(String thingID){

		ThingStatusInRule status=new ThingStatusInRule(thingID);
//		status.setThingID(thingID);

		status.addValue("foo",Math.random()*100-50);
		status.addValue("bar",Math.random()*10-5);

		addThingStatus(thingID, status);

	}

	protected void addThing(String id,String thingID) {
		SingleThing thing=new SingleThing();
		thing.setThingID(thingID);
		thing.setTriggerID(id);

		ruleLoader.addOrUpdateData(thing,true);
	}
}
