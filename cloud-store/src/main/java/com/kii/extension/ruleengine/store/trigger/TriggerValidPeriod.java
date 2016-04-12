package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SimplePeriod.class,name="Simple"),
		@JsonSubTypes.Type(value = SchedulePeriod.class,name="Schedule"),
})
public interface TriggerValidPeriod {

	public String getType();

}
