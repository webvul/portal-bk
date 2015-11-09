package com.kii.extension.sdk.aop;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.service.AppBindToolResolver;


@Aspect
public class AppBindAspect {


	@Autowired
	private AppBindToolResolver  bindTool;

	@Before("execution com.kii.beehive.portal.service..*.*(..)  && @annotation(com.kii.extension.sdk.annotation.BindAppByName) ")
	public void beforeCallBindDao(BindAppByName appByName){


		AppChoice choice=new AppChoice();
		choice.setBindName(appByName.appBindSource());
		choice.setAppName(appByName.appName());
		choice.setSupportDefault(appByName.usingDefault());

		bindTool.setAppChoice(choice);

	}

	@Before("execution com.kii.beehive.portal.service..*.*(@com.kii.extension.sdk.annotation.AppBindParam(*)) ")
	public void  beforeCallBindFunction(JoinPoint joinPoint ){
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
		choice.setBindName(annotation.appBindSource());
		choice.setSupportDefault(annotation.usingDefault());

		bindTool.setAppChoice(choice);

	}


}
