package com.kii.extension.ruleengine.drools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.extension.ruleengine.drools.entity.TriggerType;
import com.kii.extension.ruleengine.store.trigger.CommandParam;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.CronPrefix;
import com.kii.extension.ruleengine.store.trigger.Express;
import com.kii.extension.ruleengine.store.trigger.IntervalPrefix;
import com.kii.extension.ruleengine.store.trigger.RuleEnginePredicate;
import com.kii.extension.ruleengine.store.trigger.SchedulePrefix;
import com.kii.extension.ruleengine.store.trigger.SummaryExpress;
import com.kii.extension.ruleengine.store.trigger.TimerUnitType;
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
import com.kii.extension.ruleengine.store.trigger.multiple.GroupSummarySource;
import com.kii.extension.ruleengine.store.trigger.multiple.MultipleSrcTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.multiple.ThingSource;

@Component
public class RuleGeneral {

	private static final String EVAL_TRUE = " eval( true ) ";
	private Logger log= LoggerFactory.getLogger(RuleGeneral.class);

	@Autowired
	private ResourceLoader  loader;

	@Autowired
	private ExpressConvert  convert;

	private String loadTemplate(String name)  {

		try {
			return StreamUtils.copyToString(
					loader.getResource("classpath:com/kii/extension/ruleengine/template/" + name + "Trigger.template").getInputStream(),
					StandardCharsets.UTF_8);
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}

	private String loadUnit(String name)  {

		name=StringUtils.capitalize(name);
		try {
			return StreamUtils.copyToString(
					loader.getResource("classpath:com/kii/extension/ruleengine/template/unit" + name + ".template").getInputStream(),
					StandardCharsets.UTF_8);
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}




	public String generMultipleDrlConfig(MultipleSrcTriggerRecord  record,boolean withSchedule) {


		StringBuilder sb=new StringBuilder();

		String fullTemplate = loadTemplate(TriggerType.multiple.name());
		Map<String, String> params = new HashMap<>();

		if(withSchedule) {
			fullTemplate=loadTemplate(TriggerType.multiple.name()+"Schedule");
			params.put("timer",generTimer(record.getPredicate().getSchedule()));

		}
		params.put("triggerID", record.getId());
		params.put("express",generExpress(record.getPredicate()));



		params.put("fillParam",getParamMappingList(record.getTargetParamList(),false));

		String multipleDrl=StrTemplate.generByMap(fullTemplate,params);

		sb.append(multipleDrl);
		record.getSummarySource().forEach((k, v) -> {

			switch(v.getType()){

				case thing:
					String thingTemplate = loadUnit("thing");

					ThingSource thingSrc=(ThingSource)v;

					Map<String, String> things = new HashMap<>();
					things.put("triggerID", record.getId());
					things.put("express",generExpress(thingSrc.getExpress()));
					things.put("name",k);

					String thingUnit=StrTemplate.generByMap(thingTemplate,things);
					sb.append("\n").append(thingUnit).append("\n");

					break;
				case summary:
					String groupTemplate = loadUnit("group");

					GroupSummarySource groupSrc=(GroupSummarySource)v;

					Map<String, String> groups = new HashMap<>();
					groups.put("triggerID", record.getId());
					groups.put("express",generExpress(groupSrc.getExpress()));
					groups.put("name",k);

					String groupUnit=StrTemplate.generByMap(groupTemplate,groups);
					sb.append("\n").append(groupUnit).append("\n");

					break;
				default:
					throw new IllegalArgumentException();
			}

		});

		sb.append("\n\n");

		return sb.toString();
	}


	public String generSlideConfig(String triggerID,String summaryField,SummaryExpress express){

		String template=loadTemplate("slideSummary");

		Map<String,String> params=new HashMap<>();

/*
rule "${triggerID} summary unit:slide windows"
when
	Trigger(type =="summary",$triggerID:triggerID=="${triggerID}" )
	$summary:Summary(triggerID==$triggerID ,$things:things, funName=="${slide-fun-name}" )
	CurrThing(thing memberOf $things) from currThing
	accumulate( ThingStatusInRule(thingID memberOf $things , $status:values) over window:${sum-suffix}( ${windowSize});
                    $sum : ${funName}($status.get($summary.getFieldName()))
                  )
then
	System.out.println("compute  sum summary by slide length"+$sum);
	insert(new SummaryResult($triggerID,$summary.getSummaryField(),$sum));
end
 */
		params.put("triggerID",triggerID);
		params.put("funName",express.getFunction().name());
		params.put("sum-suffix",express.getSlideFuntion().getType().name());
		params.put("slide-fun-name",express.getFullSlideFunName());
		params.put("windowSize",express.getSlideFuntion().getWindowDefine());
		params.put("summaryField",summaryField);

		String fullDrl= StrTemplate.generByMap(template,params);

		log.info("slide drl\n"+fullDrl);

		return fullDrl;
	}




	public String getSimpleTriggerDrl(String triggerID, RuleEnginePredicate predicate, List<CommandParam> paramList){


		Map<String,String> params=new HashMap<>();

		String template=null;
		if(predicate.getSchedule()!=null){

			if(predicate.getCondition()==null&& StringUtils.isBlank(predicate.getExpress())){

				template=loadTemplate("schedule");
			}else {
				template = loadTemplate("simpleSchedule");
			}
			params.put("timer",generTimer(predicate.getSchedule()));

		}else {
			template=loadTemplate(TriggerType.simple.name());
		}

		params.put("fillParam",getParamMappingList(paramList,true));

		params.put("triggerID",triggerID);
		params.put("express",generExpress(predicate));

		String fullDrl=StrTemplate.generByMap(template,params);

		log.info(triggerID+"\n"+fullDrl);
		return fullDrl;
	}

	private static String ParamBindTemplate="result.setParam(\"${0}\",${1}); \n";

	private String getParamMappingList(List<CommandParam>  paramList,boolean isSimpleTrigger){


		/*
			MatchResult result=new MatchResult($thingID);
	result.setParam("a",$status.getValue("foo"));
	result.setParam("b",$status.getValue("bar"));
	result.setDelay($status.getValue("delay"));
		 */
		StringBuilder sb=new StringBuilder();

		paramList.forEach((param)->{

			String result=StrTemplate.gener(ParamBindTemplate,param.getName(),convert.convertRightExpress(param.getExpress(),isSimpleTrigger));

			sb.append(result);
		});

		return sb.toString();
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
				TimerUnitType timeUnit=interval.getTimeUnit();
				if(timeUnit==null){
					timeUnit= TimerUnitType.Minute;
				}
				sb.append(" int: 0s ").append(timeUnit.getFullDescrtion(interval.getInterval()));
				break;
			case "Cron":

				String cron=((CronPrefix)prefix).getCron();

				sb.append(" cron: "+cron);
				break;
		}

		sb.append(")");
		return sb.toString();
	}




	public String generExpress(RuleEnginePredicate predicate) {


		if (predicate.getExpress() != null) {
			return convert.convertExpress(predicate.getExpress());
		}

		if (predicate.getCondition() == null) {

			return EVAL_TRUE;
		}

		Condition condition = predicate.getCondition();
		return generExpress(condition);
	}


	public String generExpress(Express express){

			if(express.getCondition()!=null){
				return generExpress(express.getCondition());
			}else if(express.getExpress()!=null){
				return express.getExpress();
			}else{
				return  EVAL_TRUE;
			}

	}

	public String generExpress(Condition condition){

		StringBuilder  sb=new StringBuilder();

		switch(condition.getType()){

			case and:
			case or:
				sb.append(getLogicExpress((LogicCol)condition));
				break;
			case not:
				sb.append(getNotExpress((NotLogic)condition));
				break;
			default:
				sb.append("(");
				sb.append(getSimpleExpress((SimpleCondition)condition));
				sb.append(")");
		}

		return sb.toString();
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

			return convert.convertExpress(express);
		}else{
			throw new IllegalArgumentException("condition invalidFormat ,exp:"+express+" obj:"+obj);
		}
	}




	private String getFinalValue(ExpressCondition cond){
		return getFinalValue(cond.getExpress(),cond.getValue());
	}
	

}
