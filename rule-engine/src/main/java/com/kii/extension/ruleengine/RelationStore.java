package com.kii.extension.ruleengine;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.stereotype.Component;

@Component
public class RelationStore {


	private Map<String,Set<String>> thingTriggerMap=new ConcurrentHashMap<>();


	public void fillThingTriggerIndex(String thingID,String triggerID){
		fillThingsTriggerIndex(Collections.singletonList(thingID),triggerID);
	}

	public void fillThingsTriggerIndex(Collection<String> thingSet, String triggerID){

		thingSet.forEach((th)->{

			Set<String>  triggerSet=new ConcurrentSkipListSet<>();
			triggerSet.add(triggerID);

			thingTriggerMap.merge(th,triggerSet,(oldV,v)-> {
						oldV.addAll(v);
						return oldV;
					}
			);

		});
	}


	public void maintainThingTriggerIndex(Collection<String>  thingSet,String triggerID){

		thingSet.forEach((th)->{

			Set<String>  triggerSet=new ConcurrentSkipListSet<>();

			thingTriggerMap.merge(th,triggerSet,(oldV,v)-> {
						oldV.remove(triggerID);
						return oldV;
					}
			);

		});
	}

}
