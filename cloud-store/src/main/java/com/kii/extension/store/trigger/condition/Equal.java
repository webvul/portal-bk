package com.kii.extension.store.trigger.condition;


import com.kii.extension.store.trigger.ConditionType;

public class Equal extends ExpressCondition {

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
	


}
