package com.kii.extension.sdk.query;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.kii.extension.sdk.entity.thingif.SchedulePredicate;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.TaskPredicate;
import com.kii.extension.sdk.query.condition.All;
import com.kii.extension.sdk.query.condition.AndLogic;
import com.kii.extension.sdk.query.condition.Equal;
import com.kii.extension.sdk.query.condition.FieldExist;
import com.kii.extension.sdk.query.condition.InCollect;
import com.kii.extension.sdk.query.condition.NotLogic;
import com.kii.extension.sdk.query.condition.OrLogic;
import com.kii.extension.sdk.query.condition.PrefixLike;
import com.kii.extension.sdk.query.condition.Range;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Range.class,name="range"),
		@JsonSubTypes.Type(value = All.class,name="all"),
		@JsonSubTypes.Type(value = InCollect.class,name="in"),
		@JsonSubTypes.Type(value = FieldExist.class,name="hasField"),
		@JsonSubTypes.Type(value = PrefixLike.class,name="prefix"),
		@JsonSubTypes.Type(value = Equal.class,name="eq"),
		@JsonSubTypes.Type(value = AndLogic.class,name="and"),
		@JsonSubTypes.Type(value = OrLogic.class,name="or"),
		@JsonSubTypes.Type(value = NotLogic.class,name="not"),

})
public  interface Condition {
	
	public ConditionType getType();


	
}
