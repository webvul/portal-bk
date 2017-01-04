package com.kii.beehive.business.ruleengine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.exception.InvalidTriggerFormatException;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.BeehiveTriggerService;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.TriggerCreateException;
import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;
import com.kii.extension.ruleengine.service.BusinessObjDao;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.condition.All;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryFunctionType;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;

@Component
public class TriggerOperate {

	@Autowired
	private BusinessEventListenerService eventService;



	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private BeehiveTriggerService general;
	
	
	@Autowired
	private BusinessObjDao businessObjDao;
	
	
	


	public Set<String> getTriggerListByThingID(long thingID){
	
//		String fullKiiThingID=thingTagService.getThingByID(thingID).getFullKiiThingID();
		
		return general.getTriggerIDByObjID(BusinessObjType.Thing.getFullID(String.valueOf(thingID),null));
		
		
	}

	public List<String> init(List<TriggerRecord> list){


		List<String> errList=new ArrayList<>();

		general.enterInit();


		for(TriggerRecord  trigger:list){

			try {
				createTrigger(trigger);
				
			}catch(TriggerCreateException ex){
				errList.add(trigger.getTriggerID());
			}

		}


		thingTagService.iteratorAllThingsStatus(s -> {
			if (org.springframework.util.StringUtils.isEmpty(s.getStatus())) {
				return;
			}

			BusinessObjInRule info = new BusinessObjInRule(s.getFullKiiThingID());
			info.setCreateAt(s.getModifyDate());
			info.setValues(s.getStatus());
			
			BusinessDataObject obj=new BusinessDataObject();
			obj.setBusinessType(BusinessObjType.Thing);
			obj.setBusinessObjID(String.valueOf(s.getId()));
			obj.setData(s.getStatus());
			obj.setModified(s.getModifyDate());

			general.updateBusinessData(obj);
		});

		businessObjDao.getAllBusinessObjs().forEach((obj)->{
			
			
			general.updateBusinessData(obj);

		});
		
		businessObjDao.loadAllExtension().forEach((ext)->{
			
			general.initExternalValues(ext.getBusinessObjID(),ext.getData());
		});

		general.leaveInit();

		return errList;

	}
	
	
	public void addBusinessData(BusinessDataObject data){
		businessObjDao.addBusinessObj(data);
		
		general.updateBusinessData(data);
	}

	public void addExtensionValue(String name,Map<String,Object> data){
		
		businessObjDao.saveExtensionValue(name,data);
		
		general.initExternalValues(name,data);
	}
	
	
	public void updateExtensionValue(String name,String key,Object data){
		
		businessObjDao.updateExtensionValue(name,key,data);
		
		general.updateExternalValue(name,key,data);
	}


	
	
	public void createTrigger(TriggerRecord record) throws TriggerCreateException {

		String triggerID=record.getId();
		
		if (record instanceof GroupTriggerRecord) {
			GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
			
			record=convertGroup(groupRecord);
			
		} else if (record instanceof SummaryTriggerRecord) {
			SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;
			
			record=convertSummary(summaryRecord);
		}
		
		try {
	
			Map<String,Set<String>> map=new HashMap<>();


			if (record instanceof SimpleTriggerRecord) {
				
				map.put("comm",Collections.singleton(((SimpleTriggerRecord) record).getSource().getBusinessObj().getFullID()));

			} else if (record instanceof MultipleSrcTriggerRecord){
				MultipleSrcTriggerRecord multipleRecord=(MultipleSrcTriggerRecord)record;
				
				map=addMulToEngine(multipleRecord);
				
				multipleRecord.getSummarySource().forEach((k, v) -> {
					if(v instanceof GroupSummarySource) {
						ThingCollectSource collect=((GroupSummarySource)v).getSource();
						if(collect.getSelector()!=null&&!collect.getSelector().getTagList().isEmpty()) {
							eventService.addSummaryTagChangeListener(collect.getSelector().getTagList(), triggerID, k);
						}
					}
				});

			}else{
				throw new InvalidTriggerFormatException("unsupport trigger type");

			}
			
			general.addTriggerToEngine(record,map,true);
			

		} catch (TriggerCreateException e) {
			throw e;
		}catch(RuntimeException ex){
			throw new TriggerCreateException("unknown exception",ex);
		}

	}


