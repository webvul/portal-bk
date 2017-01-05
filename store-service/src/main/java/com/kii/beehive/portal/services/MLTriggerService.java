package com.kii.beehive.portal.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.helper.HttpClient;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.store.trigger.BeehiveTriggerType;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component
public class MLTriggerService {
	
	private Logger log= LoggerFactory.getLogger(MLTriggerService.class);
	
	
	
	@Autowired
	private TriggerManager triggerOper;
	
	@Autowired
	private RuleGeneral general;
	
	@Autowired
	private ObjectMapper mapper;
	
	
	private AtomicLong  seq=new AtomicLong(1000);
	
	private  static final String OUTPUT_VALUE="output-business";
	
	private  static final String OUTPUT_ML="output-ml";
	
	private  static final String INPUT_BOUND="input-bound";
	
	
	private ScheduledExecutorService  schedule= Executors.newScheduledThreadPool(10);
	
	@Autowired
	private HttpClient  http;
	
	private void addBusinessParamPull(String mlTaskID,int interval){
		
		
		schedule.scheduleAtFixedRate(() -> {
			
			HttpUriRequest request=new HttpGet(""+mlTaskID);
			
			String response=http.executeRequest(request);
			
			try {
				Map<String,Object> map=mapper.readValue(response, Map.class);
				
				BusinessDataObject obj=new BusinessDataObject(mlTaskID,null, BusinessObjType.TriggerGroup);
				
				triggerOper.addBusinessData(obj);
				
			} catch (IOException e) {
				log.warn("get ML data fail:task id"+mlTaskID,e.getMessage());
			}
			
			
		}, 0,interval,TimeUnit.MINUTES);
	}
	
	
	public  void  createTriggerWithML(TriggerRecord  businessTrigger,String mlTaskID,Express mlPredicate){
		
		
		MultipleSrcTriggerRecord   newRec=new MultipleSrcTriggerRecord();
		BeanUtils.copyProperties(businessTrigger,newRec);
		
		newRec.setType(BeehiveTriggerType.Multiple);
		
		
		RuleEnginePredicate predicate=businessTrigger.getPredicate();

		String mlExpress=mlPredicate.getExpress();
		if(mlPredicate.getCondition()!=null) {
			mlExpress = general.convertCondition(mlPredicate.getCondition());
		}
		
		String originExpress=predicate.getExpress();
		
		if(mlPredicate.getCondition()!=null){
			originExpress=general.convertCondition(predicate.getCondition());
		}
		
		String newExpress= originExpress + " and ( "+ mlExpress +" ) ";
		
		predicate.setExpress(newExpress);
		
		newRec.setPredicate(predicate);
		
		String triggerID=triggerOper.createTrigger(businessTrigger).getTriggerID();
		
		addBusinessParamPull(mlTaskID,10);
		
	}
	
	
	
	
	private MultipleSrcTriggerRecord  convertSimpleRecord(SimpleTriggerRecord record,String mlTaskID,String express){
		
		MultipleSrcTriggerRecord newRec=new MultipleSrcTriggerRecord();
		BeanUtils.copyProperties(record,newRec);
		newRec.setType(BeehiveTriggerType.Multiple);
		
		ThingSource source=new ThingSource();
		source.setThing(record.getSource());
		
		newRec.addSource("comm",source);
		
		
		ThingSource ml=new ThingSource();
		SingleThing obj=new SingleThing();
		obj.setTriggerGroupName(mlTaskID);
		ml.setThing(obj);
		
		newRec.addSource("ml",ml);
		
		RuleEnginePredicate predicate=record.getPredicate();
		
		String originExpress=predicate.getExpress();
		
		if(originExpress==null){
			originExpress=general.convertCondition(predicate.getCondition());
		}
		
		
		return newRec;
		
	}
	
	
	
//	private ExecuteTarget getBusinessTarget(String groupName,TriggerRecord businessTrigger){
//		SettingTriggerGroupParameter setting=new SettingTriggerGroupParameter();
//
//		setting.setGroupName(groupName);
//		setting.getParamMap().put(OUTPUT_VALUE,"sign");
//
//		businessTrigger.addTargetParam("sign","result");
//
//		businessTrigger.setTargets(Collections.singletonList(setting));
//
//		return setting;
//	}
//
//
//	private SimpleTriggerRecord  generMLTrigger(String groupName,RuleEnginePredicate predicate){
//
//		SimpleTriggerRecord trigger=new SimpleTriggerRecord();
//
//		SettingTriggerGroupParameter setting=new SettingTriggerGroupParameter();
//
//		setting.setGroupName(groupName);
//		setting.getValueMap().put(OUTPUT_ML,true);
//
//		trigger.addTarget(setting);
//
//
//		SingleThing source=new SingleThing();
//		source.setTriggerGroupName(groupName);
//		trigger.setSource(source);
//		predicate.setTriggersWhen(WhenType.CONDITION_CHANGED);
//		trigger.setPredicate(predicate);
//
//		trigger.addTargetParam("sign","result");
//
//		SettingTriggerGroupParameter parameter=new SettingTriggerGroupParameter();
//		parameter.setGroupName(groupName);
//		parameter.getParamMap().put(OUTPUT_ML,"sign");
//		trigger.addTarget(parameter);
//
//		return trigger;
//
//	}
//
//	private SimpleTriggerRecord  generFinalTrigger(List<ExecuteTarget> targetList,String triggerGroup){
//
//
//
//	}
}
