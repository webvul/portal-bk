package com.kii.beehive.portal.aop;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.annotation.AppBindParam;
import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppChoice;


@Aspect
public class AppBindAspect {


	@Autowired
	private AppBindToolResolver  bindTool;


	@Before("execution (* com.kii.extension.sdk.service.AbstractDataAccess+.*(..)  ) ")
	public void beforeCallDataAccess(JoinPoint joinPoint){

		BindAppByName  appByName=joinPoint.getTarget().getClass().getAnnotation(BindAppByName.class);

		AppChoice choice=new AppChoice();
		if(!StringUtils.isEmpty(appByName.appBindSource())) {
			choice.setBindName(appByName.appBindSource());
		}
		choice.setAppName(appByName.appName());

		bindTool.setAppChoice(choice);

	}

	@Before("execution (*  com.kii.beehive.portal.service..*(@com.kii.beehive.portal.annotation.AppBindParam (*) , .. ))")
	public void  beforeCallBindParam(JoinPoint joinPoint ){
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Annotation[][] methodAnnotations = method.getParameterAnnotations();

		Object[] args=joinPoint.getArgs();

		String param=null;
		AppBindParam annotation=null;
		for(int i=0;i<methodAnnotations.length;i++){

			for(Annotation anno:methodAnnotations[i]){
				if(anno instanceof AppBindParam){

					param=String.valueOf(args[i]);
					annotation= (AppBindParam) anno;
					break;
				}
			}

			if(param!=null){
				break;
			}

		}


		if(param==null){
			return;
		}

		AppChoice choice=new AppChoice();
		choice.setAppName(param);
		if(!StringUtils.isEmpty(annotation.appBindSource())){
			choice.setBindName(annotation.appBindSource());
		}
		bindTool.setAppChoice(choice);

	}


}
