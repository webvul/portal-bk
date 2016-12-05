package com.kii.extension.ruleengine.store.trigger;

import com.kii.extension.ruleengine.store.trigger.groups.SummarySource;

public class GatewaySummarySource extends SummarySource {

	private VendorThingList sourceVendorThing = new VendorThingList();


	public VendorThingList getSourceVendorThing() {
		return sourceVendorThing;
	}

	public void setSourceVendorThing(VendorThingList sourceVendorThing) {
		this.sourceVendorThing = sourceVendorThing;
	}
}
