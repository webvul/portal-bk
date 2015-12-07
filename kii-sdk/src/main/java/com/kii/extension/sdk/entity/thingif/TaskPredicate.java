package com.kii.extension.sdk.entity.thingif;

public class TaskPredicate extends Predicate{

	private long timestamp;

	@Override
	public EventSourceType getEventSource() {
		return EventSourceType.task;
	}


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
