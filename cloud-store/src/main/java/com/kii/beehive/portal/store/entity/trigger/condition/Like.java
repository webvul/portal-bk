package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.ConditionType;

public class Like extends ExpressCondition {

	@Override
	public ConditionType getType() {
		return ConditionType.like;
	}


	public Like(){

	}

	public Like(String field,String val){
		this();
		setField(field);
		setValue(val.substring(1,val.length()-1));
	}



}
