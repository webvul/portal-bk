package com.kii.extension.ruleengine;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.SingleThing;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.CommandParam;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;

@Component
public class EngineService {


	@Autowired
	private DroolsTriggerService  droolsTriggerService;

	@Autowired
	private RuleGeneral  ruleGeneral;

	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private RelationStore  relationStore;


	private void fillDelayParam(TriggerRecord record){


		List<CommandParam>  list=record.getTargetParamList();

		int i=0;
		for(ExecuteTarget target:record.getTargets()){

			String delay=target.getDelay();

			if(StringUtils.isBlank(delay)){
				i++;
				continue;
			}

			CommandParam  param=new CommandParam();
			param.setName("delay_"+i);
			param.setExpress(delay);

			list.add(param);

			i++;
		}

		record.setTargetParamList(list);


	}




	public void createMultipleSourceTrigger(MultipleSrcTriggerRecord record,Map<String,Set<String> > thingMap){

		fillDelayParam(record);

		relationStore.fillThingTriggerElemIndex(thingMap,record.getTriggerID());

		Trigger trigger=new Trigger(record.getId());
		trigger.setType(TriggerType.multiple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		boolean withSchedule=record.getPredicate().getSchedule()!=null;


		String drl=ruleGeneral.generMultipleDrlConfig(record,withSchedule);

		Set<String> thingSet=new HashSet<>();
		thingMap.values().forEach( v->thingSet.addAll(v));
		trigger.setThingSet(thingSet);

		droolsTriggerService.addMultipleTrigger(trigger,drl);

		record.getSummarySource().forEach((name,src)->{

			    switch(src.getType()){
					case summary:
						GroupSummarySource source=(GroupSummarySource)src;

						Summary summary=new Summary();
						summary.setTriggerID(trigger.getTriggerID());
						summary.setFieldName(source.getStateName());
						summary.setFunName(source.getFunction().name());
						summary.setThingCol(thingMap.get(name));
						summary.setName(name);

						droolsTriggerService.addTriggerData(summary);

						break;
					case thing:

						SingleThing thing=new SingleThing();
						thing.setTriggerID(trigger.getTriggerID());
						thing.setName(name);

						thing.setThingID(thingMap.get(name).iterator().next());

						droolsTriggerService.addTriggerData(thing);
						break;
					default:
					}


				}
		);

	}


	public void clear(){

//		droolsTriggerService.clear();
	}


	public Map<String,Object>  dumpEngineRuntime(){

		return droolsTriggerService.getEngineRuntime();

	}

	public void  createSimpleTrigger(String thingID, SimpleTriggerRecord record)  {

		fillDelayParam(record);


		relationStore.fillThingTriggerIndex(thingID,record.getTriggerID());


		String triggerID=record.getId();

		Trigger trigger=new Trigger(triggerID);

		trigger.setType(TriggerType.simple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		trigger.setEnable(TriggerRecord.StatusType.enable == record.getRecordStatus());

		String rule=ruleGeneral.getSimpleTriggerDrl(triggerID,record.getPredicate(),record.getTargetParamList());


		droolsTriggerService.addTrigger(trigger,rule);

		if(!StringUtils.isEmpty(thingID)) {
			SingleThing thing=new SingleThing();
			thing.setThingID(thingID);
			thing.setTriggerID(triggerID);
			thing.setName("comm");
			droolsTriggerService.addTriggerData(thing);
		}



	}


	public void changeThingsInTrigger(String triggerID,Set<String> newThings){

		relationStore.maintainThingTriggerIndex(newThings,triggerID);
		droolsTriggerService.updateThingsWithName(triggerID,"comm",newThings);


	}


	public void changeThingsInSummary(String triggerID,String summaryName,Set<String> newThings){

		relationStore.maintainThingTriggerIndex(newThings,triggerID,summaryName);

		droolsTriggerService.updateThingsWithName(triggerID,summaryName,newThings);

	}


	public void enteryInit(){
		droolsTriggerService.enterInit();
	}


	public void leaveInit(){
		droolsTriggerService.leaveInit();
	}


//	public void initThingStatus(ThingStatusInRule  status){
//		droolsTriggerService.addThingStatus(status);
//	}

	public void updateThingStatus(String thingID,Map<String,Object> status,Date time) {


		ThingStatusInRule newStatus=new ThingStatusInRule(thingID);
		newStatus.setValues(status);
		newStatus.setCreateAt(time);

		droolsTriggerService.addThingStatus(newStatus);
	}

	public void updateExternalValue(String name,String key,Object value){

		ExternalValues  values=new ExternalValues(name);

		values.addValue(key,value);
		droolsTriggerService.addExternalValue(values);

	}


	public void updateExternalValues(String name,Map<String,Object> values){
		ExternalValues  val=new ExternalValues(name);
		val.setValues(values);

		droolsTriggerService.addExternalValue(val);

	}

	public Set<String> getRelationTriggersByThingID(String thingID){

		return relationStore.getTriggerSetByThingID(thingID);
	}

	public void removeTrigger(String triggerID){

		droolsTriggerService.removeTrigger(triggerID);


	}

	public void fireSchedule(String triggerID){
		droolsTriggerService.updateScheduleSign(triggerID);

	}
}
