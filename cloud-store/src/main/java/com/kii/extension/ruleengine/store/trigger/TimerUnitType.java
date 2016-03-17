package com.kii.extension.ruleengine.store.trigger;

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
