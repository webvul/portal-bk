package com.kii.extension.sdk.query.condition;

import com.kii.extension.sdk.query.ConditionType;

public class Equal extends SimpleCondition{

	@Override
	public ConditionType getType() {
		return ConditionType.eq;
	}

	
	public Equal(String field,Object val){
		setField(field);
		setValue(val);
	}
	
	private Object value;


	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
