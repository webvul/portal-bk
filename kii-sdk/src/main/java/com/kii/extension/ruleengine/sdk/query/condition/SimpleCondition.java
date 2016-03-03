package com.kii.extension.ruleengine.sdk.query.condition;


import com.kii.extension.ruleengine.sdk.query.Condition;


public abstract  class SimpleCondition implements Condition {
	
	public SimpleCondition(){
		super();
	}
	

	private String field;
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	

}
