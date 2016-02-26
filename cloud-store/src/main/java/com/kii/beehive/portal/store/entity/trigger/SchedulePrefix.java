package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = IntervalPrefix.class,name="Interval"),
		@JsonSubTypes.Type(value = SchedulePrefix.class,name="Cron"),
})
public interface SchedulePrefix {

	String getType();

}
