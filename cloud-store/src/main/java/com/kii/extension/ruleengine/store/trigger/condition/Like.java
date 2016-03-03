package com.kii.extension.ruleengine.store.trigger.condition;


import com.kii.extension.ruleengine.store.trigger.ConditionType;

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
