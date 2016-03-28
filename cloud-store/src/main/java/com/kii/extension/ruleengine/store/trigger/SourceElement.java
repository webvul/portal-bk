package com.kii.extension.ruleengine.store.trigger;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SummarySource.class,name="thing"),
		@JsonSubTypes.Type(value = GroupSource.class,name="group"),
		@JsonSubTypes.Type(value = ThingSource.class,name="summary"),
})
public interface SourceElement {


	public SourceElementType getType();


	public static enum SourceElementType{
		thing,group,summary;
	}



}
