package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class BeehiveUser extends DBEntity {

	private String kiiUserID;

	private String kiiLoginName;

	private String userName;

	private String phone;

	private String mail;

	private String role;

	private String company;

	private String activityToken;


	@JdbcField(column="activity_token" )
	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public BeehiveUser(){

	}

	@JdbcField(column = "beehive_user_id")
	public Long getUserID(){
		return super.getId();
	}

	public void setUserID(Long id){
		super.setId(id);
	}

	public String getKiiUserID() {
		return kiiUserID;
	}

	public void setKiiUserID(String kiiUserID) {
		this.kiiUserID = kiiUserID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getKiiLoginName() {
		return kiiLoginName;
	}

	public void setKiiLoginName(String kiiLoginName) {
		this.kiiLoginName = kiiLoginName;
	}


}