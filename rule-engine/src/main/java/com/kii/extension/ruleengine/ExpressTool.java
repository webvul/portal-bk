package com.kii.extension.ruleengine;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ExpressTool {


	private static ExpressionParser parser = new SpelExpressionParser();

	public static Number  getNumValue(Object store,String express){

		StandardEvaluationContext context = new StandardEvaluationContext(store);

		 return   parser.parseExpression(express).getValue(
				 context, Number.class);

	}

	public static Object  getValue(Object store,String express){

		StandardEvaluationContext context = new StandardEvaluationContext(store);

		return   parser.parseExpression(express).getValue(
				context, Object.class);

	}

}
