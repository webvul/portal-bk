package com.kii.extension.ruleengine;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.store.trigger.Condition;
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
	private EngineService service;


	public void removeTrigger(String triggerID){

		service.removeTrigger(triggerID);
		scheduleService.removeManagerTaskForSchedule(triggerID);
	}

	public Map<String, Object> getRuleEngingDump() {

		Map<String, Object> map = service.dumpEngineRuntime();


		map.put("schedule", scheduleService.dump());

		return map;
	}


	public void updateExternalValue(String name,String key,Object value){

		service.updateExternalValue(name,key,value);
	}



		public void enterInit(){
		service.enteryInit();
	}

	public void leaveInit(){
		service.leaveInit();
	}

	public void updateThingStatus(String thingID, Map<String,Object> status){
		service.updateThingStatus(thingID,status,new Date());
	}

	public void updateThingStatus(String thingID,  Map<String,Object> status,Date time){
		service.updateThingStatus(thingID,status,time);
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

		//if  exist schedule, turn to quartz task

		if(record.getPredicate().getSchedule()!=null) {

			try {
				scheduleService.addExecuteTask(triggerID, record.getPredicate().getSchedule(), record.getRecordStatus() == TriggerRecord.StatusType.enable);

			} catch (SchedulerException e) {
				e.printStackTrace();
				throw new TriggerCreateException("schedule init fail:" + e.getMessage(),e);
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

			if(period!=null) {
				scheduleService.addManagerTask(triggerID, period,true);
			}
		} catch (RuntimeException e) {

			e.printStackTrace();
			throw new TriggerCreateException("create trigger instance fail:exception "+e.getClass().getName()+" msg:"+e.getMessage(),e);

		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new TriggerCreateException("global schedule init fail",e);
		}

		return;
	}


	private void addSimpleToEngine(SimpleTriggerRecord record,Map<String,Set<String>>  thingIDsMap) {

		Set<String>  thingIDs=thingIDsMap.get("comm");
		String thingID=thingIDs.iterator().next();

		service.createSimpleTrigger(thingID,record);

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

		service.createMultipleSourceTrigger(convertRecord,thingMap);
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

		service.createMultipleSourceTrigger(convertRecord,thingMap);
	}




	private void addMulToEngine(MultipleSrcTriggerRecord record,Map<String, Set<String>> thingMap) {

			service.createMultipleSourceTrigger(record,thingMap);
	}



}
