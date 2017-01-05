package com.kii.beehive.portal.services;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.ruleengine.TriggerConvertTool;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.service.CombineTriggerDao;
import com.kii.beehive.portal.store.entity.MLTriggerCombine;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SingleThing;
import com.kii.extension.ruleengine.store.trigger.ThingSource;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;

@Component
public class CombineTriggerService {
	
	private Logger log= LoggerFactory.getLogger(CombineTriggerService.class);
	
	
	
	@Autowired
	private TriggerManager triggerOper;

	
	@Autowired
	private TriggerConvertTool convertTool;
	
	@Autowired
	private CombineTriggerDao triggerDao;

	
	
	
	public void removeMLTrigger(String triggerID){
		
		MLTriggerCombine oldCombine=triggerDao.deleteEntity(triggerID);

		triggerDao.removeEntity(triggerID);
		
	}
	
	public void enableMLTrigger(String triggerID){
		
		MLTriggerCombine oldCombine=triggerDao.enableEntity(triggerID);
		
		triggerOper.enableTrigger(oldCombine.getRelationTriggerID());
		
	}
	
	public void disableMLTrigger(String triggerID){
		
		MLTriggerCombine oldCombine=triggerDao.disableEntity(triggerID);
		
		triggerOper.disableTrigger(oldCombine.getRelationTriggerID());
		
	}
	
	public void deleteTrigger(String triggerID) {
		
		MLTriggerCombine oldCombine=triggerDao.deleteEntity(triggerID);
		
		triggerOper.deleteTrigger(oldCombine.getRelationTriggerID());
	}
	
	
	public MLTriggerCombine getTrigger(String triggerID){
		return triggerDao.getObjectByID(triggerID);
	}
	
	public List<MLTriggerCombine> getAll(){
		return triggerDao.getAllEnableEntity();
	}
	
	public void updateMLTrigger(MLTriggerCombine combine){
		
		MLTriggerCombine oldCombine=triggerDao.getObjectByID(combine.getId());
		
		
		MultipleSrcTriggerRecord record=getTriggerRecord(combine);
		
		record.setTriggerID(oldCombine.getRelationTriggerID());
		
		triggerOper.updateTrigger(record);
		
		triggerDao.updateEntity(combine,combine.getId());
	}
	

	public  String  createTriggerWithML(MLTriggerCombine combine){
		
		
		String id=triggerDao.addEntity(combine).getObjectID();
		
		MultipleSrcTriggerRecord newTrigger = getTriggerRecord(combine);
		newTrigger.setCreator(id);
		
		String triggerID=triggerOper.createTrigger(newTrigger).getTriggerID();
		
		triggerDao.updateEntity(Collections.singletonMap("relationTriggerID",triggerID),id);
		
		return triggerID;
	}
	
	private MultipleSrcTriggerRecord getTriggerRecord(MLTriggerCombine combine) {
		MultipleSrcTriggerRecord newTrigger=convertTool.convertTrigger(combine.getBusinessTrigger());
		newTrigger.setUsedByWho(TriggerRecord.UsedByType.Combine_trigger);
		
		return addMlCondition(newTrigger,combine.getMlTaskID(),combine.getMlCondition(),combine.isJoinWithAND());
		
		
	}
	
	
	private MultipleSrcTriggerRecord addMlCondition(MultipleSrcTriggerRecord trigger,String taskID,Condition additionCond,boolean isAnd){
		
		
		String exp1=trigger.getPredicate().getExpress();
		if(StringUtils.isBlank(exp1)){
			exp1=convertTool.getExpress(trigger.getPredicate().getCondition());
		}
		if(StringUtils.isBlank(exp1)){
			exp1=" eval(true) ";
		}
		
		String exp2=convertTool.getExpress(additionCond);
		String newExp2=convertTool.addParamPrefix(exp2,"ml");
		
		newExp2+=" ||  $p{ml._enable} == false  ";
		
		String oper=isAnd?" && ":" || ";
		
		String fullExp=exp1+oper+" ( "+newExp2+" ) ";
		
		RuleEnginePredicate predicate=new RuleEnginePredicate();
		BeanUtils.copyProperties(trigger.getPredicate(),predicate);
		predicate.setExpress(fullExp);
		predicate.setCondition(null);
		
		trigger.setPredicate(predicate);
		
		trigger.addSource("ml",getMLTaskGroup(taskID));
		
		return trigger;
		
	}

	
	
	
	private ThingSource getMLTaskGroup(String taskID){
		
		
		SingleThing param=new SingleThing();
		param.setBusinessID(taskID);
		param.setBusinessName("mlOutput");
		param.setBusinessType(BusinessObjType.Context);
		
		ThingSource source=new ThingSource();
		source.setThing(param);
		return source;
	}
	
	

}
