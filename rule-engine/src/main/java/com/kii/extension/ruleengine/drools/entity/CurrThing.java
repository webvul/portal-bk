package com.kii.extension.ruleengine.drools.entity;

import java.util.Objects;

public class CurrThing {

	private String thingID;

	public String getThing() {
		return thingID;
	}

	public void setThing(String thingID) {
		this.thingID = thingID;
	}

	@Override
	public String toString() {
		return "CurrThing{" +
				"thingID='" + thingID + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode("thingID");
	}
}
