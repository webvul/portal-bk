package com.kii.beehive.portal.jdbc.entity;

import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class GlobalThingInfo extends DBEntity{

	private String vendorThingID;

	private String kiiAppID;

	private String type;

	private String status;

	private String custom;

	private String fullKiiThingID;

	private String schema;

	private int schemaVersion;
	
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

	@JdbcField(column="full_kii_thing_id")
	public String getFullKiiThingID() {
		return fullKiiThingID;
	}

	public String getKiiThingID(){
		return kiiThingID;
	}

	@JdbcField(column="thing_schema")
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	@JdbcField(column="thing_schema_version")
	public int getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	private String kiiThingID;

	public void setFullKiiThingID(String fullkiiThingID) {
		this.fullKiiThingID = fullkiiThingID;

		ThingIDTools.ThingIDCombine idCombine = ThingIDTools.splitFullKiiThingID(fullkiiThingID);

		this.kiiThingID=idCombine.kiiThingID;
		this.kiiAppID=idCombine.kiiAppID;
	}


	@Override
	public boolean equals(Object obj){

		if(obj == null){
			return false;
		}

		if(obj instanceof GlobalThingInfo){
			return this.getId()==(((GlobalThingInfo)obj).getId());
		}else{
			return false;
		}
	}


	@Override
	public int hashCode(){
		return (int) this.getId();

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
