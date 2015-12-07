package com.kii.extension.sdk.entity.thingif.conditions;

public class EqualCondition extends Condition {

	private String field;

	private Object value;

	@Override
	public ConditionType getType(){
		return ConditionType.eq;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
