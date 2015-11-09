package com.kii.beehive.portal.store.entity;

import java.util.Date;
import java.util.Map;
import java.util.Set;


public class LandLord {


	private String id;

	private String landLordName;

	private String password;


	private Date modifyDate;

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
