package com.kii.extension.ruleengine.thingtrigger;

import com.google.common.base.Objects;

public class CurrThing {

	private String thingID;

	public String getThingID() {
		return thingID;
	}

	public void setThingID(String thingID) {
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
