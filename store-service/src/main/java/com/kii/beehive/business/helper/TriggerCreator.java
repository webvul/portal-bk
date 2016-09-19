package com.kii.beehive.business.helper;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.EntryNotFoundException;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.EngineService;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.schedule.ScheduleService;
import com.kii.extension.ruleengine.service.TriggerRecordDao;
import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
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
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;

@Component
public class TriggerCreator {

	@Autowired
	private TriggerRecordDao triggerDao;


	@Autowired
	private BusinessEventListenerService eventService;



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private ScheduleService scheduleService;


	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private EngineService service;


	public void clear(){

		service.clear();
		scheduleService.clearTrigger();

		init();
	}

	public void init(){


		List<TriggerRecord> recordList = triggerDao.getAllEnableTrigger();


		List<TriggerRecord>  list=recordList.stream().filter((r)->r.getType()!= BeehiveTriggerType.Gateway).collect(Collectors.toList());

		service.enteryInit();

		list.forEach(r->addTriggerToEngine(r));

		thingTagService.iteratorAllThingsStatus(s -> {
			if (org.springframework.util.StringUtils.isEmpty(s.getStatus())) {
				return;
			}

			ThingStatusInRule info = new ThingStatusInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(s.getStatus());

			service.initThingStatus(info);
		});

		service.leaveInit();

	}


	public String createTrigger(TriggerRecord record) {

		triggerDao.addKiiEntity(record);

		addTriggerToEngine(record);

		return record.getId();

	}

	public void removeTrigger(TriggerRecord  record){

		String triggerID=record.getTriggerID();

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			if(record.isInDrools()) {
				service.removeTrigger(triggerID);
				scheduleService.removeManagerTaskForSchedule(triggerID);

				List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
				for (EventListener eventListener : eventListenerList) {
					eventListenerDao.removeEntity(eventListener.getId());
				}
			}else{

				scheduleService.removeManagerTaskForSchedule(triggerID);
			}
		}

	}

	public void disableTrigger(TriggerRecord  record){

		String triggerID=record.getTriggerID();

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			if(record.isInDrools()) {
				service.removeTrigger(triggerID);
				scheduleService.removeManagerTaskForSchedule(triggerID);
			}else{
				scheduleService.removeManagerTaskForSchedule(triggerID);
			}
		}

	}


	public Map<String, Object> getRuleEngingDump() {

		Map<String, Object> map = service.dumpEngineRuntime();


		map.put("schedule", scheduleService.dump());

		return map;
	}

	public void addTriggerToEngine(TriggerRecord record) {

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
				triggerDao.deleteTriggerRecord(triggerID,"cron timeout");
				return;
			}

		}


		//if not exist condition, turn to quartz task

		Condition  condition=record.getPredicate().getCondition();
		String express=record.getPredicate().getExpress();
		if(condition==null&& StringUtils.isBlank(express)){

			try {
				scheduleService.addExecuteTask(triggerID,record.getPredicate().getSchedule(),record.getRecordStatus()==TriggerRecord.StatusType.enable);
				if(period!=null) {
					scheduleService.addManagerTask(triggerID, record.getPreparedCondition(),false);
				}

				triggerDao.setQuartzSign(triggerID);

			}catch (SchedulerException e) {
				e.printStackTrace();
				throw new InvalidTriggerFormatException("schedule init fail");
			}

			return;
		}

		try {




			if (record instanceof SimpleTriggerRecord) {
				addSimpleToEngine((SimpleTriggerRecord) record);
			} else if (record instanceof GroupTriggerRecord) {
				GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
				addGroupToEngine(groupRecord);
				if (!groupRecord.getSource().getTagList().isEmpty()) {
					eventService.addGroupTagChangeListener(groupRecord.getSource().getTagList(), triggerID);
				}

			} else if (record instanceof SummaryTriggerRecord) {
				SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;

				addSummaryToEngine(summaryRecord);
				summaryRecord.getSummarySource().forEach((k, v) -> {
					eventService.addSummaryTagChangeListener(v.getSource().getTagList(), triggerID, k);
				});

			} else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;

				addMulToEngine(multipleRecord);

				multipleRecord.getSummarySource().forEach((k, v) -> {
					if(v instanceof GroupSummarySource) {
						eventService.addSummaryTagChangeListener(((GroupSummarySource)v).getSource().getTagList(), triggerID, k);
					}
				});

			}else{
				throw new InvalidTriggerFormatException("unsupport trigger type");

			}

			if(period!=null) {
				scheduleService.addManagerTask(triggerID, period,true);
			}
		} catch (RuntimeException e) {

			e.printStackTrace();
			triggerDao.deleteTriggerRecord(triggerID,"create trigger instance fail:"+e.getCause());
			throw e;

		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new InvalidTriggerFormatException("schedule init fail");
		}

		return ;
	}


	private void addSimpleToEngine(SimpleTriggerRecord record) {
		String thingID = null;
		if (record.getSource() != null) {
			GlobalThingInfo thingInfo = thingTagService.getThingByID(record.getSource().getThingID());
			if (thingInfo != null) {
				thingID = thingInfo.getFullKiiThingID();
			}else{

				throw EntryNotFoundException.thingNotFound(record.getSource().getThingID());
			}
		}

		service.createSimpleTrigger(thingID,record);

	}

	private void addSummaryToEngine(SummaryTriggerRecord record ){


		Map<String, Set<String>> summaryMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			if (v.getExpressList().stream().filter((exp) -> exp.getSlideFuntion() != null).findAny().isPresent() && !isStream.get()) {
				isStream.set(true);
			}
			;

			summaryMap.put(k, thingTagService.getKiiThingIDs(v.getSource()));
		});

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

	private  void addGroupToEngine(GroupTriggerRecord record){

		Set<String> thingIDs = thingTagService.getKiiThingIDs(record.getSource());


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




	private void addMulToEngine(MultipleSrcTriggerRecord record) {
		Map<String, Set<String>> thingMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			switch(v.getType()){
				case thing:
					ThingSource thing=(ThingSource)v;
					thingMap.put(k, Collections.singleton(thingTagService.getThingByID(Integer.parseInt(thing.getThingID())).getFullKiiThingID()));
				case summary:
					GroupSummarySource summary=(GroupSummarySource)v;
					thingMap.put(k, thingTagService.getKiiThingIDs(summary.getSource()));
					break;
			}
		});

		if(isStream.get()) {
//			service.createStreamSummaryTrigger(record, thingMap);
		}else{
			service.createMultipleSourceTrigger(record,thingMap);
		}
	}
	
	

}
