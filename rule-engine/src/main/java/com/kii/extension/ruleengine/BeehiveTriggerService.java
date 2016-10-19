package com.kii.extension.ruleengine;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
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
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.store.trigger.CommandParam;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimplePeriod;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TriggerValidPeriod;
import com.kii.extension.ruleengine.store.trigger.condition.All;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;


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

	public void enterInit(){
		droolsTriggerService.enterInit();
	}

	public void leaveInit(){
		droolsTriggerService.leaveInit();
	}

	public void updateThingStatus(String thingID, Map<String,Object> status){
		updateThingStatus(thingID,status,new Date());

	}

	public void updateThingStatus(String thingID,  Map<String,Object> status,Date time){

		ThingStatusInRule newStatus=new ThingStatusInRule(thingID);
		newStatus.setValues(status);
		newStatus.setCreateAt(new Date());

		droolsTriggerService.addThingStatus(newStatus);
	}


	public void addTriggerToEngine(TriggerRecord record,Map<String,Set<String>>  thingIDsMap) throws TriggerCreateException{

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


		try {

			if (record instanceof SimpleTriggerRecord) {
				addSimpleToEngine((SimpleTriggerRecord) record,thingIDsMap);
			} else if (record instanceof GroupTriggerRecord) {
				GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
				addGroupToEngine(groupRecord,thingIDsMap);

			} else if (record instanceof SummaryTriggerRecord) {
				SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;

				addSummaryToEngine(summaryRecord,thingIDsMap);


			} else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;

				addMulToEngine(multipleRecord,thingIDsMap);

			}else{
				record.setRecordStatus(TriggerRecord.StatusType.deleted);
				throw new TriggerCreateException("unsupport trigger type");
			}


			scheduleService.addManagerTask(triggerID, period,record.getPredicate().getSchedule());

		} catch (RuntimeException e) {

			e.printStackTrace();
			throw new TriggerCreateException("create trigger instance fail:exception "+e.getClass().getName()+" msg:"+e.getMessage(),e);

		} catch (SchedulerException|IOException e) {
			e.printStackTrace();
			throw new TriggerCreateException("global schedule init fail",e);
		}

		return;
	}


	private void addSimpleToEngine(SimpleTriggerRecord record,Map<String,Set<String>>  thingIDsMap) {

		Set<String>  thingIDs=thingIDsMap.get("comm");
		String thingID=thingIDs.iterator().next();

		fillDelayParam(record);


		relationStore.fillThingTriggerIndex(thingID,record.getTriggerID());


		String triggerID=record.getId();

		Trigger trigger=new Trigger(triggerID);

		trigger.setThingSet(thingIDs);
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

	private void addSummaryToEngine(SummaryTriggerRecord record,Map<String,Set<String>>  summaryMap){


		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();

		BeanUtils.copyProperties(record,convertRecord);


		Map<String,Set<String>> thingMap=new HashMap<>();

		record.getSummarySource().forEach((k,v)->{

			TagSelector source=v.getSource();

			v.getExpressList().forEach((exp)->{

				GroupSummarySource  elem=new GroupSummarySource();

				elem.setFunction(exp.getFunction());
				elem.setStateName(exp.getStateName());
				elem.setSource(source);

				String index=k+"."+exp.getSummaryAlias();
				convertRecord.addSource(index,elem);
				thingMap.put(index,summaryMap.get(k));

			});
		});

		addMulToEngine(convertRecord,thingMap);
	}

	private  void addGroupToEngine(GroupTriggerRecord record,Map<String,Set<String>>  thingIDsMap){


		Set<String>  thingIDs=thingIDsMap.get("comm");

		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();
		BeanUtils.copyProperties(record,convertRecord);


		Condition cond=new All();
		switch(record.getPolicy().getGroupPolicy()){
			//	Any,All,Some,Percent,None;

			case All:
				cond= TriggerConditionBuilder.newCondition().equal("comm",thingIDs.size()).getConditionInstance();
				break;
			case Any:
				cond=TriggerConditionBuilder.newCondition().greatAndEq("comm",1).getConditionInstance();
				break;
			case Some:
				cond=TriggerConditionBuilder.newCondition().greatAndEq("comm",record.getPolicy().getCriticalNumber()).getConditionInstance();
				break;
			case Percent:
				int percent=(record.getPolicy().getCriticalNumber()*thingIDs.size())/100;
				cond=TriggerConditionBuilder.newCondition().equal("comm",percent).getConditionInstance();
				break;
			case None:
				cond=TriggerConditionBuilder.newCondition().equal("comm",0).getConditionInstance();
		}
		RuleEnginePredicate predicate=new RuleEnginePredicate();

		predicate.setCondition(cond);
		predicate.setTriggersWhen(record.getPredicate().getTriggersWhen());
		predicate.setSchedule(record.getPredicate().getSchedule());

		convertRecord.setPredicate(predicate);

		Map<String,Set<String>> thingMap=new HashMap<>();
		thingMap.put("comm",new HashSet<>(thingIDs));

		GroupSummarySource  elem=new GroupSummarySource();

		elem.setFunction(SummaryFunctionType.count);
		Express exp=new Express();
		exp.setCondition(record.getPredicate().getCondition());
		elem.setExpress(exp);

		elem.setSource(record.getSource());

		convertRecord.addSource("comm",elem);

		addMulToEngine(convertRecord,thingMap);
	}




	private void addMulToEngine(MultipleSrcTriggerRecord record,Map<String, Set<String>> thingMap) {


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



	private void fillDelayParam(TriggerRecord record){


		List<CommandParam> list=record.getTargetParamList();

		int i=0;
		for(ExecuteTarget target:record.getTarget()){

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


}
