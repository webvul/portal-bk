package com.kii.beehive.portal.store.entity.trigger.groups;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.store.entity.trigger.schedule.TimerUnitType;

public class SlideFuntion {


	private SlideType  type;


	private TimerUnitType timeUnit;

	private int interval;

	private int length;

	public SlideType getType() {
		return type;
	}

	public void setType(SlideType type) {
		this.type = type;
	}

	public TimerUnitType getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimerUnitType timeUnit) {

		this.timeUnit = timeUnit;

	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@JsonIgnore
	public String getWindowDefine() {

		if(type==SlideType.length){

			return String.valueOf(length);
		}else{
			return timeUnit.getFullDescrtion(interval);
		}
	}
	
	public static enum SlideType {

		length,time;
	}
}
