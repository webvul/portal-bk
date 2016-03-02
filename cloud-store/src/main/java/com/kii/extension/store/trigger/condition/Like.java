package com.kii.extension.store.trigger.condition;


import com.kii.extension.store.trigger.ConditionType;

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
