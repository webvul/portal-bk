package com.kii.beehive.business.ruleengine.entitys;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type",
		defaultImpl=SingleObjEngineSource.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = GroupSummaryEngineSource.class,name="summary"),
		@JsonSubTypes.Type(value = SingleObjEngineSource.class,name="object"),
})
public interface EngineSourceElement {


	public SourceElementType getType();


	public static enum SourceElementType{
		object,summary;
	}




}
