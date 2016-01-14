package com.kii.beehive.business.ruleengine;

import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.condition.AndLogic;
import com.kii.extension.sdk.query.condition.Equal;
import com.kii.extension.sdk.query.condition.LogicCol;
import com.kii.extension.sdk.query.condition.NotLogic;
import com.kii.extension.sdk.query.condition.OrLogic;
import com.kii.extension.sdk.query.condition.Range;
import com.kii.extension.sdk.query.condition.SimpleCondition;

@Component
public class ExpressCompute {


	public boolean doExpress(Condition clause,final Map<String,Object> content)throws NumberFormatException{



		switch(clause.getType()){

			case and: {
				boolean sign = true;
				for(Condition  cond:((AndLogic) clause).getClauses()){
					sign&=doExpress(cond,content);
				};
				return sign;
			}
			case or:{
				boolean sign = false;
				for(Condition  cond:((OrLogic) clause).getClauses()){
					sign|=doExpress(cond,content);
				};
				return sign;
			}
			case not:{
				NotLogic logic=(NotLogic)clause;
				return !doExpress(logic.getClause(),content);
			}
			case eq: {
				Equal eq = (Equal) clause;
				String field = eq.getField();
				Object source = content.get(field);

				Object target = getValue(eq.getValue(), content);

				return compareToValue(source, target) == 0;
			}
			case range: {
				Range range = (Range) clause;
				String field = range.getField();
				Object source = content.get(field);

				if (range.getUpperLimit() != null) {
					Object upper=getValue(range.getUpperLimit(),content);

					int sign = compareToValue(source, upper);
					if (sign == 0 && range.isUpperIncluded()) {
						return true;
					}
					return sign < 0;
				}

				if (range.getLowerLimit() != null) {
					Object lower=getValue(range.getLowerLimit(),content);
					int sign = compareToValue(source, lower);
					if (sign == 0 && range.isLowerIncluded()) {
						return true;
					}
					return sign > 0;
				}
			}
			default:
				throw new IllegalArgumentException("unsupported express");
		}


	}



	private int compareToValue(Object source,Object target){

		if(source instanceof String){
			return ((String)source).compareTo(String.valueOf(target));
		}

		if(source instanceof Integer||
				source instanceof  Long){
			Long sourceVal=getLongValue(source);
			Long targetVal=getLongValue(target);
			return sourceVal.compareTo(targetVal);
		}else if(source instanceof  Float||
				source instanceof  Double){

			Double sourceVal=getDoubleValue(source);
			Double targetVal=getDoubleValue(target);
			double detal=sourceVal*0.01;
			if(Math.abs(sourceVal-targetVal)<detal){
				return 0;
			}else{
				return sourceVal.compareTo(targetVal);
			}
		}else if(source instanceof Boolean){
			Boolean sourceVal=(Boolean)source;
			Boolean targetVal=getBooleanValue(target);

			return sourceVal.compareTo(targetVal);
		}else {
			throw new NumberFormatException("unknown type,"+source);
		}


	}

	private Long getLongValue(Object target){
		if(target instanceof  String){
			return new Long((String)target);
		}else if(target instanceof Number){
			return ((Number)target).longValue();
		}else if(target instanceof  Boolean){
			return  ((Boolean)target).booleanValue()?1l:0l;
		}else{
			throw new NumberFormatException("unknown type,"+target.getClass());
		}
	}



	private Boolean getBooleanValue(Object target){
		if(target instanceof  String){
			return new Boolean((String)target);
		}else if(target instanceof Number){
			return ((Number)target).intValue()>0;
		}else if(target instanceof  Boolean){
			return  (Boolean)target;
		}else{
			throw new NumberFormatException("unknown type,"+target.getClass());
		}
	}

	private Double getDoubleValue(Object target){
		if(target instanceof  String){
			return new Double((String)target);
		}else if(target instanceof Number){
			return ((Number)target).doubleValue();
		}else if(target instanceof  Boolean){
			return  ((Boolean)target).booleanValue()?1d:0d;
		}else{
			throw new NumberFormatException("unknown type,"+target.getClass());
		}
	}

	private Object getValue(Object val,Map<String,Object> content){

		if(val==null){
			return null;
		}

		if(val instanceof Number||val instanceof  Boolean ){
			return val;
		}

		String str=(String)val;

		if(str.startsWith("$(")&&str.endsWith(")")){

			String key=str.substring(2,str.length()-1);
			return content.get(key);

		}else{
			return str;
		}



	}


}
