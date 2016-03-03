package com.kii.extension.sdk.entity;

import java.util.ArrayList;
import java.util.List;

public class BucketList {

	/*
	  "bucketIDs" : [ "_devportal_things", "_queries", "aboutInfo", "Firmwares", "FirmwareUpgrade_", "FirmwareUpgrade_03-01-0001", "FirmwareUpgrade_07-00-0001", "FirmwareUpgrade_07-01-0001", "FirmwareUpgrade_15-00-0001", "FirmwareUpgrade_YK15-MS" ],

	 */

	private List<String> bucketIDs=new ArrayList<>();

	private String nextPaginationKey;


	public List<String> getBucketIDs() {
		return bucketIDs;
	}

	public void setBucketIDs(List<String> bucketIDs) {
		this.bucketIDs = bucketIDs;
	}

	public String getNextPaginationKey() {
		return nextPaginationKey;
	}

	public void setNextPaginationKey(String nextPaginationKey) {
		this.nextPaginationKey = nextPaginationKey;
	}
}
