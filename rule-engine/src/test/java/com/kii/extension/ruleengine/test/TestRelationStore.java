package com.kii.extension.ruleengine.test;

import static junit.framework.TestCase.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.RelationStore;

public class TestRelationStore extends InitTest {

	@Autowired
	private RelationStore  store;

	@Test
	public void testStore(){


		Set<String> thList1=new HashSet<>();
		Set<String> thList2=new HashSet<>();
		Set<String> thList3=new HashSet<>();
		Set<String> thList4=new HashSet<>();


		for(int i=0;i<100;i++){

			if(i%3==0){
				thList1.add("th"+i);
			}
			if(i%7==0){
				thList2.add("th"+i);
			}
			if(i%13==0){
				thList3.add("th"+i);
			}

			thList4.add("th"+i);
		}


		Map<String,Set<String>> thingMap1=new HashMap<>();
		thingMap1.put("summary1",thList1);


		Map<String,Set<String>> thingMap2=new HashMap<>();
		thingMap2.put("summary1",thList2);
		thingMap2.put("summary2",thList3);

		Map<String,Set<String>> thingMap3=new HashMap<>();
		thingMap3.put("summary3",thList4);

		store.fillThingTriggerElemIndex(thingMap1,"trigger3");
		store.fillThingTriggerElemIndex(thingMap2,"trigger7");
		store.fillThingTriggerElemIndex(thingMap3,"triggerOther");


		assertEquals(1,store.getTriggerSetByThingID("th1").size());

		assertEquals(3,store.getTriggerSetByThingID("th39").size());

		assertEquals(2,store.getTriggerSetByThingID("th9").size());

		assertEquals(1,store.getTriggerSetByThingID("th50").size());


		Set<String> thNew=new HashSet<>();
		for(int i=0;i<10;i++) {
			thNew.add("th"+i);
		}
		Map<String,Set<String>> newMap=new HashMap<>();

		store.maintainThingTriggerIndex(thNew,"triggerOther","summary3");
		assertEquals(2,store.getTriggerSetByThingID("th39").size());

		assertEquals(2,store.getTriggerSetByThingID("th9").size());

		assertEquals(0,store.getTriggerSetByThingID("th50").size());

	}

}
