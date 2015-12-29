package com.kii.beehive.portal.common.utils;

public class ThingIDTools {

	public static final String  joinFullKiiThingID(String kiiThingID,String kiiAppID) {
		return kiiAppID + "-" + kiiThingID;
	}

	public static final String[]  splitFullKiiThingID(String fullKiiThingID) {

		int idx=fullKiiThingID.indexOf("-");

		String  kiiThingID=fullKiiThingID.substring(idx+1);
		String appID=fullKiiThingID.substring(0,idx);

		return new String[]{appID,kiiThingID};
	}
}
