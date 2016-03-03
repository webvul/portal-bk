package com.kii.extension.ruleengine.sdk.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class UpdateResponse {

	private boolean isUpdate;



	private String version;

	private Date createdAt;

	private Date modifyAt;

	@JsonIgnore
	public String getVersion() {
		return version;
	}

	@JsonIgnore
	public int getVersionValue(){

		String value=version.substring(1,version.length()-1);

		return Integer.parseInt(value);
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifyAt() {
		return modifyAt;
	}

	public void setModifyAt(Date modifyAt) {
		this.modifyAt = modifyAt;
	}

	@JsonIgnore
	public boolean isUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
}
