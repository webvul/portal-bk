package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class ExSitSysBeehiveUserRel extends BusinessEntity {

	private String sit_sys_user_id;
	private String beehive_user_id;

	@Override
	@JdbcField(column = "id")
	public Long getId() {
		return super.getId();
	}


	@JdbcField(column = "sit_sys_user_id")
	public String getSit_sys_user_id() {
		return sit_sys_user_id;
	}

	public void setSit_sys_user_id(String sit_sys_user_id) {
		this.sit_sys_user_id = sit_sys_user_id;
	}
	@JdbcField(column = "beehive_user_id")
	public String getBeehive_user_id() {
		return beehive_user_id;
	}

	public void setBeehive_user_id(String beehive_user_id) {
		this.beehive_user_id = beehive_user_id;
	}
}
