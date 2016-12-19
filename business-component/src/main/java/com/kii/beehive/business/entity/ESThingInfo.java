package com.kii.beehive.business.entity;

import java.util.Set;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;

public class ESThingInfo {
	
	private String vendorThingID;
	
	private String globalThingID;
	
	private String thingType;
	
	private String schemaName;
	
	private String schemaVersion;
	
	private String geoLocation;
	
	private int  floor;
	
	private String buildID;
	
	private String aliThingNo;
	
	private ESLocationTag locationTag;
	
	private Set<String> userRights;
	
	public ESThingInfo(){
		
	}
	
	public ESThingInfo(GlobalThingInfo thing, String loc, ThingGeo geo,Set<String> userIDs){
		vendorThingID=thing.getVendorThingID();
		globalThingID=String.valueOf(thing.getId());
		thingType=thing.getType();
		schemaName=thing.getSchemaName();
		schemaVersion=thing.getSchemaVersion();
		
		locationTag=new ESLocationTag(loc);
		
		geoLocation= String.format("%d10.7,%d10.7", geo.getLat(),geo.getLng());
		
		floor=geo.getFloor();
		
		buildID=geo.getBuildingID();
		
		aliThingNo=geo.getAliThingID();
		
		userRights=userIDs;
	}
	
	public Set<String> getUserRights() {
		return userRights;
	}
	
	public void setUserRights(Set<String> userRights) {
		this.userRights = userRights;
	}
	
	public String getVendorThingID() {
		return vendorThingID;
	}
	
	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}
	
	public String getGlobalThingID() {
		return globalThingID;
	}
	
	public void setGlobalThingID(String globalThingID) {
		this.globalThingID = globalThingID;
	}
	
	public String getThingType() {
		return thingType;
	}
	
	public void setThingType(String thingType) {
		this.thingType = thingType;
	}
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public String getSchemaVersion() {
		return schemaVersion;
	}
	
	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	
	public String getGeoLocation() {
		return geoLocation;
	}
	
	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void setFloor(int floor) {
		this.floor = floor;
	}
	
	public String getBuildID() {
		return buildID;
	}
	
	public void setBuildID(String buildID) {
		this.buildID = buildID;
	}
	
	public String getAliThingNo() {
		return aliThingNo;
	}
	
	public void setAliThingNo(String aliThingNo) {
		this.aliThingNo = aliThingNo;
	}
	
	public ESLocationTag getLocationTag() {
		return locationTag;
	}
	
	public void setLocationTag(ESLocationTag locationTag) {
		this.locationTag = locationTag;
	}
	
	/*
	"vendorThingID": {
          "type": "keyword"
        },
        "globalThingID": {
          "type": "keyword"
        },
        "thingType": {
          "type": "keyword"
        },
        "schemaName": {
          "type": "keyword"
        },
        "schemaVersion": {
          "type": "integer"
        },
        "geoLocation": {
          "type": "geo_point"
        },
        "floor": {
          "type": "integer"
        },
        "buildID": {
          "type": "keyword"
        },
        "aliThingNo": {
          "type": "keyword"
        },
        "floor": {
          "type": "integer"
        },
        "buildID": {
          "type": "keyword"
        },
        "aliThingNo": {
          "type": "keyword"
        },
        "uploadDate": {
          "type": "date",
          "format": "epoch_millis"
        },
        "locationTag": {
          "properties": {
            "building": {
              "type": "integer"
            },
            "floor": {
              "type": "integer"
            },
            "partition": {
              "type": "keyword"
            },
            "area": {
              "type": "keyword"
            },
            "site": {
              "type": "keyword"
            }
	 */
}




