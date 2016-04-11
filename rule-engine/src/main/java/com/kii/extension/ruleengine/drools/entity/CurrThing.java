package com.kii.extension.ruleengine.drools.entity;

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
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CurrThing currThing = (CurrThing) o;

		return thingID != null ? thingID.equals(currThing.thingID) : currThing.thingID == null;

	}

	@Override
	public int hashCode() {
		return thingID != null ? thingID.hashCode() : 0;
	}
}
