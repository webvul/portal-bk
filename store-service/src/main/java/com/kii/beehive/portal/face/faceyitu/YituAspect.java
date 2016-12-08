package com.kii.beehive.portal.face.faceyitu;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class YituAspect {

	@Autowired
	private ObjectMapper mapper;


	private Logger log= LoggerFactory.getLogger(YituAspect.class);

	@Pointcut("execution (* com.kii.beehive.portal.face.faceyitu.YituFaceService.do*(..)  ) ")
	private void bindFaceApi(){
	}

	@Before("bindFaceApi()")
	public void Before(JoinPoint joinPoint){
	}

	@AfterThrowing(pointcut = "bindFaceApi()", throwing = "ex")
	public void AfterThrowing(JoinPoint joinPoint,Exception ex){

		if( ex instanceof FaceYituException){

			((YituFaceService)joinPoint.getTarget()).loginServer();
		}

	}


}
