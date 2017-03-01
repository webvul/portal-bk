package com.kii.beehive.business.ruleengine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.business.ruleengine.entitys.BusinessFunctionParam;
import com.kii.beehive.business.ruleengine.entitys.CallHttpApiWithSign;
import com.kii.beehive.business.ruleengine.entitys.EngineBusinessObj;
import com.kii.beehive.business.ruleengine.entitys.EngineBusinessType;
import com.kii.beehive.business.ruleengine.entitys.EngineExecuteTarget;
import com.kii.beehive.business.ruleengine.entitys.EngineMultipleSrcTrigger;
import com.kii.beehive.business.ruleengine.entitys.EngineSimpleTrigger;
import com.kii.beehive.business.ruleengine.entitys.EngineTrigger;
import com.kii.beehive.business.ruleengine.entitys.GroupSummaryEngineSource;
import com.kii.beehive.business.ruleengine.entitys.ObjectCollectSource;
import com.kii.beehive.business.ruleengine.entitys.SingleObjEngineSource;
import com.kii.beehive.business.ruleengine.entitys.SingleObject;
import com.kii.beehive.business.ruleengine.entitys.ThingCommandExecuteParam;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.BusinessObjType;
import com.kii.beehive.portal.store.entity.trigger.ExecuteTarget;
import com.kii.beehive.portal.store.entity.trigger.GroupSummarySource;
import com.kii.beehive.portal.store.entity.trigger.MultipleSrcTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.SingleThing;
import com.kii.beehive.portal.store.entity.trigger.ThingSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.groups.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.groups.SummaryTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.task.CallBusinessFunction;
import com.kii.beehive.portal.store.entity.trigger.task.CommandToThing;

@Component
public class EngineTriggerBuilder {
	
	@Autowired
	private ThingTagManager thingTagService;
	
	@Autowired
	private TriggerConvertTool convertTool;
	
	
	
	@Autowired
	private ObjectMapper mapper;
	
	private String groupName;
	
	@Value("${spring.profile}")
	public void setProfile(String profile){
		groupName = "beehive_" + profile;
	}
	
	
	public String getGroupName(){
		return groupName;
	}
			
	
	public EngineBusinessObj generBusinessData(BusinessDataObject obj){
		EngineBusinessObj data=new EngineBusinessObj();
		data.setState(obj.getData());
		
		String id = obj.getBusinessObjID();
		if(StringUtils.isNotBlank(obj.getBusinessName())){
				id+="_"+obj.getBusinessName();
		}
		data.setBusinessID(id);
		
		return data;
	}
	

	
	public EngineTrigger  generEngineTrigger(TriggerRecord  record){
		
		
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
			BeanUtils.copyProperties(record,trigger,"source","targets");
			trigger.setSource(convertObj(((SimpleTriggerRecord) record).getSource()));
			engineTrigger=trigger;
		}else if(record instanceof MultipleSrcTriggerRecord){
			EngineMultipleSrcTrigger trigger=new EngineMultipleSrcTrigger();
			BeanUtils.copyProperties(record,trigger,"summarySource","targets");
			((MultipleSrcTriggerRecord) record).getSummarySource().forEach((k,v)->{
				
				if(v instanceof  ThingSource){
					trigger.addSource(k,convertBusinessObj((ThingSource) v));
				} else if (v instanceof GroupSummarySource) {
					trigger.addSource(k,convertBusinessObj((GroupSummarySource) v));
				}
				
			});
			engineTrigger=trigger;
		}
		
		List<EngineExecuteTarget>  newTargets=new ArrayList<>();
		
		for(ExecuteTarget exec:record.getTargets()){
			
			switch(exec.getType()){
				
				case CallBusinessFunction: {
					
					CallBusinessFunction param = (CallBusinessFunction) exec;
					BusinessFunctionParam command = new BusinessFunctionParam();
					command.setTriggerID(record.getTriggerID());
					command.setBeanName(param.getBeanName());
					command.setFunctionName(param.getFunctionName());
					command.setParamList(param.getParamList());
					
					CallHttpApiWithSign call = getHttpApiCall(RemoteUrlStore.getFIreBusinessFunUrl(), param);
					
					newTargets.add(call);
					break;
				}
				case ThingCommand: {
					
					CommandToThing param = (CommandToThing) exec;
					ThingCommandExecuteParam command = new ThingCommandExecuteParam();
					command.setTriggerID(record.getTriggerID());
					command.setThingList(param.getThingList().stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList()));
					command.setSelector(param.getSelector());
					command.setCommand(param.getCommand());
					command.setUserID(record.getUserID());
					
					CallHttpApiWithSign call = getHttpApiCall(RemoteUrlStore.getThingCmdRemoteUrl(), command);
					
					newTargets.add(call);
					break;
				}
			}
		}
		engineTrigger.setTargets(newTargets);
		
		return engineTrigger;
		
	}
	
	private CallHttpApiWithSign getHttpApiCall(String url,Object context) {
		CallHttpApiWithSign call=new CallHttpApiWithSign();
		call.setContentType("application/json");
		call.setMethod(CallHttpApiWithSign.HttpMethod.POST);
		call.setUrl(url);
		call.setSiteName(groupName);
		
		String json = "";
		try {
			json = mapper.writeValueAsString(context);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
		call.setContent(json);
		return call;
	}
	
	
	private  SingleObject convertObj(SingleThing thing){
		
		SingleObject obj=new SingleObject();
		if(thing.getBusinessType()==BusinessObjType.Context) {
			obj.setObjDataType(EngineBusinessType.Context);
		}else{
			obj.setObjDataType(EngineBusinessType.Business);
		}
		String id=null;
		if(thing.getBusinessType()==BusinessObjType.Thing) {
			id = thingTagService.getThingByID(thing.getThingID()).getVendorThingID();
		}else{
			id=thing.getBusinessID();
		}
		
		if(StringUtils.isNotBlank(thing.getBusinessName())){
			id+="_"+thing.getBusinessName();
		}
		obj.setBusinessID(id);
		obj.setGroupName(groupName);
		
		return obj;
		
	}
	
	private SingleObjEngineSource convertBusinessObj(ThingSource source){
		SingleObjEngineSource output=new SingleObjEngineSource();
		output.setBusinessObj(convertObj(source.getThing()));
		output.setExpress(source.getExpress());
		return output;
	}
	
	
	private GroupSummaryEngineSource convertBusinessObj(GroupSummarySource collect){
		
		List<String> ids=thingTagService.getThingInfosByIDs(collect.getSource().getThingList()).stream().map(GlobalThingInfo::getVendorThingID).collect(Collectors.toList());
		
		ObjectCollectSource source=new ObjectCollectSource();
		source.setBusinessIDList(ids);
		if(collect.getSource().getBusinessType()==BusinessObjType.Context) {
			source.setObjDataType(EngineBusinessType.Context);
		}else{
			source.setObjDataType(EngineBusinessType.Business);
		}
		source.setGroupName(groupName);
		
		GroupSummaryEngineSource  output=new GroupSummaryEngineSource();
		BeanUtils.copyProperties(collect,output,"source");
		output.setSource(source);
		
		return output;
	}
	
}
