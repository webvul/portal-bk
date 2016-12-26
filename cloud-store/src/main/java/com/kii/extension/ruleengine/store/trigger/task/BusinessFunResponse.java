package com.kii.extension.ruleengine.store.trigger.task;

import java.util.ArrayList;
import java.util.List;

public class BusinessFunResponse extends  TriggerResult {
	@Override
	public String getType() {
		return "businessFun";
	}

	private Object returnValue;
	
	private String businessBeanName;

	private String businessClassName;
	
	private String methodName;

	private List<Object> paramList=new ArrayList<>();
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String beanName) {
		this.methodName = beanName;
	}
	
	public List<Object> getParamList() {
		return paramList;
	}

	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}

	public String getBusinessClassName() {
		return businessClassName;
	}

	public void setBusinessClassName(String businessClassName) {
		this.businessClassName = businessClassName;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
	
	public String getBusinessBeanName() {
		return businessBeanName;
	}
	
	public void setBusinessBeanName(String businessBeanName) {
		this.businessBeanName = businessBeanName;
	}
}
