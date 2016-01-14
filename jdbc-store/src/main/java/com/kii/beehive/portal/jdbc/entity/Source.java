package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class Source extends DBEntity {


	private String name;
	private SourceType type;
	
	public final static String SOURCE_ID = "source_id";
	public final static String TYPE = "type";
	public final static String NAME = "name";
	
	@Override
	@JdbcField(column=SOURCE_ID)
	public Long getId(){
		return super.getId();
	}
	
	@JdbcField(column = TYPE,type= JdbcFieldType.EnumStr)
	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	@JdbcField(column=NAME)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
