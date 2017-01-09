package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class ExSitLock extends BusinessEntity {

	private String space_code;
	private Long lock_global_thing_id;

	@Override
	@JdbcField(column = "id")
	public Long getId() {
		return super.getId();
	}

	@JdbcField(column = "lock_global_thing_id")
	public Long getLock_global_thing_id() {
		return lock_global_thing_id;
	}

	public void setLock_global_thing_id(Long lock_global_thing_id) {
		this.lock_global_thing_id = lock_global_thing_id;
	}

	@JdbcField(column = "space_code")
	public String getSpace_code() {
		return space_code;
	}

	public void setSpace_code(String space_code) {
		this.space_code = space_code;
	}
}
