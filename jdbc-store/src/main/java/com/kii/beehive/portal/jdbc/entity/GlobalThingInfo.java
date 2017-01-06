package com.kii.beehive.portal.jdbc.entity;

import java.util.Map;
import java.util.regex.Pattern;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class GlobalThingInfo extends BusinessEntity {
	
	public  final static Pattern validVendorThingIDPattern = Pattern.compile("^\\w{4}\\w-[A-Z][\\w]{2}-\\w{1}-\\d{3}$");
	
	
	public  final static Pattern locationPattern = Pattern.compile("^\\w{4}\\w-[A-Z][\\w]{2}$");
	
	
	private String vendorThingID;

	private String kiiAppID;

	private String type;

	private Map<String,Object> status;

	private Map<String,Object> consumer;

	private String fullKiiThingID;

	private String schemaName;

	private String schemaVersion;


	public final static String ID_GLOBAL_THING = "id_global_thing";
	public final static String VANDOR_THING_ID = "vendor_thing_id";
	public final static String KII_APP_ID = "kii_app_id";
	public final static String THING_TYPE = "thing_type";
	public final static String STATUS = "status";
	public final static String CUSTOM_INFO = "custom_info";
	public final static String FULL_KII_THING_ID = "full_kii_thing_id";
	public final static String SCHEMA_NAME = "schema_name";
	public final static String SCHEMA_VERSION = "schema_version";


	public final static String VIEW_NAME ="view_thing_user_ownership";
	public final static String VIEW_THING_ID="thing_id";
	public final static String VIEW_USER_ID="user_id";

	@Override
	@JdbcField(column = ID_GLOBAL_THING)
	public Long getId() {
		return super.getId();
	}

	@JdbcField(column = VANDOR_THING_ID)
	public String getVendorThingID() {
		return vendorThingID;
	}

	public void setVendorThingID(String vendorThingID) {
		this.vendorThingID = vendorThingID;
	}

	@JdbcField(column = KII_APP_ID)
	public String getKiiAppID() {
		return kiiAppID;
	}

	public void setKiiAppID(String kiiAppID) {
		this.kiiAppID = kiiAppID;
	}

	@JdbcField(column = THING_TYPE)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JdbcField(column = STATUS, type = JdbcFieldType.Json)
	public  Map<String,Object> getStatus() {
		return status;
	}

	public void setStatus( Map<String,Object> status) {
		this.status = status;
	}

	@JdbcField(column = CUSTOM_INFO, type = JdbcFieldType.Json)
	public  Map<String,Object> getCustom() {
		return consumer;
	}

	public void setCustom( Map<String,Object> custom) {
		this.consumer = custom;
	}

	@JdbcField(column = FULL_KII_THING_ID)
	public String getFullKiiThingID() {
		return fullKiiThingID;
	}

	@JdbcField(column = SCHEMA_NAME)
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@JdbcField(column = SCHEMA_VERSION)
	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public String getKiiThingID() {
		return kiiThingID;
	}


	private String kiiThingID;

	public void setFullKiiThingID(String fullkiiThingID) {
		this.fullKiiThingID = fullkiiThingID;

		ThingIDTools.ThingIDCombine idCombine = ThingIDTools.splitFullKiiThingID(fullkiiThingID);

		this.kiiThingID = idCombine.kiiThingID;
		this.kiiAppID = idCombine.kiiAppID;
	}

	public void fillFullKiiThingID(){

		this.fullKiiThingID=ThingIDTools.joinFullKiiThingID(kiiAppID,vendorThingID);

	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof GlobalThingInfo) {
			GlobalThingInfo info = (GlobalThingInfo) obj;

			return this.getId().equals(info.getId());
		} else {
			return false;
		}
	}


	@Override
	public int hashCode() {
		return this.getId().intValue();

	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GlobalThingInfo [vendorThingID=");
		builder.append(vendorThingID);
		builder.append(", globalThingID=");
		builder.append(getId());
		builder.append(", kiiAppID=");
		builder.append(kiiAppID);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		builder.append(", custom=");
		builder.append(consumer);
		builder.append(", fullKiiThingID=");
		builder.append(fullKiiThingID);
		builder.append("]");
		return builder.toString();
	}


}
