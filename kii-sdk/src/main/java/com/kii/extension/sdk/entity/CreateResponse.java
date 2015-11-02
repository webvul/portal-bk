package com.kii.extension.sdk.entity;

import java.util.Date;

public class CreateResponse {

	/*
	  "objectID" : {OBJECT_ID},
  "createdAt" : 1337039114613,
  "dataType" : "application/vnd.{APP_ID}.mydata+json"

	 */

	private String objectID;

	private Date createdAt;

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
