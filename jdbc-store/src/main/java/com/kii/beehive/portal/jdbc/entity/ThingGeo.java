package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

/**
 * Created by USER on 7/31/16.
 */
public class ThingGeo extends BusinessEntity {

	private Long globalThingID;

	private String vendorThingID;

	private Double lng;

	private Double lat;

	private Integer floor;

	private String buildingID;

	private String aliThingID;

	private String description;

	private Integer geoType;

	public static final String ID = "id";

	public static final String GLOBAL_THING_ID = "global_thing_id";

	public static final String VENDOR_THING_ID = "vendor_thing_id";

	public static final String LNG = "lng";

	public static final String LAT = "lat";

	public static final String FLOOR = "floor";

	public static final String BUILDING_ID = "building_id";

	public static final String ALI_THING_ID = "ali_thing_id";

	public static final String DESCRIPTION = "description";

	public static final String GEO_TYPE = "geo_type";

	@Override
	@JdbcField(column=ID)
	public Long getId(){
		return super.getId();
	}

	@JdbcField(column=GLOBAL_THING_ID)
	public Long getGlobalThingID() {
		return globalThingID;
	}

	public void setGlobalThingID(Long globalThingID) {
		this.globalThingID = globalThingID;
	}

	@JdbcField(column=VENDOR_THING_ID)
	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	@JdbcField(column=LNG)
	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	@JdbcField(column=LAT)
	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	@JdbcField(column=FLOOR)
	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	@JdbcField(column=BUILDING_ID)
	public String getBuildingID() {
		return buildingID;
	}

	public void setBuildingID(String buildingID) {
		this.buildingID = buildingID;
	}

	@JdbcField(column=ALI_THING_ID)
	public String getAliThingID() {
		return aliThingID;
	}

	public void setAliThingID(String aliThingID) {
		this.aliThingID = aliThingID;
	}

	@JdbcField(column=DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JdbcField(column=GEO_TYPE)
	public Integer getGeoType() {
		return geoType;
	}

	public void setGeoType(Integer geoType) {
		this.geoType = geoType;
	}

	@Override
	public String toString() {
		return "ThingGeo{" +
				"globalThingID=" + globalThingID +
				", vendorThingID='" + vendorThingID + '\'' +
				", lng=" + lng +
				", lat=" + lat +
				", floor=" + floor +
				", buildingID='" + buildingID + '\'' +
				", aliThingID='" + aliThingID + '\'' +
				", description='" + description + '\'' +
				", geoType=" + geoType +
				'}';
	}
}
