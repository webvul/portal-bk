package com.kii.beehive.portal.store.entity;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class LandLord {


	@Id
	private String id;

	private String landLordName;

	private String password;


	@LastModifiedDate
	private Date modifyDate;

	@CreatedDate
	private Date createDate;


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLandLordName() {
		return landLordName;
	}

	public void setLandLordName(String landLordName) {
		this.landLordName = landLordName;
	}

}
