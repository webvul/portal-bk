package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class GlobalThingEntity extends DBEntity{

	private String vendorThingID;

	private String kiiAppID;

	private String type;

	private String status;

	private String custom;


	@Override
	@JdbcField(column="id_global_thing")
	public long getId(){
		return super.getId();
	}

	@JdbcField(column="vendor_thing_id")
	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	@JdbcField(column="kii_app_id")
	public String getKiiAppID() {
		return kiiAppID;
	}

	public void setKiiAppID(String kiiAppID) {
		this.kiiAppID = kiiAppID;
	}

	@JdbcField(column="thing_type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JdbcField(column = "status",type= JdbcFieldType.Json)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JdbcField(column = "custom_info",type= JdbcFieldType.Json)
	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}
}
