package com.kii.extension.ruleengine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ExpressTool {

	private static Logger log= LoggerFactory.getLogger(ExpressTool.class);



	private static ExpressionParser parser = new SpelExpressionParser();

	public static Number  getNumValue(Object store,String field){

		String express=getFullFieldPath(field);

		StandardEvaluationContext context = new StandardEvaluationContext(store);
		
		try {
			Number val = parser.parseExpression(express).getValue(
					context, Number.class);
			
			if (val == null) {
				return 0.0f;
			}
			return val;
		}catch(ExpressionException e){
			return 0.0f;
		}
			
	}

	public static <T>  T  getValue(Object store,String field,Class<T> cls){

		String express=getFullFieldPath(field);

		StandardEvaluationContext context = new StandardEvaluationContext(store);
		
		try {
		return   parser.parseExpression(express).getValue(
				context, cls);
		}catch(ExpressionException e){
			return null;
		}
	}
	
	public static Object getObjValue(Object store,String field){
		
		String express=getFullFieldPath(field);

		StandardEvaluationContext context = new StandardEvaluationContext(store);
		
		try{
			Object value=   parser.parseExpression(express).getValue(
					context, Object.class);
			if(value==null){
				return 0.0f;
			}else{
				return value;
			}
		}catch(ExpressionException e){
			return 0.0f;
		}
	}


	private  static  String  getFullFieldPath(String field){

		boolean isHistory=false;
		if(field.startsWith("previous.")){
			isHistory=true;
			field=StringUtils.substring(field,9);
		}
		
		int idx=StringUtils.indexOfAny(field,".","[");
		if(idx==-1){
			idx=field.length();
		}
		String prefix= StringUtils.substring(field,0,idx);
		String header=isHistory?"previous":"values";
		
		String fullField=header+"['"+prefix+"']"+StringUtils.substring(field,idx);

//		log.debug("fullField:"+fullField);
		return fullField;
	}

}
