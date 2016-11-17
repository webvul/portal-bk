package com.kii.extension.ruleengine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ExpressTool {

	private static Logger log= LoggerFactory.getLogger(ExpressTool.class);



	private static ExpressionParser parser = new SpelExpressionParser();

	public static Number  getNumValue(Object store,String field){

		String express=getFullFieldPath(field);

		StandardEvaluationContext context = new StandardEvaluationContext(store);

		 return   parser.parseExpression(express).getValue(
				 context, Number.class);

	}

	public static Object  getValue(Object store,String field){

		String express=getFullFieldPath(field);

		StandardEvaluationContext context = new StandardEvaluationContext(store);

		return   parser.parseExpression(express).getValue(
				context, Object.class);

	}


	private  static  String  getFullFieldPath(String field){

		int idx=StringUtils.indexOfAny(field,".","[");
		if(idx==-1){
			idx=field.length();
		}
		String prefix= StringUtils.substring(field,0,idx);
		String fullField="values['"+prefix+"']"+StringUtils.substring(field,idx);

		log.debug("fullField:"+fullField);
		return fullField;
	}

}
