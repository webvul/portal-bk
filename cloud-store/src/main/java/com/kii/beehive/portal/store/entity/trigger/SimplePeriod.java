package com.kii.beehive.portal.store.entity.trigger;

public class SimplePeriod implements   AbstractPeriod{

	private long startAt;

	private int  interval;

	private long duration=0;

	public long getStartAt() {
		return startAt;
	}

	public void setStartAt(long startAt) {
		this.startAt = startAt;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getType(){
		return "Simple";
	};
}
