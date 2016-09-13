package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.MultiplesValueMap;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.ThingResult;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerData;
import com.kii.extension.ruleengine.drools.entity.TriggerType;

@Component
public class DroolsTriggerService {

	@Autowired
	@Qualifier("cloudDroolsService")
	private DroolsRuleService cloudService;


	private final Map<String, Trigger> triggerMap=new ConcurrentHashMap<>();

	private final Map<String,Map<String,Summary>> thingColMap =new ConcurrentHashMap<>();


	public void clear(){
		cloudService.clear();
		triggerMap.clear();
		thingColMap.clear();
	}


	public  Map<String,Object> getEngineRuntime(){

		Map<String,Object> map=new HashMap<>();

		map.put("cloud",cloudService.getEngineEntitys());

		return map;

	}

	private DroolsRuleService getService(Trigger trigger){

		if(trigger.isStream()){
			return null;
		}else{
			return cloudService;
		}

	}

	public void addTrigger(Trigger triggerInput,String ruleContent){


		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);

		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);

	}

	public void addMultipleTrigger(Trigger triggerInput,String ruleContent){
		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);

		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);

		MultiplesValueMap map=new MultiplesValueMap();
		map.setTriggerID(trigger.getTriggerID());
		getService(trigger).addOrUpdateData(map);

		ThingResult  result=new ThingResult(trigger.getTriggerID());
		getService(trigger).addOrUpdateData(result);

	}

	public void addTriggerData(TriggerData data) {

		Trigger trigger=triggerMap.get(data.getTriggerID());

		getService(trigger).addOrUpdateData(data);

		if(data instanceof Summary) {

			thingColMap.computeIfAbsent(trigger.getTriggerID(), (id) -> new HashMap<>()).put((data).getName(), (Summary) data);
		}
	}


	public void addSlideSummary(Summary summary,String drl) {

		Trigger trigger=triggerMap.get(summary.getTriggerID());

		getService(trigger).addOrUpdateData(summary);

		getService(trigger).addCondition("slide-rule"+summary.getTriggerID()+summary.getName(),drl);


		thingColMap.computeIfAbsent(trigger.getTriggerID(), (id) -> new HashMap<>()).put((summary).getName(), summary);

	}

	public void updateThingsWithName(String triggerID,String name,Set<String> newThings){

		Trigger trigger=triggerMap.get(triggerID);

		Summary data= thingColMap.get(triggerID).get(name);

		data.setThingCol(newThings);

		getService(trigger).addOrUpdateData(data);
	}

	public void removeTrigger(String triggerID){


		Trigger trigger=triggerMap.get(triggerID);



		Map<String,Summary> map= thingColMap.remove(triggerID);
		if(map != null ){
			map.values().forEach(summary-> getService(trigger).removeData(summary));
		}

		if(trigger.getType()== TriggerType.multiple) {

			MultiplesValueMap mulMap = new MultiplesValueMap();
			mulMap.setTriggerID(trigger.getTriggerID());
			getService(trigger).removeData(mulMap);

			ThingResult result = new ThingResult(trigger.getTriggerID());
			getService(trigger).removeData(result);
		}

		getService(trigger).removeData(trigger);
		getService(trigger).removeCondition("rule"+triggerID);

	}

	public void enableTrigger(String triggerID) {


		Trigger trigger=triggerMap.get(triggerID);

		trigger.setEnable(true);

		getService(trigger).addOrUpdateData(trigger);


	}

	public void disableTrigger(String triggerID) {

		Trigger trigger=triggerMap.get(triggerID);

		trigger.setEnable(false);

		getService(trigger).addOrUpdateData(trigger);

	}

	public void enterInit(){
		cloudService.enterInit();
	}

	public void leaveInit(){
		cloudService.leaveInit();
	}




	public void addThingStatus(ThingStatusInRule newStatus){


		cloudService.addOrUpdateData(newStatus);
		cloudService.inThing(newStatus.getThingID());

	}

	public void addExternalValue(ExternalValues newValues){


		cloudService.addOrUpdateExternal(newValues);
	}



}
