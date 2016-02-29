package com.kii.beehive.portal.store.entity.trigger;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.beehive.portal.store.entity.trigger.condition.*;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Range.class,name="range"),
		@JsonSubTypes.Type(value = All.class,name="all"),
		@JsonSubTypes.Type(value = InCollect.class,name="in"),
		@JsonSubTypes.Type(value = Like.class,name="like"),
		@JsonSubTypes.Type(value = Equal.class,name="eq"),
		@JsonSubTypes.Type(value = AndLogic.class,name="and"),
		@JsonSubTypes.Type(value = OrLogic.class,name="or"),
		@JsonSubTypes.Type(value = NotLogic.class,name="not"),

})
public  interface Condition {
	
	public ConditionType getType();


	
}
