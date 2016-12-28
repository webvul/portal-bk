package com.kii.beehive.portal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SimpleTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.WhenType;
import com.kii.extension.ruleengine.store.trigger.target.SettingParameter;
import com.kii.extension.ruleengine.store.trigger.task.SettingTriggerGroupParameter;

@Component
public class MLTriggerService {
	
	
	
	@Autowired
	private TriggerManager  triggerMang;
	
	
	private  static final String OUTPUT_NAME="output-business";
	
	private  static final String OUTPUT_ML="output-ml";
	
	private  static final String INPUT_BOUND="input-bound";
	
	
	private ExecuteTarget getInnerTarget(String groupName){
		SettingTriggerGroupParameter setting=new SettingTriggerGroupParameter();
		
		setting.setGroupName(groupName);
		setting.getValueMap().put(OUTPUT_NAME,true);
		
		return setting;
	}
	
	
	private SimpleTriggerRecord  generMLTrigger(String groupName){
		
		MultipleSrcTriggerRecord trigger=new MultipleSrcTriggerRecord();
		
		SettingTriggerGroupParameter setting=new SettingTriggerGroupParameter();
		
		setting.setGroupName(groupName);
		setting.getValueMap().put(OUTPUT_ML,true);
		
		trigger.addTarget(setting);
		
		
		ThingSource source=new ThingSource();
		source.setTriggerGroupName(groupName);
		trigger.addSource("one",source);
		
		ThingSource target=new ThingSource();
		target.setTriggerGroupName(groupName);
		trigger.addSource("two",target);
		
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		predicate.setExpress("$p:n{one.output-ml}>$p:n{two.input-bound}");
		predicate.setTriggersWhen(WhenType.CONDITION_CHANGED);
		
		trigger.setPredicate(predicate);
		
		trigger.addTargetParam("sign","result");
		
	}
	
	private SimpleTriggerRecord  generFinalTrigger(List<ExecuteTarget> targetList,String triggerGroup){
		
		
		
	}
}
