package com.kii.beehive.portal.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
public class LogAspect {

	@Autowired
	private ObjectMapper mapper;


	private Logger log= LoggerFactory.getLogger("manager-log");


	@Before("within ( com.kii.beehive.portal.manager.* ) ")
	public void beforeCallBusinessFun(JoinPoint joinPoint){

		Method method=logMethod(joinPoint,"been called");

		StringBuilder sb=new StringBuilder("arg list:\n");

		for(int i=0;i<joinPoint.getArgs().length;i++){

			String name=method.getParameters()[i].getName();
			Object val=joinPoint.getArgs()[i];
			sb.append(name).append(":").append(safeToString(val)).append("\n");
		}
		log.debug(sb.toString());

	}

	private Method logMethod(JoinPoint joinPoint,String suffix) {
		String   clsName=joinPoint.getTarget().getClass().getName();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		log.debug(" method "+method+ "  in "+clsName+" "+suffix);
		return method;
	}

	@AfterReturning(pointcut = "within ( com.kii.beehive.portal.manager.* )",   returning = "result" )
	public void afterCallBusinessFun(JoinPoint joinPoint,Object result){

		logMethod(joinPoint,"execute finish ");

		log.debug(" result:"+ safeToString(result));
	}

	private String safeToString(Object obj){

		if(obj==null){
			return "null";
		}
		if(obj instanceof  Number){
			return String.valueOf(obj);
		}else if(obj instanceof String ){
			return (String)obj;
		}else if(obj.getClass().getPackage().getName().startsWith("com.kii")){
			try {
				return mapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				throw new IllegalArgumentException(e);
			}

		}else{
			return String.valueOf(obj);
		}

	}

}
