package com.kii.extension.sdk.entity;

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
