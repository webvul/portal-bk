package com.kii.extension.store.trigger.condition;

public abstract class ExpressCondition extends  SimpleCondition {

	private Object value;

	private String express;

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
