package com.kii.extension.ruleengine.sdk.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class KiiEntity {

	private String id;

	private int version;

	private String dataType;

	private String owner;

	private Date created;

	private Date modified;


	@JsonIgnore
	public String getId() {
		return id;
	}

	@JsonSetter("_id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public int getVersion() {
		return version;
	}

	@JsonSetter("_version")
	public void setVersion(int version) {
		this.version = version;
	}

	@JsonIgnore
	public String getDataType() {
		return dataType;
	}

	@JsonSetter("_dataType")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@JsonIgnore
	public String getOwner() {
		return owner;
	}

	@JsonSetter("_owner")
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@JsonIgnore
	public Date getCreated() {
		return created;
	}

	@JsonSetter("_created")
	public void setCreated(Date created) {
		this.created = created;
	}

	@JsonIgnore
	public Date getModified() {
		return modified;
	}

	@JsonSetter("_modified")
	public void setModified(Date modified) {
		this.modified = modified;
	}
}
