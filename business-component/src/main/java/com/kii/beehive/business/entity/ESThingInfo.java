package com.kii.beehive.business.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;

public class ESThingInfo {
	
	
	private String kiicloudThingID;
	
	private String vendorThingID;
	
	private String globalThingID;
	
	private String thingType;
	
	private String schemaName;
	
	private String schemaVersion;
	
	private String geoLocation;
	
	private int  floor;
	
	private String buildID;
	
	private String aliThingNo;
	
	private ESLocationTag locationTag=null;
	
	private Set<String> containLocs=new HashSet<>();
	
	private Set<String> userRights=new HashSet<>();

	
	public ESThingInfo(GlobalThingInfo thing, ThingGeo geo,String  userIDs,String  locs){
		vendorThingID=thing.getVendorThingID();
		globalThingID=String.valueOf(thing.getId());
		
		kiicloudThingID=thing.getFullKiiThingID();
		
		thingType=thing.getType();
		schemaName=thing.getSchemaName();
		schemaVersion=thing.getSchemaVersion();
		

		if(GlobalThingInfo.validVendorThingIDPattern.matcher(vendorThingID).find()){
			String loc= StringUtils.substring(vendorThingID,0,9);
			locationTag= ESLocationTag.getInstance(loc);
		};
	
		if(geo.getId()!=null) {
			
			geoLocation = String.format("%f10,%f10", geo.getLat(), geo.getLng());
			
			if(geo.getFloor()!=null) {
				floor = geo.getFloor();
			}
			
			buildID = geo.getBuildingID();
			
			aliThingNo = geo.getAliThingID();
			if (StringUtils.isNotBlank(userIDs)) {
				String[] ids = StringUtils.split(userIDs, ",");
				userRights.addAll(Arrays.asList(ids));
			}
		}
		
		if(StringUtils.isNotBlank(locs)) {
			String[] ids=StringUtils.split(locs,",");
			containLocs.addAll(Arrays.asList(ids));		}
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
	
	public Set<String> getContainLocs() {
		return containLocs;
	}
	
	public void setContainLocs(Set<String> containLocs) {
		this.containLocs = containLocs;
	}
	
	public ESLocationTag getLocationTag() {
		return locationTag;
	}
	
	public void setLocationTag(ESLocationTag locationTag) {
		this.locationTag = locationTag;
	}
	
	public String getKiicloudThingID() {
		return kiicloudThingID;
	}
	
	public void setKiicloudThingID(String kiicloudThingID) {
		this.kiicloudThingID = kiicloudThingID;
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




