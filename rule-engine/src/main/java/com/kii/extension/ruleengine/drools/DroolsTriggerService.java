package com.kii.extension.ruleengine.drools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;
import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.MultiplesValueMap;
import com.kii.extension.ruleengine.drools.entity.ScheduleFire;
import com.kii.extension.ruleengine.drools.entity.SingleThing;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.SummaryResult;
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
	
	private Map<Integer,List<BusinessObjInRule>>  dataMap=new ConcurrentHashMap<>();
	
	private AtomicInteger index=new AtomicInteger(0);


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
	
	public void addTrigger(Trigger triggerInput,TriggerValues   instData,String ruleContent){


		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger.isStream());

		getService(trigger.getTriggerID()).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger.getTriggerID()).addOrUpdateData(trigger,true);
		
		getService(trigger.getTriggerID()).addOrUpdateData(instData,true);

	}

	public void addMultipleTrigger(Trigger triggerInput,TriggerValues  instData,String ruleContent){
		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger.isStream());

		getService(trigger.getTriggerID()).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger.getTriggerID()).addOrUpdateData(trigger,true);

		MultiplesValueMap map=new MultiplesValueMap();
		map.setTriggerID(trigger.getTriggerID());
		getService(trigger.getTriggerID()).addOrUpdateData(map,true);

		getService(trigger.getTriggerID()).addOrUpdateData(instData,true);
	}
	
	public void updateTriggerInstData(String triggerID, String key,Object value) {
		
		TriggerValues values=new TriggerValues(triggerID);
		values.setValues(Collections.singletonMap(key,value));
		
		getService(triggerID).addOrUpdateData(values,false);
	}

	public void addTriggerData(TriggerData data) {

		getService(data.getTriggerID()).addOrUpdateData(data,false);

	}
	
	public void addMockThing(String triggerID){
		
		SingleThing thing=new SingleThing();
		thing.setTriggerID(triggerID);
		addTriggerData(thing);
		
	}
	
	public void addMockSummary(String triggerID){
		
		Summary summary=new Summary();
		summary.setTriggerID(triggerID);
		summary.setName("_MOCK");
		
		getService(triggerID).addOrUpdateData(new SummaryResult(summary,0),false);
		
	}


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
		
		cloudService.addOrUpdateData(fire,false);

	}
	
	
	@Scheduled(fixedRate=900,initialDelay=10000)
	public void commitBusinessObjChange(){
		
		int oldIndex=index.getAndIncrement();
		
		List<BusinessObjInRule> dataList=dataMap.remove(oldIndex);
		
		if(dataList==null){
			return;
		}
		addThingStatus(dataList);
		
	}
	
	
	
	
	public void addBusinessObj(BusinessObjInRule newStatus){
		
		List<BusinessObjInRule> list = dataMap.computeIfAbsent(index.get(), (k) -> {
			return new ArrayList<>();
		});
		list.add(newStatus);
	}
	
	public void addContextObj(BusinessObjInRule newStatus){

		cloudService.addOrUpdateData(newStatus,true);
	}
	
	private void  addThingStatus(Collection<BusinessObjInRule> newStatusSet){
		
		Set<String> thIDs=new HashSet<>();
		Map<String,Map<String,Object>>  statusSet=new HashMap<>();
		
		newStatusSet.forEach(s->{
			cloudService.moveHistory(s.getThingID());
			cloudService.addOrUpdateData(s,false);
			thIDs.add(s.getThingID());
			statusSet.put(s.getThingID(),s.getValues());
		});
		
		
		ExternalValues newValues=new ExternalValues("runtime");
		newValues.addValue("currStatusCol",statusSet);
		
		cloudService.inThing(thIDs,newValues);

	}
	
	public void fireCurrTrigger(String triggerID) {
		
		cloudService.inFireTrigger(triggerID);
	}
	

}
