package com.kii.extension.sdk.entity.thingif;

public class TaskPredicate extends Predicate{

	private long timestamp;

	private long duration;

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public EventSourceType getEventSource() {
		return EventSourceType.schedule_once;
	}


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
