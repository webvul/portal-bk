package com.kii.beehive.business.ruleengine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.MethodTools;
import com.kii.extension.ruleengine.ExecuteParam;
import com.kii.extension.ruleengine.service.ExecuteResultDao;
import com.kii.extension.ruleengine.store.trigger.target.BusinessFunResponse;
import com.kii.extension.ruleengine.store.trigger.target.CallBusinessFunction;
import com.kii.extension.ruleengine.store.trigger.target.ExceptionInfo;

@Component
public class BusinessFunctionCallService {
	
	@Autowired
	private ApplicationContext  context;
	
	
	@Autowired
	private ExecuteResultDao resultDao;
	
	
	
	@Autowired
	private ResponseBuilder  builder;
	
	public void doBusinessFunCall(CallBusinessFunction function, String triggerID, ExecuteParam params) {
		
		BusinessFunResponse result=builder.getBusinessFunResponse(triggerID,params);
		
		try {
			Object bean = context.getBean(function.getBeanName());
			
			result.setBusinessClassName(bean.getClass().getName());
			
			Map<String, Object> triggerParams = params.getBusinessParams();
			
			Object[] paramArray = new Object[function.getParamList().size()];
			
			for (int i = 0; i < paramArray.length; i++) {
				
				paramArray[i] = triggerParams.get(function.getParamList().get(i));
			}
	
			result.setParamList(Arrays.asList(paramArray));
	
			result.setMethodName(function.getFunctionName());
			
			Method method= MethodTools.getMethodByName(bean.getClass(),function.getFunctionName(),paramArray.length);
		
			Object returnResult=method.invoke(bean,paramArray);
			
			result.setReturnValue(returnResult);
			
			resultDao.addEntity(result);
			
		} catch (IllegalAccessException|InvocationTargetException e) {
			
			result.setExceptionInfo(new ExceptionInfo(e));
		}catch(NoSuchBeanDefinitionException e){
			result.setExceptionInfo(new ExceptionInfo(e));
		}
		
	}
}
