package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.ConditionType;

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
