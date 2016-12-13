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
import com.kii.extension.ruleengine.drools.entity.ScheduleFire;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.ThingResult;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerData;
import com.kii.extension.ruleengine.drools.entity.TriggerValues;
import com.kii.extension.ruleengine.drools.entity.WithTrigger;

@Component
public class DroolsTriggerService {

	@Autowired
	@Qualifier("cloudDroolsService")
	private DroolsService cloudService;


	private final Map<String, Boolean> triggerMap=new ConcurrentHashMap<>();



	public  Map<String,Object> getEngineRuntime(String triggerID){

		Map<String,Object> map=new HashMap<>();

		map.put("cloud",cloudService.getEngineEntitys(triggerID));

		return map;

	}

	private DroolsService getService(String triggerID){

		if(triggerMap.get(triggerID)){
			return null;
		}else{
			return cloudService;
		}

	}

	public void addTrigger(Trigger triggerInput,String ruleContent){


		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger.isStream());

		getService(trigger.getTriggerID()).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger.getTriggerID()).addOrUpdateData(trigger,true);

		TriggerValues  value=new TriggerValues(trigger.getTriggerID());
		getService(trigger.getTriggerID()).addOrUpdateData(value,true);

	}

	public void addMultipleTrigger(Trigger triggerInput,String ruleContent){
		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger.isStream());

		getService(trigger.getTriggerID()).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger.getTriggerID()).addOrUpdateData(trigger,true);

		MultiplesValueMap map=new MultiplesValueMap();
		map.setTriggerID(trigger.getTriggerID());
		getService(trigger.getTriggerID()).addOrUpdateData(map,true);

		ThingResult  result=new ThingResult(trigger.getTriggerID());
		getService(trigger.getTriggerID()).addOrUpdateData(result,true);


		TriggerValues  value=new TriggerValues(trigger.getTriggerID());
		getService(trigger.getTriggerID()).addOrUpdateData(value,true);
	}

	public void addTriggerData(TriggerData data) {


		getService(data.getTriggerID()).addOrUpdateData(data,false);

	}


//	public void addSlideSummary(Summary summary,String drl) {
//
////		Trigger trigger=triggerMap.get(summary.getTriggerID());
//
//
//		getService(summary.getTriggerID()).addOrUpdateData(summary,true);
//
////		getService(summary.getTriggerID()).addCondition("slide-rule"+summary.getTriggerID()+summary.getName(),drl);
//
//
//	}

	public void updateThingsWithName(String triggerID,String name,Set<String> newThings){


		Summary data= new Summary();
		data.setTriggerID(triggerID);
		data.setName(name);
		data.setThingCol(newThings);

		getService(triggerID).addOrUpdateData(data,false);

	}

	public void removeTrigger(String triggerID){


		Trigger trigger=new Trigger(triggerID);

		getService(triggerID).removeData(trigger);

		getService(triggerID).removeFact(	(o)->{
			if(o instanceof WithTrigger){
				return ((WithTrigger)o).getTriggerID().equals(triggerID);
			}
			return false;
		});

		getService(triggerID).removeCondition("rule"+triggerID);

	}

	public void enableTrigger(String triggerID) {


		Trigger trigger=new Trigger(triggerID);

		trigger.setEnable(true);

		getService(triggerID).addOrUpdateData(trigger,false);


	}

	public void disableTrigger(String triggerID) {

		Trigger trigger=new Trigger(triggerID);

		trigger.setEnable(false);

		getService(triggerID).addOrUpdateData(trigger,false);

	}

	public void enterInit(){
		cloudService.enterInit();
	}

	public void leaveInit(){
		cloudService.leaveInit();
	}



	public void updateScheduleSign(String triggerID){

		ScheduleFire fire=new ScheduleFire();
		fire.setTriggerID(triggerID);
		fire.setEnable(true);

		cloudService.updateScheduleData(fire);


	}


	public void addThingStatus(ThingStatusInRule newStatus){
		
		cloudService.moveHistory(newStatus.getThingID());
		
		cloudService.addOrUpdateData(newStatus,false);
		
		ExternalValues newValues=new ExternalValues("runtime");
		newValues.addValue("currStatus",newStatus.getValues());
		
		cloudService.addOrUpdateExternal(newValues);
		
		cloudService.inThing(newStatus.getThingID());

	}

	public void addExternalValue(ExternalValues newValues){


		cloudService.addOrUpdateExternal(newValues);
	}
	
	
	public void fireCurrTrigger(String triggerID) {
		
		cloudService.inFireTrigger(triggerID);
	}
}
