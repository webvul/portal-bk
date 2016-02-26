package com.kii.extension.sdk.query.condition;

import com.kii.extension.sdk.query.ConditionType;

public class Like extends SimpleCondition{

	@Override
	public ConditionType getType() {
		return ConditionType.like;
	}


	public Like(){

	}

	public Like(String field,String val){
		this();
		setField(field);
		setLike(val.substring(1,val.length()-1));
	}

	private String like;


	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}


}
