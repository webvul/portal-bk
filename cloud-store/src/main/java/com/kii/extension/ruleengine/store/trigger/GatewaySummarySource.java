package com.kii.extension.ruleengine.store.trigger;

public class GatewaySummarySource extends SummarySource{

	private VendorThingList sourceVendorThing = new VendorThingList();


	public VendorThingList getSourceVendorThing() {
		return sourceVendorThing;
	}

	public void setSourceVendorThing(VendorThingList sourceVendorThing) {
		this.sourceVendorThing = sourceVendorThing;
	}
}
