package com.kii.beehive.business.ruleengine;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.extension.ruleengine.TriggerConditionBuilder;
import com.kii.extension.ruleengine.drools.RuleGeneral;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.CommandParam;
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
public class TriggerConvertTool {
	
	
	@Autowired
	private RuleGeneral general;
	
	@Autowired
	private ThingTagManager thingTagService;
	
	
	public String getExpress(Condition condition){
	
		return general.convertCondition(condition);
	}
	
	public MultipleSrcTriggerRecord convertTrigger(TriggerRecord record) {
		
		switch (record.getType()){
			
			case Simple:
				return convertSimple((SimpleTriggerRecord)record);
			case Group:
				return convertGroup((GroupTriggerRecord)record);
			case Summary:
				return convertSummary((SummaryTriggerRecord)record);
			default:
				return (MultipleSrcTriggerRecord) record;
		}
	}
	
	
	private MultipleSrcTriggerRecord  convertSimple(SimpleTriggerRecord trigger){
		
		
		MultipleSrcTriggerRecord record=new MultipleSrcTriggerRecord();
		
		record.setPreparedCondition(trigger.getPreparedCondition());
		record.setTargets(trigger.getTargets());
		
		List<CommandParam> list=new ArrayList<>();
		trigger.getTargetParamList().forEach((k)->{
			k.setExpress(addParamPrefix(k.getExpress(),"comm"));
			list.add(k);
		});
		
		record.setTargetParamList(list);
		
		String exp1=trigger.getPredicate().getExpress();
		if(StringUtils.isBlank(exp1)){
			exp1=general.convertCondition(trigger.getPredicate().getCondition());
		}
		
		String newExp1=addParamPrefix(exp1,"comm");
		
		RuleEnginePredicate predicate=trigger.getPredicate();
		predicate.setExpress(newExp1);
		predicate.setCondition(null);
		
		record.setPredicate(predicate);
		
		ThingSource source=new ThingSource();
		source.setThing(trigger.getSource());
		record.addSource("comm",source);
		
		return record;
		
	}
	
	
	
	public  String addParamPrefix(String express,String prefix){
		StringBuffer sb=new StringBuffer();
		
		Pattern pattern= Pattern.compile("\\$p(\\:\\w)?\\{([^\\}]+)\\}");
		
		Matcher matcher=pattern.matcher(express);
		
		while(matcher.find()) {
			
			int start=matcher.start(2);
			
			int base=matcher.start();
			
			String str=matcher.group();
			
			StringBuffer buf=new StringBuffer(str);
			buf.insert(start-base,prefix+".");

			
			matcher.appendReplacement(sb,Matcher.quoteReplacement(buf.toString()));
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}
	
	public  MultipleSrcTriggerRecord convertSummary(SummaryTriggerRecord record){
		
		
		MultipleSrcTriggerRecord convertRecord=new MultipleSrcTriggerRecord();
		
		BeanUtils.copyProperties(record,convertRecord,"type");
		
		record.getSummarySource().forEach((k,v)->{
			
			ThingCollectSource source=v.getSource();
			
			v.getExpressList().forEach((exp)->{
				
				GroupSummarySource elem=new GroupSummarySource();
				
				elem.setFunction(exp.getFunction());
				elem.setStateName(exp.getStateName());
				elem.setSource(source);
				
				String index=k+"."+exp.getSummaryAlias();
				convertRecord.addSource(index,elem);
				
			});
		});
		
		return convertRecord;
	}
	
	public  MultipleSrcTriggerRecord  convertGroup(GroupTriggerRecord record){
		
		
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
	
	
	private Set<String> getBusinessObjSet(ThingCollectSource source){
		
		if(source.getSelector().notEmpty()){
			
			return thingTagService.getBusinessObjs(source.getSelector());
			
		}else {
			
			return source.getFullBusinessObjs().stream().map(BusinessDataObject::getFullID).collect(Collectors.toSet());
		}
	}
	
}
