package com.kii.extension.sdk.entity.thingif;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "eventSource")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SchedulePredicate.class,name="states"),
		@JsonSubTypes.Type(value = StatePredicate.class,name="schedule"),
		@JsonSubTypes.Type(value = TaskPredicate.class,name="task"),
})
public class Predicate {

	private EventSourceType  eventSource;

	public EventSourceType getEventSource() {
		return eventSource;
	}

	public void setEventSource(EventSourceType eventSource) {
		this.eventSource = eventSource;
	}
}
