package com.kii.beehive.portal.store.entity.trigger.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.store.entity.trigger.ExecuteTarget;

public class CallBusinessFunction extends ExecuteTarget {
	@Override
	public TargetType getType() {
		return TargetType.CallBusinessFunction;
	}

	private String beanName;

	private String functionName;

	private List<String> paramList=new ArrayList<>();


	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	@JsonIgnore
	public void setParamArrays(String... params){
		paramList.addAll(Arrays.asList(params));

	}

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
}
