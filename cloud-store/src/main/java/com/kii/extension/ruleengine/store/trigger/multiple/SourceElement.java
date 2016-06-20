package com.kii.extension.ruleengine.store.trigger.multiple;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = GroupSummarySource.class,name="summary"),
		@JsonSubTypes.Type(value = ThingSource.class,name="thing"),
})
public interface SourceElement {


	public SourceElementType getType();


	public static enum SourceElementType{
		thing,summary;
	}




}
