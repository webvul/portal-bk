package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.ConditionType;

public class Like extends SimpleCondition {

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

	private String express;

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}


}
