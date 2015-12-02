package com.kii.beehive.portal.aop;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.helper.PortalTokenService;
import com.kii.beehive.portal.store.entity.PortalEntity;

@Aspect
public class ContextAspect {

	@Autowired
	private PortalTokenService tokenService;

	@Before("execution (*  com.kii.extension.sdk.service.AbstractDataAccess+.add*(..) )")
	public void beforeCallCreateFun(JoinPoint joinPoint){


		Object[] args=joinPoint.getArgs();

		for(Object arg:args){

			if(arg instanceof PortalEntity){
				((PortalEntity)arg).setCreateBy(tokenService.getUserDescription());
				((PortalEntity)arg).setModifyBy(tokenService.getUserDescription());
			}
		}

	}

	@Before("execution (* com.kii.extension.sdk.service.AbstractDataAccess+.update*(..) ) ")
	public void beforeCallUpdateFun(JoinPoint joinPoint){

		Object[] args=joinPoint.getArgs();

		for(Object arg:args){

			if(arg instanceof PortalEntity){
				((PortalEntity)arg).setModifyBy(tokenService.getUserDescription());
			}else if(arg instanceof Map){
				((Map)arg).put("modifyBy",tokenService.getUserDescription());
			}
		}

	}

}
