package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class GlobalThingInfo extends DBEntity{

	private String vendorThingID;

	private String kiiAppID;

	private String type;

	private String status;

	private String custom;
	
	public final static String ID_GLOBAL_THING = "id_global_thing";
	public final static String VANDOR_THING_ID = "vendor_thing_id";
	public final static String KII_APP_ID = "kii_app_id";
	public final static String THING_TYPE = "thing_type";
	public final static String STATUS = "status";
	public final static String CUSTOM_INFO = "custom_info";
	


	@Override
	@JdbcField(column="id_global_thing")
	public long getId(){
		return super.getId();
	}

	@JdbcField(column=VANDOR_THING_ID)
	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	@JdbcField(column=KII_APP_ID)
	public String getKiiAppID() {
		return kiiAppID;
	}

	public void setKiiAppID(String kiiAppID) {
		this.kiiAppID = kiiAppID;
	}

	@JdbcField(column=THING_TYPE)
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GlobalThingInfo [vendorThingID=");
		builder.append(vendorThingID);
		builder.append(", kiiAppID=");
		builder.append(kiiAppID);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		builder.append(", custom=");
		builder.append(custom);
		builder.append("]");
		return builder.toString();
	}
	
	
}
