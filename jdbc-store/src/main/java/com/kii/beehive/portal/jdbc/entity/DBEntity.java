package com.kii.beehive.portal.jdbc.entity;

import java.util.Date;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class DBEntity {

	private long id;

	private Date createDate;

	private Date modifyDate;

	private String createBy;

	private String modifyBy;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JdbcField(column="create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@JdbcField(column="modify_date")
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@JdbcField(column="create_by")
	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@JdbcField(column="modify_by")
	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}
}
