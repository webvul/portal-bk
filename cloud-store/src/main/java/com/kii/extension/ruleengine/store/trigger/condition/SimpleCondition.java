package com.kii.extension.ruleengine.store.trigger.condition;


import com.kii.extension.ruleengine.store.trigger.Condition;

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
