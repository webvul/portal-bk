package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class IndustryTemplate extends DBEntity {


	private String name;
	private String thingType;
	private String version;
	private String content;

	public final static String ID = "id";

	public static final String NAME = "name";

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
	@JdbcField(column=VERSION)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	@JdbcField(column=CONTENT)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
