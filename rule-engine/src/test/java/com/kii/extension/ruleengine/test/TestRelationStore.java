package com.kii.extension.ruleengine.test;

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

		}

		store.fillThingsTriggerIndex(thList1,"trigger3");
		store.fillThingsTriggerIndex(thList2,"trigger7");
		store.fillThingsTriggerIndex(thList3,"trigger13");




	}

}
