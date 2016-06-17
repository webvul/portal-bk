package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.MultiplesValueMap;
import com.kii.extension.ruleengine.drools.entity.ResultParam;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.SummaryValueMap;
import com.kii.extension.ruleengine.drools.entity.ThingCol;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerData;

@Component
public class DroolsTriggerService {

	@Autowired
	@Qualifier("cloudDroolsService")
	private DroolsRuleService cloudService;

	@Autowired
	@Qualifier("streamDroolsService")
	private DroolsRuleService streamService;

	@Autowired
	private CommandExec exec;



	private final Map<String, Trigger> triggerMap=new ConcurrentHashMap<>();

	private final Map<String,Map<String,ThingCol>> thingColMap =new ConcurrentHashMap<>();


	public void clear(){
		cloudService.clear();
		streamService.clear();
		triggerMap.clear();
		thingColMap.clear();
	}


	public  Map<String,Object> getEngineRuntime(){

		Map<String,Object> map=new HashMap<>();

		map.put("cloud",cloudService.getEngineEntitys());
		map.put("stream",streamService.getEngineEntitys());

		return map;

	}

	private DroolsRuleService getService(Trigger trigger){

		if(trigger.isStream()){
			return streamService;
		}else{
			return cloudService;
		}

	}

	public void addTrigger(Trigger triggerInput,String ruleContent,boolean withSchedule){


		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);

		if(withSchedule){
			ResultParam param=new ResultParam(trigger.getTriggerID());

			getService(trigger).addOrUpdateData(param);
		}


		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);

	}

	public void addMultipleTrigger(Trigger triggerInput,String ruleContent,boolean withSchedule){
		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);

		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);


		if(withSchedule){
			ResultParam param=new ResultParam(trigger.getTriggerID());
			getService(trigger).addOrUpdateData(param);
		}

		MultiplesValueMap map=new MultiplesValueMap();
		map.setTriggerID(trigger.getTriggerID());
		getService(trigger).addOrUpdateData(map);

	}

	public void addSummaryTrigger(Trigger triggerInput,String ruleContent){


		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);

		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);

		SummaryValueMap map=new SummaryValueMap();
		map.setTriggerID(trigger.getTriggerID());
		getService(trigger).addOrUpdateData(map);

	}


	public void addTriggerData(TriggerData data) {

		Trigger trigger=triggerMap.get(data.getTriggerID());

		getService(trigger).addOrUpdateData(data);

		if(data instanceof ThingCol) {

			thingColMap.computeIfAbsent(trigger.getTriggerID(), (id) -> new HashMap<>()).put(((ThingCol) data).getName(), (ThingCol)data);
		}
	}


	public void addSlideSummary(Summary summary,String drl) {

		Trigger trigger=triggerMap.get(summary.getTriggerID());

		getService(trigger).addOrUpdateData(summary);

		getService(trigger).addCondition("slide-rule"+summary.getTriggerID()+summary.getName(),drl);

		if(summary instanceof ThingCol) {

			thingColMap.computeIfAbsent(trigger.getTriggerID(), (id) -> new HashMap<>()).put(((ThingCol) summary).getName(), summary);
		}

//		thingColMap.computeIfAbsent(trigger.getTriggerID(),(id)->new HashMap<>()).put(summary.getSummaryField(),summary);

	}

	public void updateThingsWithName(String triggerID,String name,Set<String> newThings){

		Trigger trigger=triggerMap.get(triggerID);

		ThingCol data= thingColMap.get(triggerID).get(name);

		data.setThingCol(newThings);

		getService(trigger).addOrUpdateData(data);
	}

	public void removeTrigger(String triggerID){



		Trigger trigger=triggerMap.get(triggerID);

		getService(trigger).removeData(trigger);
		getService(trigger).removeCondition("rule"+triggerID);

		Map<String,ThingCol> map= thingColMap.remove(triggerID);
		if(map != null ){
			map.values().forEach(summary-> getService(trigger).removeData(summary));
		}
	}

	public void enableTrigger(String triggerID) {


		Trigger trigger=triggerMap.get(triggerID);

		trigger.setEnable(true);

		getService(trigger).rejectCurrThingID();
		getService(trigger).addOrUpdateData(trigger);

	}

	public void disableTrigger(String triggerID) {

		Trigger trigger=triggerMap.get(triggerID);

		trigger.setEnable(false);

		getService(trigger).addOrUpdateData(trigger);
	}

	public void setInitSign(boolean sign){
		cloudService.setInitSign(sign);
		streamService.setInitSign(sign);
	}

	public void initThingStatus(ThingStatusInRule newStatus){

		cloudService.addOrUpdateData(newStatus);
		streamService.addOrUpdateData(newStatus);
	}


	public void addThingStatus(ThingStatusInRule newStatus){

		cloudService.addOrUpdateData(newStatus);
		streamService.addOrUpdateData(newStatus);

		setCurrThingID(newStatus.getThingID());

		fireCondition();
	}

	public void addExternalValue(ExternalValues newValues){


		cloudService.addOrUpdateExternal(newValues);
		streamService.addOrUpdateExternal(newValues);

	}

	private void setCurrThingID(String fullThingID){

		cloudService.setCurrThingID(fullThingID);
		streamService.setCurrThingID(fullThingID);
	}

	public  void fireCondition(){

		cloudService.fireCondition();

		List<MatchResult> results=cloudService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID(),r));

		streamService.fireCondition();
		results=streamService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID(),r));

	}
	

}
