package com.kii.extension.ruleengine;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.drools.DroolsTriggerService;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;
import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.SingleThing;
import com.kii.extension.ruleengine.drools.entity.Summary;
import com.kii.extension.ruleengine.drools.entity.Trigger;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.drools.entity.TriggerValues;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.CommandParam;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.schedule.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.schedule.TriggerValidPeriod;


@Component
public class BeehiveTriggerService {


	@Autowired
	private ScheduleService scheduleService;


	@Autowired
	private DroolsTriggerService droolsTriggerService;

	@Autowired
	private RuleGeneral ruleGeneral;

	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private RelationStore  relationStore;


	public void removeTrigger(String triggerID){

		droolsTriggerService.removeTrigger(triggerID);
		scheduleService.removeManagerTaskForSchedule(triggerID);
	}

	public Map<String, Object> getRuleEngingDump(String triggerID) {

		Map<String, Object> map = droolsTriggerService.getEngineRuntime(triggerID);


		map.put("schedule", scheduleService.dump(triggerID));

		return map;
	}



	public void updateExternalValue(String name,String key,Object value){
		ExternalValues val=new ExternalValues(name);
		val.addValue(key,value);
		droolsTriggerService.addExternalValue(val);
	}
	
	public void updateTriggerInstValue(String triggerID,String key,Object value){
		
		
		droolsTriggerService.updateTriggerInstData(triggerID,key,value);
	}
	
	
	public void initExternalValues(String name,Map<String,Object> values){
		ExternalValues val=new ExternalValues(name);
		val.setValues(values);
		droolsTriggerService.addExternalValue(val);
	}

	public void enterInit(){
		droolsTriggerService.enterInit();
	}

	public void leaveInit(){
		droolsTriggerService.leaveInit();
	}



	public void updateBusinessData(BusinessDataObject data){

		BusinessObjInRule newStatus=new BusinessObjInRule(data.getFullID());
		newStatus.setValues(data.getData());
		newStatus.setCreateAt(data.getModified());

		droolsTriggerService.addThingStatus(newStatus);
	}
	
	public void addTriggerToEngine(TriggerRecord record,Map<String,Set<String>>  thingIDsMap,boolean fireNow) throws TriggerCreateException{

		
		
		String triggerID=record.getId();

		TriggerValidPeriod period=record.getPreparedCondition();

		if(period!=null){
			//ctrl enable sign by schedule.
			record.setRecordStatus(TriggerRecord.StatusType.disable);
		}

		if(period instanceof SimplePeriod){
			SimplePeriod  simp=(SimplePeriod)period;
			long endDate=simp.getEndTime();
			if(System.currentTimeMillis()>endDate){
				throw new TriggerCreateException("cron timeout");

			}

		}
		
		TriggerValues instData=new TriggerValues(triggerID);
		instData.setValues(record.getInstData().getValues());

		try {

			if (record instanceof SimpleTriggerRecord) {
				Set<String>  thingIDs=thingIDsMap.get("comm");
				String thingID=thingIDs.iterator().next();
				addSimpleToEngine((SimpleTriggerRecord) record,thingID,instData);
			}  else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;

				addMulToEngine(multipleRecord,thingIDsMap,instData);

			}else{
				record.setRecordStatus(TriggerRecord.StatusType.deleted);
				throw new TriggerCreateException("unsupport trigger type");
			}


			scheduleService.addManagerTask(triggerID, period,record.getPredicate().getSchedule());
			
			if(fireNow){
				
				droolsTriggerService.fireCurrTrigger(triggerID);
			}

		} catch (RuntimeException e) {

//			e.printStackTrace();
			throw new TriggerCreateException("create trigger instance fail:exception "+e.getClass().getName()+" msg:"+e.getMessage(),e);

		} catch (SchedulerException|IOException e) {
//			e.printStackTrace();
			throw new TriggerCreateException("global schedule init fail",e);
		}

		return;
	}


	private void addSimpleToEngine(SimpleTriggerRecord record,String thingID,TriggerValues instData) {

//		Set<String>  thingIDs=thingIDsMap.get("comm");
//		String thingID=thingIDs.iterator().next();

		fillDelayParam(record);


		relationStore.fillThingTriggerIndex(thingID,record.getTriggerID());


		String triggerID=record.getId();

		Trigger trigger=new Trigger(triggerID);

		if(StringUtils.isNotBlank(thingID)) {
			trigger.setThingSet(Collections.singleton(thingID));
		}
		trigger.setType(TriggerType.simple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		trigger.setEnable(TriggerRecord.StatusType.enable == record.getRecordStatus());

		String rule=ruleGeneral.getSimpleTriggerDrl(triggerID,record.getPredicate(),record.getTargetParamList());
		
		droolsTriggerService.addTrigger(trigger,instData,rule);

		if(StringUtils.isNotBlank(thingID)) {
			SingleThing thing=new SingleThing();
			thing.setThingID(thingID);
			thing.setTriggerID(triggerID);
			thing.setName("comm");
			droolsTriggerService.addTriggerData(thing);
		}


	}





	private void addMulToEngine(MultipleSrcTriggerRecord record,Map<String, Set<String>> thingMap,TriggerValues  instData) {


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
		
		droolsTriggerService.addMultipleTrigger(trigger,instData,drl);

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



	private void fillDelayParam(TriggerRecord record){


		List<CommandParam> list=record.getTargetParamList();

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


	public void changeThingsInTrigger(String triggerID,Set<String> newThings){

		relationStore.maintainThingTriggerIndex(newThings,triggerID);
		droolsTriggerService.updateThingsWithName(triggerID,"comm",newThings);


	}


	public void changeThingsInSummary(String triggerID,String summaryName,Set<String> newThings){

		relationStore.maintainThingTriggerIndex(newThings,triggerID,summaryName);

		droolsTriggerService.updateThingsWithName(triggerID,summaryName,newThings);

	}
	
	public Set<String>  getTriggerIDByObjID(String thingID){
		Set<String>  set=relationStore.getTriggerSetByThingID(thingID);
		
		if(set==null){
			return Collections.emptySet();
		}
		
		return set;
	}


}
