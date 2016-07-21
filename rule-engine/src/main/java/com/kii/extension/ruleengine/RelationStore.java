package com.kii.extension.ruleengine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.stereotype.Component;

@Component
public class RelationStore {


	private Map<String,Set<String>> thingTriggerMap=new ConcurrentHashMap<>();

	private Map<TriggerInfo,Set<String>>  thingMap=new ConcurrentHashMap<>();


	private static class TriggerInfo{


		TriggerInfo(String triggerID){
			this.triggerID=triggerID;
			this.elemName=null;
		}

		TriggerInfo(String triggerID,String elemName){
			this.triggerID=triggerID;
			this.elemName=elemName;
		}

		String triggerID;

		String elemName;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			TriggerInfo that = (TriggerInfo) o;
			return Objects.equals(triggerID, that.triggerID) &&
					Objects.equals(elemName, that.elemName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(triggerID, elemName);
		}
	}



	public void fillThingTriggerIndex(String thingID,String triggerID){


		fillThingsTriggerIndex(Collections.singleton(thingID),new TriggerInfo(triggerID));
	}


	public void fillThingTriggerElemIndex(Map<String,Set<String>> thingMap,String triggerID){


		thingMap.forEach((k,v)->{

			fillThingsTriggerIndex(v,new TriggerInfo(triggerID,k));

		});

	}


	private void fillThingsTriggerIndex(Set<String> thingSet, TriggerInfo triggerID){



		thingSet.forEach((th)->{

			Set<String>  triggerSet=new ConcurrentSkipListSet<>();
			triggerSet.add(triggerID.triggerID);

			thingTriggerMap.merge(th,triggerSet,(oldV,v)-> {
						oldV.addAll(v);
						return oldV;
					}
			);

		});

		thingMap.put(triggerID,thingSet);
	}

	public void maintainThingTriggerIndex(Set<String>  thingSet,String triggerID) {
		maintainThingTriggerIndex(thingSet,triggerID,null);
	}

		public void maintainThingTriggerIndex(Set<String>  thingSet,String triggerID,String elemID){


		TriggerInfo triggerInfo=new TriggerInfo(triggerID,elemID);

		Set<String> oldThingSet=thingMap.put(triggerInfo,thingSet);

		Set<String> copyThingSet=new HashSet<>(oldThingSet);

		oldThingSet.removeAll(thingSet);

		oldThingSet.forEach((th)->{

			thingTriggerMap.get(th).remove(triggerID);
		});

		thingSet.removeAll(copyThingSet);

		thingSet.forEach((th)->{

			Set<String>  triggerSet=new ConcurrentSkipListSet<>();

			thingTriggerMap.merge(th,triggerSet,(oldV,v)-> {
						oldV.remove(triggerID);
						return oldV;
					}
			);

		});


	}

	public Set<String> getTriggerSetByThingID(String thingID){


		return thingTriggerMap.get(thingID);
	}

}
