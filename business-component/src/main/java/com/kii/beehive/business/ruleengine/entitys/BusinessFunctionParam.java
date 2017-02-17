package com.kii.beehive.business.ruleengine.entitys;

import java.util.ArrayList;
import java.util.List;

public class BusinessFunctionParam {
	
	
	private String beanName;
	
	private String functionName;
	
	private List<String> paramList=new ArrayList<>();
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public List<String> getParamList() {
		return paramList;
	}
	
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}
}
