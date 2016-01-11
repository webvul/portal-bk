package com.kii.beehive.business.ruleengine;

import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.condition.Equal;
import com.kii.extension.sdk.query.condition.Range;
import com.kii.extension.sdk.query.condition.SimpleCondition;

@Component
public class ExpressConvert {

	public boolean doExpress(Condition condition,Map<String,Object> content){






	}

	public String convert(Condition condition){


		StringBuilder sb=new StringBuilder();

		switch(condition.getType()){

			case and:




		}

	}

	private boolean doExpress(SimpleCondition clause,Map<String,Object> content)throws NumberFormatException{

		String field=clause.getField();

		Object source=content.get(field);

		switch(clause.getType()){

			case eq:
				Equal  eq=(Equal)clause;
				Object target=getValue(eq.getValue(),content);

				return compareToValue(source,target)==0;

			case range:

				Range range=(Range)clause;



		}


	}



	private int compareToValue(Object source,Object target){

		if(source instanceof String){
			return ((String)source).compareTo(String.valueOf(target));
		}

		if(source instanceof Integer||
				source instanceof  Long){
			Long sourceVal=(Long)source;
			Long targetVal=getLongValue(target);
			return sourceVal.compareTo(targetVal);
		}else if(source instanceof  Float||
				source instanceof  Double){

			Double sourceVal=(Double)source;
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

		if(str.startsWith("${")&&str.endsWith("}")){

			String key=str.substring(2,str.length()-1);
			return content.get(key);

		}else{
			return str;
		}



	}


}
