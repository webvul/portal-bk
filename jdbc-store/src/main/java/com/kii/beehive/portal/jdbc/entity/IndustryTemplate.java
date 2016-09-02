package com.kii.beehive.portal.jdbc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class IndustryTemplate extends BusinessEntity {

	public enum SchemaType { industrytemplate,device,haystack }
	private SchemaType schemaType;
	private String thingType;
	private String name;
	private String version;
	private String content;

	public final static String ID = "id";

	public static final String NAME = "name";

	public static final String SCHEMA_TYPE = "schema_type";

	public static final String THING_TYPE = "thing_type";

	public static final String VERSION = "version";

	public static final String CONTENT = "content";


	@Override
	@JdbcField(column=ID)
	public Long getId(){
		return super.getId();
	}
	
	@JdbcField(column=NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@JdbcField(column=THING_TYPE)
	public String getThingType() {
		return thingType;
	}

	public void setThingType(String thingType) {
		this.thingType = thingType;
	}

	@JdbcField(column=SCHEMA_TYPE, type = JdbcFieldType.EnumStr)
	public SchemaType getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(SchemaType schemaType) {
		this.schemaType = schemaType;
	}

	@JdbcField(column=VERSION)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@JsonIgnore
	@JdbcField(column=CONTENT)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
