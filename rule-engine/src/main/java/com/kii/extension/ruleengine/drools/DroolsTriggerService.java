package com.kii.extension.ruleengine.drools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.SummaryValueMap;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;

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

	private final Map<String,Map<String,Summary>>  summaryMap=new ConcurrentHashMap<>();


	/**
	 * 清空 drools 相关 重新初始化
	 */
	public void clear(){
		cloudService.clear();
		streamService.clear();
		triggerMap.clear();
		summaryMap.clear();
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

	public void addTrigger(Trigger triggerInput,String ruleContent){


		Trigger trigger=new Trigger(triggerInput);
		triggerMap.put(trigger.getTriggerID(),trigger);


		getService(trigger).addCondition("rule"+trigger.getTriggerID(),ruleContent);

		getService(trigger).addOrUpdateData(trigger);

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



	public void addSummary(Summary summary) {

		Trigger trigger=triggerMap.get(summary.getTriggerID());

		getService(trigger).addOrUpdateData(summary);

		summaryMap.computeIfAbsent(trigger.getTriggerID(),(id)->new HashMap<>()).put(summary.getSummaryField(),summary);

	}

	public void addSummary(Summary summary,String drl) {

		Trigger trigger=triggerMap.get(summary.getTriggerID());

		getService(trigger).addOrUpdateData(summary);

		getService(trigger).addCondition("slide-rule"+summary.getTriggerID()+summary.getSummaryField(),drl);

		summaryMap.computeIfAbsent(trigger.getTriggerID(),(id)->new HashMap<>()).put(summary.getSummaryField(),summary);

	}

	public void updateThingsInTrigger(String triggerID, Set<String> newThings) {

		Trigger trigger=triggerMap.get(triggerID);

		trigger.setThings(newThings);


		getService(trigger).addOrUpdateData(trigger);

	}

	public void updateThingsInSummary(String triggerID,String summaryField,Set<String> newThings){

		Trigger trigger=triggerMap.get(triggerID);

		Summary summary=summaryMap.get(triggerID).get(summaryField);

		summary.setThings(newThings);

		getService(trigger).addOrUpdateData(summary);
	}

	public void removeTrigger(String triggerID){



		Trigger trigger=triggerMap.get(triggerID);

		getService(trigger).removeData(trigger);
		getService(trigger).removeCondition("rule"+triggerID);

		Map<String,Summary> map=summaryMap.remove(triggerID);
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

	private void setCurrThingID(String fullThingID){

		cloudService.setCurrThingID(fullThingID);
		streamService.setCurrThingID(fullThingID);
	}

	public  void fireCondition(){

		cloudService.fireCondition();

		List<MatchResult> results=cloudService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID()));

		streamService.fireCondition();
		results=streamService.doQuery("get Match Result by TriggerID");

		results.forEach(r-> exec.doExecute(r.getTriggerID()));

	}
	

}
