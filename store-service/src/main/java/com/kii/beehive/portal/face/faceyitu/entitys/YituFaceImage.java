package com.kii.beehive.portal.face.faceyitu.entitys;


public class YituFaceImage {

	private Integer repository_id = 1;
	private String picture_image_content_base64;
	private String beehive_user_id;
	private String external_id;
	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRepository_id() {
		return repository_id;
	}

	public void setRepository_id(Integer repository_id) {
		this.repository_id = repository_id;
	}

	public String getPicture_image_content_base64() {
		return picture_image_content_base64;
	}

	public void setPicture_image_content_base64(String picture_image_content_base64) {
		this.picture_image_content_base64 = picture_image_content_base64;
	}

	public String getBeehive_user_id() {
		return beehive_user_id;
	}

	public void setBeehive_user_id(String beehive_user_id) {
		this.beehive_user_id = beehive_user_id;
	}

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
}
