package com.kii.extension.sdk.aop;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.AppInfo;


@Aspect
public class AppBindAspect {


	@Autowired
	private AppBindToolResolver  bindTool;




	@Pointcut("execution (* com.kii.extension.sdk.service.AbstractDataAccess+.*(..)  ) ")
	private void commDataAccess(){

	}

	@Pointcut("within (@com.kii.extension.sdk.annotation.BindAppByName  com.kii..* ) ")
	private void appBindWithAnnotation(){


	}


	@Pointcut("execution (*  com.kii..*(..,@com.kii.extension.sdk.annotation.AppBindParam (*),.. ))")
	private void bindWithParam(){

	}


	@Before("commDataAccess()  ||  appBindWithAnnotation() ")
	public void beforeCallDataAccess(JoinPoint joinPoint){

		BindAppByName  appByName=joinPoint.getTarget().getClass().getAnnotation(BindAppByName.class);

		AppChoice choice=new AppChoice();
		if(!StringUtils.isEmpty(appByName.appBindSource())) {
			choice.setBindName(appByName.appBindSource());
		}
		choice.setAppName(appByName.appName());

		bindTool.setAppChoice(choice);

	}


	@After("commDataAccess() || appBindWithAnnotation() ")
	public void afterCallDataAccess(JoinPoint joinPoint){

		bindTool.clean();

	}


	@After("bindWithParam()")
	public void  afterCallBindParam(JoinPoint joinPoint ){
		bindTool.clean();
	}


	@Before("bindWithParam()")
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

					Object arg=args[i];
					if(arg instanceof AppInfo){
						bindTool.setAppInfoDirectly((AppInfo)arg);
						break;
					}else if(arg instanceof  String) {
						param = String.valueOf(args[i]);
						bindTool.setAppInfoDirectly(param);
						break;
					}
//					annotation=(AppBindParam)anno;
					break;
				}
			}

			if(param!=null){
				break;
			}

		}


//		if(param==null){
//			return;
//		}
//
//		AppChoice choice=new AppChoice();
//		choice.setAppName(param);
//		if(!StringUtils.isEmpty(annotation.appBindSource())){
//			choice.setBindName(annotation.appBindSource());
//		}
//		bindTool.setAppChoice(choice);

	}



}
