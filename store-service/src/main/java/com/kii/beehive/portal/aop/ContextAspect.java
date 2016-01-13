package com.kii.beehive.portal.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.helper.PortalTokenService;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.beehive.portal.util.AuthInfoStore;

@Aspect
public class ContextAspect {

	@Pointcut("execution (* com.kii.extension.sdk.service.DataService.createObject(..)  ) ")
	private void commDataAdd(){

	}


	@Pointcut("execution (* com.kii.extension.sdk.service.DataService.update*(..)  ) ")
	private void commDataUpdate(){

	}

	@Pointcut("execution (* com.kii.extension.sdk.service.DataService.fullUpdate*(..)  ) ")
	private void commDataFullUpdate(){

	}

	@Before("commDataAdd()")
	public void beforeDataAdd(JoinPoint joinPoint){


		for(Object obj:joinPoint.getArgs()){

			if(obj instanceof PortalEntity){

				((PortalEntity)obj).setCreateBy(AuthInfoStore.getUserID());
				((PortalEntity)obj).setModifyBy(AuthInfoStore.getUserID());
				break;
			}
		};
	}

	@Before(" commDataUpdate() || commDataFullUpdate() ")
	public void beforeDataUpdate(JoinPoint joinPoint){


		for(Object obj:joinPoint.getArgs()){

			if(obj instanceof PortalEntity){
				((PortalEntity)obj).setModifyBy(AuthInfoStore.getUserID());
				break;
			}else if(obj instanceof Map){
				Map map=new HashMap();
				map.putAll((Map)obj);
				map.put("modifyBy",AuthInfoStore.getUserID());
				break;
			}
		};
	}
}
