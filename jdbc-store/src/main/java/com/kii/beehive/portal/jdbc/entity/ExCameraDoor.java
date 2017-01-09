package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class ExCameraDoor extends BusinessEntity {

	private Long door_thing_id;
	private Long face_thing_id;
	private String camera_id;

	@JdbcField(column = "id")
	public Long getId() {
		return super.getId();
	}

	@JdbcField(column = "door_thing_id")
	public Long getDoor_thing_id() {
		return door_thing_id;
	}

	public void setDoor_thing_id(Long door_thing_id) {
		this.door_thing_id = door_thing_id;
	}
	@JdbcField(column = "face_thing_id")
	public Long getFace_thing_id() {
		return face_thing_id;
	}

	public void setFace_thing_id(Long face_thing_id) {
		this.face_thing_id = face_thing_id;
	}

	@JdbcField(column = "camera_id")
	public String getCamera_id() {
		return camera_id;
	}

	public void setCamera_id(String camera_id) {
		this.camera_id = camera_id;
	}
}
