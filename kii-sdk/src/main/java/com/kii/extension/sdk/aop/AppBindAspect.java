package com.kii.extension.sdk.aop;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.exception.ForbiddenException;
import com.kii.extension.sdk.exception.KiiCloudException;
import com.kii.extension.sdk.exception.SystemException;
import com.kii.extension.sdk.exception.UnauthorizedAccessException;


@Aspect
public class AppBindAspect {

	private Logger log= LoggerFactory.getLogger(AppBindAspect.class);

	@Autowired
	private AppBindToolResolver bindTool;




	@Pointcut("execution (* com.kii.extension.sdk.service.AbstractDataAccess+.*(..)  ) ")
	private void commDataAccess(){

	}

	@Pointcut("within (@com.kii.extension.sdk.annotation.BindAppByName  com.kii..* ) ")
	private void appBindWithAnnotation(){

	}


	@Pointcut("execution (*  com.kii..*(..,@com.kii.extension.sdk.annotation.AppBindParam (*),.. ))")
	private void bindWithParam(){

	}

	
	@Around("commDataAccess()  ||  appBindWithAnnotation() ")
	public Object aroundCallDataAccess(ProceedingJoinPoint pjp)throws Throwable{
		
		
		BindAppByName appByName=pjp.getTarget().getClass().getAnnotation(BindAppByName.class);
		
		if(appByName==null) {
			
			try {
				
				return pjp.proceed();
				
			} catch (Throwable throwable) {
				throw throwable;
			}
		}
		
		AppChoice choice=new AppChoice();
		
		if(!StringUtils.isEmpty(appByName.appBindSource())) {
			choice.setBindName(appByName.appBindSource());
		}
		choice.setAppName(appByName.appName());
		
		
		if(appByName.tokenBind()== TokenBindTool.BindType.Custom){
			choice.setTokenBindName(appByName.customBindName());
		}else {
			choice.setTokenBindName(appByName.tokenBind().name());
		}
		
		bindTool.pushAppChoice(choice);
		
		return retryFunction(pjp);
	}
	
	private Object retryFunction(ProceedingJoinPoint pjp) throws Throwable {
		int retry=3;
		
		Object result=null;
		KiiCloudException kiiCloudException=null;
		while(retry>0){
			
			
			try{
				
				result = pjp.proceed();
				break;
				
			}catch(UnauthorizedAccessException |ForbiddenException ex){
				
				bindTool.refreshToken();
				retry--;
				kiiCloudException=ex;
				
			}catch(SystemException e){
				//send kiicloud service alarm
				throw e;
			} catch(KiiCloudException kiie){
				if(kiie.getStatusCode()== 401 ){
					bindTool.refreshToken();
					retry--;
					kiiCloudException=kiie;
				}
			}catch (Throwable throwable) {
				throw throwable;
			}
			
		}
		
		bindTool.pop();
		
		if(retry==0&&kiiCloudException!=null){
			throw kiiCloudException;
		}
		return result;
	}

//	@Before("commDataAccess()  ||  appBindWithAnnotation() ")
//	public void beforeCallDataAccess(JoinPoint joinPoint){
//
//		BindAppByName appByName=joinPoint.getTarget().getClass().getAnnotation(BindAppByName.class);
//
//		if(appByName==null){
//			return;
//		}
//		AppChoice choice=new AppChoice();
//
//		if(!StringUtils.isEmpty(appByName.appBindSource())) {
//			choice.setBindName(appByName.appBindSource());
//		}
//		choice.setAppName(appByName.appName());
//
//
//		if(StringUtils.isNotBlank(appByName.tokenBind())){
//			choice.setTokenBindName(appByName.tokenBind());
//		}else {
//			if (appByName.bindAdmin()) {
//				choice.setTokenBindName(TokenBindTool.BindType.admin.name());
//			} else if (appByName.bindUser()) {
//				choice.setTokenBindName(TokenBindTool.BindType.user.name());
//			} else if (appByName.bindThing()) {
//				choice.setTokenBindName(TokenBindTool.BindType.thing.name());
//			}
//		}
//
//		bindTool.pushAppChoice(choice);
//	}
//
//
//	@After("commDataAccess() || appBindWithAnnotation() ")
//	public void afterCallDataAccess(JoinPoint joinPoint){
//
//		BindAppByName  appByName=joinPoint.getTarget().getClass().getAnnotation(BindAppByName.class);
//		if(appByName==null){
//			return;
//		}
//		bindTool.pop();
//
//	}
//
//
//
//	@AfterThrowing(pointcut = "commDataAccess() || appBindWithAnnotation()",throwing="ex")
//	public void  afterCallBindParam(JoinPoint joinPoint,Exception  ex){
//
//
//		if(ex instanceof UnauthorizedAccessException||
//				ex instanceof ForbiddenException ){
//
//			bindTool.refreshToken();
//
//		}
//
//	}
	
	
	@Around("bindWithParam()")
	public Object  aroundCallBindParam(ProceedingJoinPoint joinPoint )throws Throwable{
		
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Annotation[][] methodAnnotations = method.getParameterAnnotations();

		Object[] args=joinPoint.getArgs();

		boolean sign=false;
		for(int i=0;i<methodAnnotations.length;i++){

			for(Annotation anno:methodAnnotations[i]){
				if(anno instanceof AppBindParam){

					AppBindParam bind=(AppBindParam)anno;
					
					Object arg=args[i];
					AppChoice choice=new AppChoice();
					
					if(!StringUtils.isEmpty(bind.appBindSource())) {
						choice.setBindName(bind.appBindSource());
					}
					
					if(arg instanceof  AppInfo) {
						choice.setAppName( ((AppInfo)arg).getAppID() );
					}else if(arg instanceof  String){
						choice.setAppName((String)arg);
					}
					
					
					if(bind.tokenBind()== TokenBindTool.BindType.Custom){
						choice.setTokenBindName(bind.customBindName());
					}else {
						choice.setTokenBindName(bind.tokenBind().name());
					}
					
					bindTool.pushAppChoice(choice);
					sign=true;
					break;
				}
			}

			if(sign){
				break;
			}

		}
		
		return retryFunction(joinPoint);
		
		
	}



}
