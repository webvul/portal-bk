package com.kii.extension.ruleengine.store.trigger;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = IntervalPrefix.class,name="Interval"),
		@JsonSubTypes.Type(value = CronPrefix.class,name="Cron"),
})
public interface SchedulePrefix extends  Serializable{

	String getType();

}
