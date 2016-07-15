package com.kii.extension.ruleengine;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.condition.All;
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

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

//		List<String> thingList=thingMap.values().stream().flatMap(th->th.stream()).collect(Collectors.toList());

		relationStore.fillThingTriggerElemIndex(thingMap,record.getTriggerID());

		Trigger trigger=new Trigger(record.getId());
		trigger.setType(TriggerType.multiple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		String drl=ruleGeneral.generMultipleDrlConfig(record,thingMap);

		droolsTriggerService.addMultipleTrigger(trigger,drl,record.getPredicate().getSchedule()!=null);

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

		droolsTriggerService.fireCondition();

	}


	public void clear(){
		droolsTriggerService.clear();
	}


	public Map<String,Object>  dumpEngineRuntime(){

		return droolsTriggerService.getEngineRuntime();

	}


//	public void createStreamSummaryTrigger(SummaryTriggerRecord record, Map<String,Set<String> > summaryMap){
//
//		Trigger trigger=new Trigger(record.getId());
//
//		trigger.setType(TriggerType.summary);
//		trigger.setWhen(record.getPredicate().getTriggersWhen());
//		trigger.setStream(true);
//		trigger.setEnable(record.getRecordStatus()== TriggerRecord.StatusType.enable);
//
//		String 	rule = ruleGeneral.generDrlConfig(record.getId(), TriggerType.summary, record.getPredicate(),record.getTargetParamList());
//
//		droolsTriggerService.addSummaryTrigger(trigger,rule);
//
//		record.getSummarySource().forEach((k,v)->{
//
//			v.getExpressList().forEach((exp)->{
//
//				Summary summary=new Summary();
//				summary.setTriggerID(trigger.getTriggerID());
//				summary.setFieldName(exp.getStateName());
//
//				summary.setName(k+"."+exp.getSummaryAlias());
//				summary.setThingCol(summaryMap.get(k));
//
//				if(exp.getSlideFuntion()!=null){
//					String drl=ruleGeneral.generSlideConfig(trigger.getTriggerID(),k,exp);
//					summary.setFunName(exp.getFunction().name());
//					droolsTriggerService.addSlideSummary(summary,drl);
//
//				}else{
//					summary.setFunName(exp.getFunction().name());
//					droolsTriggerService.addTriggerData(summary);
//				}
//
//			});
//
//		});
//
//		droolsTriggerService.fireCondition();
//
//	}

	public void createSummaryTrigger(SummaryTriggerRecord record,Map<String,Set<String> > summaryMap){

		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();
		convertRecord.setId(record.getId());
		convertRecord.setPreparedCondition(record.getPreparedCondition());
		convertRecord.setTargetParamList(record.getTargetParamList());
		convertRecord.setPredicate(record.getPredicate());
		convertRecord.setTarget(record.getTargets());

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

		this.createMultipleSourceTrigger(convertRecord,thingMap);
	}

	public void createGroupTrigger(GroupTriggerRecord record,Collection<String> thingIDs){

		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();
		convertRecord.setId(record.getId());
		convertRecord.setPreparedCondition(record.getPreparedCondition());
		convertRecord.setTargetParamList(record.getTargetParamList());
		convertRecord.setTarget(record.getTargets());

		Condition cond=new All();
		switch(record.getPolicy().getGroupPolicy()){
			//	Any,All,Some,Percent,None;

			case All:
				cond=TriggerConditionBuilder.newCondition().equal("comm",thingIDs.size()).getConditionInstance();
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
		this.createMultipleSourceTrigger(convertRecord,thingMap);
	}

	/*

	public void createGroupTrigger(Collection<String> thingIDs, GroupTriggerRecord record){


		Trigger trigger=new Trigger(record.getId());

		trigger.setType(TriggerType.group);

		Group group=new Group();

		TriggerGroupPolicy policy=record.getPolicy();
		group.setPolicy(policy.getGroupPolicy());
		group.setNumber(policy.getCriticalNumber());
		group.setTriggerID(record.getId());
		group.setName("comm");

		trigger.setStream(false);

		RuleEnginePredicate predicate=record.getPredicate();

		trigger.setWhen(predicate.getTriggersWhen());

		group.setThingCol(thingIDs);

		trigger.setEnable(record.getRecordStatus()== TriggerRecord.StatusType.enable);

		String rule=ruleGeneral.generGroupDrlConfig(record.getId(),policy.getGroupPolicy(),predicate);

		droolsTriggerService.addTrigger(trigger,rule);
		droolsTriggerService.addTriggerData(group);

		droolsTriggerService.fireCondition();

	}
	*/


	public void  createSimpleTrigger(String thingID, SimpleTriggerRecord record)  {

		fillDelayParam(record);


		relationStore.fillThingTriggerIndex(thingID,record.getTriggerID());


		String triggerID=record.getId();

		Trigger trigger=new Trigger(triggerID);

		trigger.setType(TriggerType.simple);
		trigger.setStream(false);
		trigger.setWhen(record.getPredicate().getTriggersWhen());

		trigger.setEnable(TriggerRecord.StatusType.enable == record.getRecordStatus());

		String rule=ruleGeneral.generDrlConfig(triggerID,TriggerType.simple,record.getPredicate(),record.getTargetParamList());

		droolsTriggerService.addTrigger(trigger,rule,record.getPredicate().getSchedule()!=null);

		if(!StringUtils.isEmpty(thingID)) {
			SingleThing thing=new SingleThing();
			thing.setThingID(thingID);
			thing.setTriggerID(triggerID);
			thing.setName("comm");
			droolsTriggerService.addTriggerData(thing);
		}



		droolsTriggerService.fireCondition();

	}


	public void changeThingsInTrigger(String triggerID,Set<String> newThings){

		relationStore.maintainThingTriggerIndex(newThings,triggerID);
		droolsTriggerService.updateThingsWithName(triggerID,"comm",newThings);

	}


	public void changeThingsInSummary(String triggerID,String summaryName,Set<String> newThings){

		relationStore.maintainThingTriggerIndex(newThings,triggerID,summaryName);

		droolsTriggerService.updateThingsWithName(triggerID,summaryName,newThings);

	}

	public void initThingStatus(List<ThingStatusInRule> thingInfos) {

		droolsTriggerService.setInitSign(true);

		thingInfos.forEach(th->droolsTriggerService.initThingStatus(th));

		droolsTriggerService.fireCondition();
		droolsTriggerService.setInitSign(false);
	}

	public void updateThingStatus(String thingID,ThingStatus status,Date time) {


		ThingStatusInRule newStatus=new ThingStatusInRule(thingID);
		newStatus.setValues(status.getFields());
		newStatus.setCreateAt(time);

		droolsTriggerService.addThingStatus(newStatus);
	}

	public void updateExternalValue(String name,String key,Object value){

		ExternalValues  values=new ExternalValues(name);

		values.addValue(key,value);
		droolsTriggerService.addExternalValue(values);
		droolsTriggerService.fireCondition();

	}


	public void updateExternalValues(String name,Map<String,Object> values){
		ExternalValues  val=new ExternalValues(name);
		val.setValues(values);

		droolsTriggerService.addExternalValue(val);
		droolsTriggerService.fireCondition();

	}

	public Set<String> getRelationTriggersByThingID(String thingID){

		return relationStore.getTriggerSetByThingID(thingID);
	}

	
	public void disableTrigger(String triggerID) {

		droolsTriggerService.disableTrigger(triggerID);

	}


	public void enableTrigger(String triggerID) {


		droolsTriggerService.enableTrigger(triggerID);

		droolsTriggerService.fireCondition();

	}

	public void removeTrigger(String triggerID){

		droolsTriggerService.removeTrigger(triggerID);

	}
}
