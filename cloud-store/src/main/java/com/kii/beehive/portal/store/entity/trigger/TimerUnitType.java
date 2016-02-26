package com.kii.beehive.portal.store.entity.trigger;

public enum TimerUnitType {

	Day("d"),Hour("h"),Minute("m"),Second("s");

	private String val;

	TimerUnitType(String val){
		this.val=val;
	}

	public String getFullDescrtion(int value){
		return String.valueOf(value)+val;
	}

}
