package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.Condition;

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
