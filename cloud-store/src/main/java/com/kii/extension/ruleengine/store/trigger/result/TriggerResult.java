package com.kii.extension.ruleengine.store.trigger.result;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = HttpCallResponse.class,name="httpResponse"),
		@JsonSubTypes.Type(value = ExceptionResponse.class,name="exception"),
		@JsonSubTypes.Type(value= CommandResponse.class,name="command")
})
public interface TriggerResult {


	public String getType();


}