	public void removeTrigger(TriggerRecord  record){


		String triggerID=record.getTriggerID();


		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {

			general.removeTrigger(record.getTriggerID());

			List<EventListener> eventListenerList = eventListenerDao.getEventListenerByTargetKey(triggerID);
			for (EventListener eventListener : eventListenerList) {
					eventListenerDao.removeEntity(eventListener.getId());
			}
		}

	}

	public void disableTrigger(TriggerRecord  record){

		String triggerID=record.getTriggerID();

		if(record.getRecordStatus()== TriggerRecord.StatusType.enable) {



			general.removeTrigger(record.getTriggerID());
		}

	}



	public Map<String, Object> getRuleEngingDump(String triggerID) {

		return general.getRuleEngingDump(triggerID);
	}



	private Map<String, Set<String>> addMulToEngine(MultipleSrcTriggerRecord record) {
		Map<String, Set<String>> thingMap = new HashMap<>();

		final AtomicBoolean isStream = new AtomicBoolean(false);

		record.getSummarySource().forEach((k, v) -> {

			switch(v.getType()){
				case thing:
					ThingSource thing=(ThingSource)v;
					thingMap.put(k, Collections.singleton(thing.getBusinessObj().getFullID()));
					break;
				case summary:
					GroupSummarySource summary=(GroupSummarySource)v;
					
					thingMap.put(k,getBusinessObjSet(summary.getSource()));
					break;
			}
		});

		return thingMap;


	}
	
	private Set<String>  getBusinessObjSet(ThingCollectSource source){
		
		if(source.getSelector().notEmpty()){
			
			return thingTagService.getBusinessObjs(source.getSelector());
			
		}else {
			
			return source.getFullBusinessObjs().stream().map(BusinessDataObject::getFullID).collect(Collectors.toSet());
		}
	}
	
	
	private MultipleSrcTriggerRecord convertSummary(SummaryTriggerRecord record){
		
		
		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();
		
		BeanUtils.copyProperties(record,convertRecord,"type");
		
		record.getSummarySource().forEach((k,v)->{
			
			ThingCollectSource source=v.getSource();
			
			v.getExpressList().forEach((exp)->{
				
				GroupSummarySource  elem=new GroupSummarySource();
				
				elem.setFunction(exp.getFunction());
				elem.setStateName(exp.getStateName());
				elem.setSource(source);
				
				String index=k+"."+exp.getSummaryAlias();
				convertRecord.addSource(index,elem);
				
			});
		});
		
		return convertRecord;
	}
	
	private MultipleSrcTriggerRecord  convertGroup(GroupTriggerRecord record){
		
		
		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();
		BeanUtils.copyProperties(record,convertRecord,"type");
		
		int thingNum=getBusinessObjSet(record.getSource()).size();
		
		
		Condition cond=new All();
		switch(record.getPolicy().getGroupPolicy()){
			//	Any,All,Some,Percent,None;
			
			case All:
				cond= TriggerConditionBuilder.newCondition().equal("comm",thingNum).getConditionInstance();
				break;
			case Any:
				cond=TriggerConditionBuilder.newCondition().greatAndEq("comm",1).getConditionInstance();
				break;
			case Some:
				cond=TriggerConditionBuilder.newCondition().greatAndEq("comm",record.getPolicy().getCriticalNumber()).getConditionInstance();
				break;
			case Percent:
				int percent=(record.getPolicy().getCriticalNumber()*thingNum)/100;
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
		
		GroupSummarySource  elem=new GroupSummarySource();
		
		elem.setFunction(SummaryFunctionType.count);
		Express exp=new Express();
		exp.setCondition(record.getPredicate().getCondition());
		elem.setExpress(exp);
		
		elem.setSource(record.getSource());
		
		convertRecord.addSource("comm",elem);
		
		return convertRecord;
	}
	
	
	
}
