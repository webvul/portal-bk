package com.kii.extension.test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.demo.Applicant;
import com.kii.extension.ruleengine.demo.Fire;
import com.kii.extension.ruleengine.demo.Message;
import com.kii.extension.ruleengine.demo.Room;
import com.kii.extension.ruleengine.demo.Sprinkler;
import com.kii.extension.ruleengine.thingtrigger.Summary;
import com.kii.extension.ruleengine.thingtrigger.SummaryValueMap;
import com.kii.extension.ruleengine.thingtrigger.Trigger;

public class TestDemo extends InitTest {

	private Logger log= LoggerFactory.getLogger(TestDemo.class);



	@Before
	public void init() throws IOException {


		ruleLoader.initCondition(
				getDrlContent("demo"),
				getDrlContent("FireAlarm"),
				getDrlContent("group")
		);

		execute.initCondition(getDrlContent("demo1"));


		for(int i=0;i<10;i++){

			updateThingState(String.valueOf(i));
		}
	}

	@Test
	public void testSummary() throws IOException {

		Trigger trigger=new Trigger();
		for(int i=0;i<10;i++){
			trigger.addThing(String.valueOf(i));
		}
		trigger.setType("summary");
		trigger.setTriggerID(100);

		ruleLoader.addData(trigger);

		Summary summary=new Summary();
		summary.setFieldName("foo");
		summary.setFunName("sum");
		summary.setTriggerID(100);
		summary.setSummaryField("sum_foo");
		ruleLoader.addData(summary);


		Summary summary2=new Summary();
		summary2.setFieldName("bar");
		summary2.setFunName("sum");
		summary2.setTriggerID(100);
		summary2.setSummaryField("sum_bar");
		ruleLoader.addData(summary2);

		SummaryValueMap map=new SummaryValueMap();
		map.setTriggerID(100);
		ruleLoader.addData(map);

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

		for(int i=0;i<10;i++){
			int id=i%3;
			triggerMap.computeIfAbsent(id,(key)->{
				Trigger t=new Trigger();
				t.setTriggerID(key);
				return t;
			}).addThing(String.valueOf(i));
		}

		triggerMap.values().forEach(t->{
			t.setType("all");
			ruleLoader.addData(t);
		});

		ruleLoader.fireCondition();

	}

	@Test
	public void testFireAlarm(){

		String[] names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
		Map<String,Room> name2room = new HashMap<String,Room>();
		for( String name: names ){
			Room room = new Room();
			room.setName(name);
			name2room.put( name, room );
			ruleLoader.addData(room);

			Sprinkler sprinkler = new Sprinkler( );
			sprinkler.setOn(false);
			sprinkler.setRoom(room);

			ruleLoader.addData( sprinkler );
		}

		ruleLoader.fireCondition();

		Fire newFire=new Fire();
		newFire.setRoom(name2room.get(names[0]));

		FactHandle holder=ruleLoader.addData(newFire);

		ruleLoader.fireCondition();

		ruleLoader.removeData(holder);

		ruleLoader.fireCondition();
	}

	@Test
	public void testExecute() throws IOException{

		Applicant applicant = new Applicant();
		applicant.setAge(16);
		applicant.setName("agent.smith");
		applicant.setValid(true);

		Applicant  result=execute.doExecute(applicant);

		assertFalse(applicant.isValid());

		applicant.setAge(19);
		applicant.setValid(true);

		execute.doExecute(applicant);

		assertTrue(applicant.isValid());

	}

	@Test
	public void testFire() throws IOException {

		// go !
		Message message = new Message();
		message.setMessage("Hello World");
		message.setStatus(Message.HELLO);

		ruleLoader.addData(message);

		ruleLoader.fireCondition();

//		System.in.read();



	}
}
