package com.kii.extension.ruleengine.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.demo.Message;
import com.kii.extension.ruleengine.drools.entity.Group;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.SummaryValueMap;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;

//import com.kii.extension.ruleengine.StatelessRuleExecute;

public class TestDemo extends InitTest {

	private Logger log= LoggerFactory.getLogger(TestDemo.class);


	@Before
	public void init() throws IOException {


		for(int i=0;i<10;i++){

			updateThingState(String.valueOf(i));
		}
	}

	@Test
	public void testSummary() throws IOException {

//		for(int i=0;i<10;i++){
//			trigger.addThing(String.valueOf(i));
//		}
		String triggerID="100";

		Trigger trigger=new Trigger(triggerID);

//		trigger.setTriggerID(triggerID);
		trigger.setType(TriggerType.summary);

		ruleLoader.addOrUpdateData(trigger);

		Summary summary=new Summary();
		summary.setFieldName("foo");
		summary.setFunName("sum");
		summary.setTriggerID(triggerID);
		summary.setName("sum_foo");
		ruleLoader.addOrUpdateData(summary);


		Summary summary2=new Summary();
		summary2.setFieldName("bar");
		summary2.setFunName("sum");
		summary2.setTriggerID(triggerID);
		summary2.setName("sum_bar");
		ruleLoader.addOrUpdateData(summary2);

		SummaryValueMap map=new SummaryValueMap();
		map.setTriggerID(triggerID);
		ruleLoader.addOrUpdateData(map);

		ruleLoader.fireCondition();

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));

		ruleLoader.fireCondition();

		for(int i=0;i<20;i++){


			updateThingState(String.valueOf(i%10));


			ruleLoader.fireCondition();
		}
//
//		List<SummaryValueMap> summarys=ruleLoader.doQuery("match summary value");
//
//		System.out.println( "we have results "+summarys.size()+" valu:"+summarys.get(0).getTriggerID() );


	}

	private FactHandle currThingsHander;


	@Test
	public void testThingGroup() throws IOException {

		ruleLoader.addCondition("trigger",getDrlContent("triggerRule"));
		Map<Integer,Trigger> triggerMap=new HashMap<>();

		Map<String,Group> groupMap=new HashMap<>();

 		for(int i=0;i<10;i++){
			int id=i%3;
			Trigger trigger=triggerMap.computeIfAbsent(id,(key)->{
				Trigger t=new Trigger(String.valueOf(key));
//				t.setTriggerID();
				return t;
			});
			groupMap.computeIfAbsent(trigger.getTriggerID(),(key)->{

				Group group=new Group();
				group.setTriggerID(trigger.getTriggerID());
				return group;

			}).addThing(String.valueOf(i));
		}

		groupMap.values().forEach(t->{

//			t.setPolicy(TriggerGroupPolicyType.All);
			ruleLoader.addOrUpdateData(t);
		});
		triggerMap.forEach((k,v)->{
			ruleLoader.addOrUpdateData(v);
		});

		ruleLoader.fireCondition();

	}


	@Test
	public void testFire() throws IOException {

		// go !
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);

		ruleLoader.addOrUpdateData(message);

		ruleLoader.fireCondition();

//		System.in.read();



	}



}
