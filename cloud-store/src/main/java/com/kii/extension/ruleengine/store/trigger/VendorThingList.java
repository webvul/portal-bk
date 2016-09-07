package com.kii.extension.ruleengine.store.trigger;

import java.util.ArrayList;
import java.util.List;

public class VendorThingList {

	private List<String> vendorThingIdList=new ArrayList<>();


	private List<Long> thingList=new ArrayList<>();


	public List<Long> getThingList() {
		return thingList;
	}

	public void setThingList(List<Long> thingList) {
		this.thingList = thingList;
	}


	public List<String> getVendorThingIdList() {
		return vendorThingIdList;
	}

	public void setVendorThingIdList(List<String> vendorThingIdList) {
		this.vendorThingIdList = vendorThingIdList;
	}

}
