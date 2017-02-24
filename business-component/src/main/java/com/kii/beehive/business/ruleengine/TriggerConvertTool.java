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
import com.kii.beehive.portal.store.entity.trigger.BusinessDataObject;
import com.kii.beehive.portal.store.entity.trigger.CommandParam;
import com.kii.beehive.portal.store.entity.trigger.Condition;
import com.kii.beehive.portal.store.entity.trigger.Express;
import com.kii.beehive.portal.store.entity.trigger.GroupSummarySource;
import com.kii.beehive.portal.store.entity.trigger.MultipleSrcTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.RuleEnginePredicate;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.ThingCollectSource;
import com.kii.beehive.portal.store.entity.trigger.ThingSource;
import com.kii.beehive.portal.store.entity.trigger.TriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.condition.All;
import com.kii.beehive.portal.store.entity.trigger.condition.AndLogic;
import com.kii.beehive.portal.store.entity.trigger.condition.Equal;
import com.kii.beehive.portal.store.entity.trigger.condition.ExpressCondition;
import com.kii.beehive.portal.store.entity.trigger.condition.InCollect;
import com.kii.beehive.portal.store.entity.trigger.condition.Like;
import com.kii.beehive.portal.store.entity.trigger.condition.LogicCol;
import com.kii.beehive.portal.store.entity.trigger.condition.NotLogic;
import com.kii.beehive.portal.store.entity.trigger.condition.OrLogic;
import com.kii.beehive.portal.store.entity.trigger.condition.Range;
import com.kii.beehive.portal.store.entity.trigger.condition.SimpleCondition;
import com.kii.beehive.portal.store.entity.trigger.groups.GroupTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.groups.SummaryFunctionType;
import com.kii.beehive.portal.store.entity.trigger.groups.SummaryTriggerRecord;

@Component
public class TriggerConvertTool {

	
	@Autowired
	private ThingTagManager thingTagService;
	

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
		
		RuleEnginePredicate predicate=trigger.getPredicate();
		
		String exp1=trigger.getPredicate().getExpress();
		if(StringUtils.isBlank(exp1)){
			Condition newCondition=convertCondition(trigger.getPredicate().getCondition(),"comm");
			predicate.setCondition(newCondition);
		}else{
			String newExp1=addParamPrefix(exp1,"comm");
			predicate.setExpress(newExp1);
		}
		
		record.setPredicate(predicate);
		
		ThingSource source=new ThingSource();
		source.setThing(trigger.getSource());
		record.addSource("comm",source);
		
		return record;
		
	}
	
	private Condition convertCondition(Condition condition,String prefix){
	
		if(condition instanceof LogicCol){
			
			switch (condition.getType()){
				case and:
					for(Condition cond:((AndLogic)condition).getClauses()){
						convertCondition(cond,prefix);
					}
					break;
				case or:
					for(Condition cond:((OrLogic)condition).getClauses()){
						convertCondition(cond,prefix);
					}
					break;
				case not:
					convertCondition( ((NotLogic)condition).getClause(),prefix);
					break;
			}
		}
		
		if(condition instanceof SimpleCondition){
			String field=((SimpleCondition) condition).getField();
			field=prefix+"."+field;
			((SimpleCondition)condition).setField(field);
			
			if(condition instanceof  ExpressCondition){
				String express=((ExpressCondition)condition).getExpress();
				if(StringUtils.isNotBlank(express)){
					((ExpressCondition)condition).setExpress(addParamPrefix(express,prefix));
				}
			}
			
			if(condition instanceof  Range){
				Range range=(Range)condition;
				if(StringUtils.isNotBlank(range.getLowerExpress())){
					range.setLowerExpress(addParamPrefix(range.getLowerExpress(),prefix));
				}
				if(StringUtils.isNotBlank(range.getUpperExpress())){
					range.setLowerExpress(addParamPrefix(range.getUpperExpress(),prefix));
				}
			}
			
			switch(condition.getType()){
				
				case range:
					Range range=(Range)condition;
					range.setLowerLimit(convertValue(range.getLowerLimit(),prefix));
					range.setUpperLimit(convertValue(range.getUpperLimit(),prefix));
					break;
				case in:
					InCollect in=(InCollect)condition;
					in.getValues().replaceAll((v)->convertValue(v,prefix));
					break;
				case eq:
					Equal eq=(Equal)condition;
					eq.setValue(convertValue(eq.getValue(),prefix));
					break;
				case like:
					Like like=(Like)condition;
					like.setValue(convertValue(like.getValue(),prefix));
					break;
			}
			
		}
		
		return condition;
		
	}
	
	private static final Pattern paramPattern= Pattern.compile("\\$([peth](\\:[iscm])?)\\{([^\\}]+)\\}");
	
	
	private Object convertValue(Object obj,String prefix){
		
		
		if(obj==null) {
			return null;
		}
		if (obj instanceof String) {
				
				String value=(String)obj;
				if(paramPattern.matcher(value).find()){
					return addParamPrefix(value,prefix);
				}else {
					return "\"" + String.valueOf(obj) + "\"";
				}
		}
		return obj;
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
		
		BeanUtils.copyProperties(record,convertRecord,"type","summarySource");
		
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
		BeanUtils.copyProperties(record,convertRecord,"type","source");
		
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
