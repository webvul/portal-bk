package com.kii.beehive.portal.entitys;

import java.util.Date;
import java.util.Map;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;

public class ThingStatusMsg {
	
	
	private final String  vendorThingID;
	
	private final String appID;
	
	private final String kiiThingID;
	
	private final long thingID;
	
	private final Map<String,Object> status;
	
	private final String statusBody;
	
	private final Date timestamp;
	
	public ThingStatusMsg(GlobalThingInfo globalThingInfo, String body,Date timestamp) {
		this.statusBody=body;
		this.timestamp=timestamp;
		this.status=globalThingInfo.getStatus();
		this.thingID=globalThingInfo.getId();
		this.kiiThingID=globalThingInfo.getKiiThingID();
		this.appID=globalThingInfo.getKiiAppID();
		this.vendorThingID=globalThingInfo.getVendorThingID();
	}
	
	public ThingStatusMsg(ThingStatusMsg msg){
		this.statusBody=msg.statusBody;
		this.timestamp=msg.timestamp;
		this.status=msg.status;
		this.thingID=msg.thingID;
		this.kiiThingID=msg.kiiThingID;
		this.appID=msg.appID;
		this.vendorThingID=msg.vendorThingID;
	}
	
	public String getVendorThingID() {
		return vendorThingID;
	}
	
	public ThingStatusMsg(ThingStatusMsg msg){
		this.statusBody=msg.statusBody;
		this.timestamp=msg.timestamp;
		this.status=msg.status;
		this.thingID=msg.thingID;
		this.kiiThingID=msg.kiiThingID;
		this.appID=msg.appID;;
	}
	
	
	
	public String getAppID() {
		return appID;
	}
	
	
	public String getKiiThingID() {
		return kiiThingID;
	}
	
	
	public long getThingID() {
		return thingID;
	}
	
	
	public Map<String,Object> getStatus() {
		return status;
	}
	
	
	public String getStatusBody() {
		return statusBody;
	}
	
	
	public Date getTimestamp() {
		return timestamp;
	}
	
}
