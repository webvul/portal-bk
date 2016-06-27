package com.kii.extension.ruleengine.store.trigger;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type",
		defaultImpl=CommandToThing.class )
@JsonSubTypes({
		@JsonSubTypes.Type(value = CommandToThing.class,name="ThingCommand"),
		@JsonSubTypes.Type(value = CallHttpApi.class,name="HttpApiCall")
})
public interface ExecuteTarget {


	String  getType();


}
