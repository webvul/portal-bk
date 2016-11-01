package com.kii.extension.sdk.entity.trigger;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "eventSource")
@JsonSubTypes({
		@JsonSubTypes.Type(value = SchedulePredicate.class,name="schedule"),
		@JsonSubTypes.Type(value = StatePredicate.class,name="states"),
		@JsonSubTypes.Type(value = TaskPredicate.class,name="schedule_once"),
})
public abstract   class Predicate {

	private EventSourceType eventSource;

	abstract public EventSourceType getEventSource() ;

	public void setEventSource(EventSourceType eventSource) {
		this.eventSource = eventSource;
	}
}
