package com.kii.extension.ruleengine.test;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.ruleengine.RelationStore;

public class TestRelationStore extends InitTest {

	@Autowired
	private RelationStore  store;

	@Test
	public void testStore(){


		List<String> thList1=new ArrayList<>();
		List<String> thList2=new ArrayList<>();
		List<String> thList3=new ArrayList<>();
		List<String> thList4=new ArrayList<>();


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

		store.fillThingsTriggerIndex(thList1,"trigger3");
		store.fillThingsTriggerIndex(thList2,"trigger7");
		store.fillThingsTriggerIndex(thList3,"trigger13");
		store.fillThingsTriggerIndex(thList4,"triggerOther");


		assertEquals(1,store.getTriggerSetByThingID("th1").size());

		assertEquals(3,store.getTriggerSetByThingID("th39").size());

		assertEquals(2,store.getTriggerSetByThingID("th9").size());

		List<String> thNew=new ArrayList<>();
		for(int i=0;i<10;i++) {
			thNew.add("th"+i);
		}
		store.maintainThingTriggerIndex(thNew,"triggerOther");
		assertEquals(2,store.getTriggerSetByThingID("th39").size());

		assertEquals(2,store.getTriggerSetByThingID("th9").size());


	}

}
