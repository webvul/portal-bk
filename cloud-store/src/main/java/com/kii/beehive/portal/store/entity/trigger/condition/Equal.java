package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.ConditionType;

public class Equal extends SimpleCondition {

	@Override
	public ConditionType getType() {
		return ConditionType.eq;
	}

	public Equal(){

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
