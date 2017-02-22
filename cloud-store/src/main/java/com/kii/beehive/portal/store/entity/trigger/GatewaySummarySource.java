package com.kii.beehive.portal.store.entity.trigger;

import com.kii.beehive.portal.store.entity.trigger.groups.SummarySource;

public class GatewaySummarySource extends SummarySource {

	private VendorThingList sourceVendorThing = new VendorThingList();


	public VendorThingList getSourceVendorThing() {
		return sourceVendorThing;
	}

	public void setSourceVendorThing(VendorThingList sourceVendorThing) {
		this.sourceVendorThing = sourceVendorThing;
	}
}
