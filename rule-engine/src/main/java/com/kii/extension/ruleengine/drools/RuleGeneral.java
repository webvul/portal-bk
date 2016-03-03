package com.kii.extension.ruleengine.drools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.IntervalPrefix;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SchedulePrefix;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.ruleengine.store.trigger.condition.ExpressCondition;
import com.kii.extension.ruleengine.store.trigger.condition.InCollect;
import com.kii.extension.ruleengine.store.trigger.condition.Like;
import com.kii.extension.ruleengine.store.trigger.condition.LogicCol;
import com.kii.extension.ruleengine.store.trigger.condition.NotLogic;
import com.kii.extension.ruleengine.store.trigger.condition.OrLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Range;
import com.kii.extension.ruleengine.store.trigger.condition.SimpleCondition;

@Component
public class RuleGeneral {

	private Logger log= LoggerFactory.getLogger(RuleGeneral.class);

	@Autowired
	private ResourceLoader  loader;

	private String loadTemplate(String name)  {

		try {
			return StreamUtils.copyToString(
					loader.getResource("classpath:com/kii/extension/ruleengine/template/" + name + "Trigger.template").getInputStream(),
					StandardCharsets.UTF_8);
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}


	private String generScheduleDrl(SchedulePrefix schedule, String triggerID){

		String template=loadTemplate("schedule");
		Map<String,String> params=new HashMap<>();
		params.put("timer",generTimer(schedule));

		params.put("triggerID",triggerID);

		return StrTemplate.generByMap(template,params);

	}

	public String generDrlConfig(String triggerID, TriggerType type, RuleEnginePredicate predicate){


		String template=loadTemplate(type.name());

		String fullDrl=generDrl(template,predicate,triggerID);

		log.info(triggerID+"\n"+fullDrl);
		return fullDrl;
	}


	public String generTimer(SchedulePrefix prefix){
		//	timer (cron:* 0/1 * * * ?)
		//	timer (int: 0s 1m)

		if(prefix==null){
			return "";
		}
		StringBuilder sb=new StringBuilder("timer ( ");

		switch(prefix.getType()){
			case "Interval":
				IntervalPrefix interval=(IntervalPrefix)prefix;
				sb.append(" int: 0s ").append(interval.getTimeUnit().getFullDescrtion(interval.getInterval()));
				break;
			case "Cron":

				String cron=((CronPrefix)prefix).getCron();

				sb.append(" cron: "+cron);
				break;
		}

		sb.append(")");
		return sb.toString();
	}


	private String generDrl(String template, RuleEnginePredicate predicate, String triggerID){


			Map<String,String> params=new HashMap<>();

			if(!StringUtils.isEmpty(predicate.getExpress())) {
				params.put("express",predicate.getExpress());
				params.put("timer",generTimer(predicate.getSchedule()));

			}else if(predicate.getCondition()!=null){
				Condition cond=predicate.getCondition();
				if(cond==null){
					params.put("express"," eval(true) ");
				}else{
					params.put("express",generExpress(cond));
				}
				params.put("timer",generTimer(predicate.getSchedule()));

			}else if(predicate.getSchedule()!=null){

				return generScheduleDrl(predicate.getSchedule(),triggerID);

			}else{
				throw new IllegalArgumentException("predicate format invalid:"+predicate);
			}

			params.put("triggerID",triggerID);

		return StrTemplate.generByMap(template,params);

	}



	public String generExpress(Condition condition){


		StringBuilder  sb=new StringBuilder("(");

		switch(condition.getType()){

			case and:
			case or:
				sb.append(getLogicExpress((LogicCol)condition));
				break;
			case not:
				sb.append(getNotExpress((NotLogic)condition));
				break;
			default:
				sb.append(getSimpleExpress((SimpleCondition)condition));
		}

		return sb.append(")").toString();
	}

	private String getLogicExpress(LogicCol condition){

		StringBuilder sb=new StringBuilder();

		switch(condition.getType()){
			case and:
				AndLogic andLogic=(AndLogic)condition;
				for(Condition cond:andLogic.getClauses()){

					String express=generExpress(cond);
					sb.append(express);
					sb.append(" && ");
				}
				sb.delete(sb.length()-4,sb.length());

				break;
			case or:
				OrLogic orLogic=(OrLogic)condition;
				for(Condition cond:orLogic.getClauses()){

					String express=generExpress(cond);
					sb.append(express);
					sb.append(" || ");
				}
				sb.delete(sb.length()-4,sb.length());
				break;
		}
		return sb.toString();
	}

	private String getNotExpress(NotLogic condition){

		Condition clause=condition.getClause();

		if(clause instanceof LogicCol){

			return "!(" + getLogicExpress((LogicCol)clause) +" ) ";

		}else{
			return getSimpleExpress((SimpleCondition) clause,true);
		}
	}

	private String getSimpleExpress(SimpleCondition condition){

		return getSimpleExpress(condition,false);
	}

	private static final String FIELD=" values[\"${0}\"] ";

	private String getFieldStr(SimpleCondition cond){

		return StrTemplate.gener(FIELD, cond.getField());
	}


	private String getSimpleExpress(SimpleCondition condition,boolean isNot){

		StringBuilder sb=new StringBuilder();


		switch(condition.getType()){

			case all:
				if(isNot){
					sb.append(" false ");
				}else{
					sb.append(" true ");
				}
			case eq:
				sb.append(getFieldStr(condition));
				if(isNot){
					sb.append(" != ");
				}else {
					sb.append(" == ");
				}

				Equal value=(Equal)condition;

				sb.append(getFinalValue(value));

				break;
			case range:
				sb.append(getFieldStr(condition));
				Range range=(Range)condition;

				if (range.isExistLower()) {
					    if(isNot){
							sb.append("<");
						}else {
							sb.append(">");
						}
						if (range.isLowerIncluded()^isNot) {
							sb.append("=");
						}
						sb.append(getFinalValue(range.getLowerExpress(), range.getLowerLimit()));
				}
				if (range.isExistUpper()) {
						if(isNot){
							sb.append(">");
						}else {
							sb.append("<");
						}
						if (range.isUpperIncluded()^isNot) {
							sb.append("=");
						}
						sb.append(getFinalValue(range.getUpperExpress(),range.getUpperLimit()));
				}

				break;
			case like:
				sb.append(getFieldStr(condition));
				if(isNot){
					sb.append(" not contains ");
				}else {
					sb.append(" contains ");
				}
				Like like=((Like)condition);
				sb.append(getFinalValue(like));
				break;
			case in:
				sb.append(getFieldStr(condition));
				if(isNot){
					sb.append(" not in ");
				}else{
					sb.append(" in ");
				}
				String array=getArrayValue(((InCollect)condition).getValues());
				sb.append(array);
				break;
			default:
				throw new IllegalArgumentException("unsupported operate:"+condition.getType().name());

		}

		return sb.toString();

	}

	private String getArrayValue(List<?> list){

		return list.toString();
	}

	private String getFinalValue(String express,Object obj){


		if(obj!=null) {
			if (obj instanceof String) {
				return "\"" + String.valueOf(obj) + "\"";
			} else {
				return String.valueOf(obj);
			}
		}else if(!StringUtils.isEmpty(express)){
			return express;
		}else{
			throw new IllegalArgumentException("condition invalidFormat ,exp:"+express+" obj:"+obj);
		}
	}

	private String getFinalValue(ExpressCondition cond){

		return getFinalValue(cond.getExpress(),cond.getValue());
	}



}
