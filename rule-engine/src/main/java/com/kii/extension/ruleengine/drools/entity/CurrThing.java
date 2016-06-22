package com.kii.extension.ruleengine.drools.entity;

public class CurrThing {

	private boolean isInit=false;

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean init) {
		isInit = init;
	}

	@Override
	public String toString() {
		return "CurrThing{" +
				"isInit=" + isInit +
				'}';
	}

}
