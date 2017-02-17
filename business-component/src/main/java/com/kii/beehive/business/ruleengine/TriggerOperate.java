package com.kii.beehive.business.ruleengine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.event.BusinessEventListenerService;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.ruleengine.entitys.BusinessFunctionParam;
import com.kii.beehive.business.ruleengine.entitys.EngineMultipleSrcTrigger;
import com.kii.beehive.business.ruleengine.entitys.EngineSimpleTrigger;
import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.beehive.business.ruleengine.entitys.GroupSummaryEngineSource;
import com.kii.beehive.business.ruleengine.entitys.ObjectCollectSource;
import com.kii.beehive.business.ruleengine.entitys.SingleObjEngineSource;
import com.kii.beehive.business.ruleengine.entitys.SingleObject;
import com.kii.beehive.business.ruleengine.entitys.ThingCommandExecuteParam;
import com.kii.beehive.portal.event.EventListener;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.EventListenerDao;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.ThingCollectSource;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.GroupTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.task.CallBusinessFunction;
import com.kii.extension.ruleengine.store.trigger.task.CallHttpApi;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;

@Component
public class TriggerOperate {

	@Autowired
	private BusinessEventListenerService eventService;


	@Autowired
	private ObjectMapper  mapper;

	@Autowired
	private ThingTagManager thingTagService;


	@Autowired
	private EventListenerDao eventListenerDao;


	@Autowired
	private RuleEngineService general;
	
	
	@Autowired
	private TriggerConvertTool convertTool;

	@Value("${spring.profile}")
	private String profile;

	public Set<String> getTriggerListByThingID(long thingID){
		
		//TODO:
		return null;
		
	}

	
	private Map<Integer,Set<BusinessDataObject>>  dataMap=new ConcurrentHashMap<>();
	
	private AtomicInteger  index=new AtomicInteger(0);
	
	@Scheduled(initialDelay = 1000*60,fixedRate = 1000)
	public void submitData(){
		
		int oldIndex=index.getAndAccumulate(1, (left, right) -> (left+right)%10);
		
		Set<BusinessDataObject> list=dataMap.get(oldIndex);
		
		general.updateBusinessData(list);
		
		dataMap.get(oldIndex).clear();
		
	}
	
	public void addBusinessData(BusinessDataObject data){
		
		dataMap.computeIfAbsent(index.get(),(k)-> new HashSet<>()).add(data);
		
	}

	
	
	public void createTrigger(TriggerRecord record) throws TriggerCreateException {

		String triggerID=record.getId();
		
		if (record instanceof GroupTriggerRecord) {
			GroupTriggerRecord groupRecord = ((GroupTriggerRecord) record);
			
			record=convertTool.convertGroup(groupRecord);
			
		} else if (record instanceof SummaryTriggerRecord) {
			SummaryTriggerRecord summaryRecord = (SummaryTriggerRecord) record;
			
			record=convertTool.convertSummary(summaryRecord);
		}
		
		EngineTrigger engineTrigger=null;
		if(record instanceof SimpleTriggerRecord){
			EngineSimpleTrigger trigger=new EngineSimpleTrigger();
			BeanUtils.copyProperties(record,trigger,"source");
			trigger.setSource(convertBusinessObj(((SimpleTriggerRecord) record).getSource()));
			engineTrigger=trigger;
		}else if(record instanceof  MultipleSrcTriggerRecord){
			EngineMultipleSrcTrigger trigger=new EngineMultipleSrcTrigger();
			BeanUtils.copyProperties(record,trigger);
			((MultipleSrcTriggerRecord) record).getSummarySource().forEach((k,v)->{
				
				if(v instanceof  ThingSource){
					trigger.addSource(k,convertBusinessObj((ThingSource) v));
				}else if(v instanceof  ThingCollectSource){
					trigger.addSource(k,convertBusinessObj((GroupSummarySource) v));
				}
				
			});
			engineTrigger=trigger;
		}
		
		List<ExecuteTarget>  newTargets=new ArrayList<>();
		
		for(ExecuteTarget exec:engineTrigger.getTargets()){
			
			
			
			switch(exec.getType()){
				
				case CallBusinessFunction: {
					CallHttpApi call = new CallHttpApi();
					call.setContentType("application/json");
					call.setMethod(CallHttpApi.HttpMethod.POST);
					call.setUrl(ReomteUrlStore.FIRE_BUSINESS_FUN);
					
					CallBusinessFunction param = (CallBusinessFunction) exec;
					BusinessFunctionParam command = new BusinessFunctionParam();
					
					command.setBeanName(param.getBeanName());
					command.setFunctionName(param.getFunctionName());
					command.setParamList(param.getParamList());
					
					String json = "";
					try {
						json = mapper.writeValueAsString(command);
					} catch (JsonProcessingException e) {
						throw new IllegalArgumentException(e);
					}
					
					call.setContent(json);
					newTargets.add(call);
					break;
				}
				case ThingCommand:
					CallHttpApi call=new CallHttpApi();
					call.setContentType("application/json");
					call.setMethod(CallHttpApi.HttpMethod.POST);
					call.setUrl(ReomteUrlStore.FIRE_THING_CMD);
					
					CommandToThing param=(CommandToThing)exec;
					ThingCommandExecuteParam command=new ThingCommandExecuteParam();
					command.setThingList(param.getThingList().stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList()));
					command.setSelector(param.getSelector());
					command.setCommand(param.getCommand());
					command.setUserID(record.getUserID());
					
					String json = "";
					try {
						json = mapper.writeValueAsString(command);
					} catch (JsonProcessingException e) {
						throw new IllegalArgumentException(e);
					}
					call.setContent(json);
					newTargets.add(call);
					break;
				case SettingParameter:
					newTargets.add(exec);
			}
		}
		engineTrigger.setTargets(newTargets);
	}

	
	
	
	private  SingleObject convertBusinessObj(SingleThing thing){
		
		SingleObject obj=new SingleObject();
		obj.setBusinessType(BusinessObjType.Business);
		obj.setBusinessID(thingTagService.getThingByID(thing.getThingID()).getVendorThingID());
		obj.setBusinessName("beehive."+profile);
		
		return obj;
		
	}
	
	private SingleObjEngineSource convertBusinessObj(ThingSource source){
		SingleObjEngineSource output=new SingleObjEngineSource();
		output.setBusinessObj(convertBusinessObj(source.getThing()));
		output.setExpress(source.getExpress());
		return output;
	}
	
	
	private GroupSummaryEngineSource convertBusinessObj(GroupSummarySource collect){
		
		List<String> ids=thingTagService.getThingInfosByIDs(collect.getSource().getThingList()).stream().map(GlobalThingInfo::getVendorThingID).collect(Collectors.toList());
		
		ObjectCollectSource source=new ObjectCollectSource();
		source.setBusinessIDList(ids);
		source.setBusinessType(BusinessObjType.Business);
		source.setBusinessName("beehive."+profile);
		
		GroupSummaryEngineSource  output=new GroupSummaryEngineSource();
		BeanUtils.copyProperties(collect,output,"source");
		output.setSource(source);
		
		return output;
	}
	
	public  BusinessDataObject getBusinessObj(BusinessDataObject input){
		
		BusinessDataObject obj=new BusinessDataObject();
		obj.setBusinessType(BusinessObjType.Business);
		obj.setBusinessObjID(input.getBusinessName()+"."+input.getBusinessObjID());
		obj.setBusinessName("beehive."+profile);
		obj.setData(input.getData());
		
		return obj;
		
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
	
	
}
