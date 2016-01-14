package com.kii.beehive.portal.jdbc.entity;

import java.util.Date;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class DBEntity {

	private Long id;

	private Date createDate;

	private Date modifyDate;

	private String createBy;

	private String modifyBy;
	
	public final static String CREATE_DATE = "create_date";
	public final static String CREATE_BY = "create_by";
	public final static String MODIFY_DATE = "modify_date";
	public final static String MODIFY_BY = "modify_by";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JdbcField(column=CREATE_DATE)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@JdbcField(column=MODIFY_DATE)
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@JdbcField(column=CREATE_BY)
	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	@JdbcField(column=MODIFY_BY)
	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}
}
