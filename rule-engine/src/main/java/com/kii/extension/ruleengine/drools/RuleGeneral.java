package com.kii.extension.ruleengine.drools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.store.entity.trigger.Condition;
import com.kii.beehive.portal.store.entity.trigger.CronPrefix;
import com.kii.beehive.portal.store.entity.trigger.IntervalPrefix;
import com.kii.beehive.portal.store.entity.trigger.RuleEnginePredicate;
import com.kii.beehive.portal.store.entity.trigger.SchedulePrefix;
import com.kii.beehive.portal.store.entity.trigger.condition.AndLogic;
import com.kii.beehive.portal.store.entity.trigger.condition.Equal;
import com.kii.beehive.portal.store.entity.trigger.condition.InCollect;
import com.kii.beehive.portal.store.entity.trigger.condition.Like;
import com.kii.beehive.portal.store.entity.trigger.condition.LogicCol;
import com.kii.beehive.portal.store.entity.trigger.condition.NotLogic;
import com.kii.beehive.portal.store.entity.trigger.condition.OrLogic;
import com.kii.beehive.portal.store.entity.trigger.condition.Range;
import com.kii.beehive.portal.store.entity.trigger.condition.SimpleCondition;
import com.kii.extension.ruleengine.drools.entity.TriggerType;

@Component
public class RuleGeneral {

	@Autowired
	private ResourceLoader  loader;

	private String loadTemplate(String name)  {

		try {
			return StreamUtils.copyToString(
					loader.getResource("classpath:com/kii/extension/ruleengine/template/" + name + ".template").getInputStream(),
					StandardCharsets.UTF_8);
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}

	public String generDrlConfig(String triggerID, TriggerType type, RuleEnginePredicate predicate){

		String template=loadTemplate(type.name());

		String fullDrl=generDrl(template,predicate,triggerID);

		return fullDrl;
	}


	private String generDrl(String template, RuleEnginePredicate predicate, String triggerID){

			Map<String,String> params=new HashMap<>();
			params.put("timer",generTimer(predicate.getSchedule()));

			if(StringUtils.isEmpty(predicate.getExpress())) {
				Condition cond=predicate.getCondition();
				if(cond==null){
					params.put("express"," eval(true) ");
				}else{
					params.put("express",generExpress(cond));
				}
			}else{
				params.put("express",predicate.getExpress());
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
			case not:
				sb.append(getNotExpress((NotLogic)condition));
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
				sb.delete(sb.length()-5,sb.length()-1);

				break;
			case or:
				OrLogic orLogic=(OrLogic)condition;
				for(Condition cond:orLogic.getClauses()){

					String express=generExpress(cond);
					sb.append(express);
					sb.append(" || ");
				}
				sb.delete(sb.length()-5,sb.length()-1);
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


	private String getSimpleExpress(SimpleCondition condition,boolean isNot){

		StringBuilder sb=new StringBuilder();
		sb.append(condition.getField()).append(" ");

		switch(condition.getType()){

			case eq:
				if(isNot){
					sb.append(" != ");
				}else {
					sb.append(" == ");
				}

				Equal value=(Equal)condition;

				sb.append(getFinalValue(value.getValue()));

				break;
			case range:
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
						sb.append(getFinalValue(range.getLowerLimit()));
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
						sb.append(getFinalValue(range.getUpperLimit()));
				}

				break;
			case like:
				if(isNot){
					sb.append(" not contains ");
				}else {
					sb.append(" contains ");
				}
				String like=((Like)condition).getLike();
				sb.append(getFinalValue(like));
				break;
			case in:
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

	private String getFinalValue(Object obj){

		if(obj instanceof String){
			return "\""+String.valueOf(obj)+"\"";
		}else{
			return String.valueOf(obj);
		}
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

}
