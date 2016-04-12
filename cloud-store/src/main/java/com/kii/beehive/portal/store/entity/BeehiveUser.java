package com.kii.beehive.portal.store.entity;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BeehiveUser extends  PortalEntity {

	private String kiiUserID;

//	private String kiiLoginName;

	private String userName;

	private String phone;

	private String mail;

//	private String role;

	private String company;

	private String activityToken;

	private String userPassword;


	public String getUserID(){
		return super.getId();
	}


	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public BeehiveUser(){

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

//	public String getRole() {
//		return role;
//	}
//
//	public void setRole(String role) {
//		this.role = role;
//	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

//	public String getKiiLoginName() {
//		return kiiLoginName;
//	}
//
//	public void setKiiLoginName(String kiiLoginName) {
//		this.kiiLoginName = kiiLoginName;
//	}


	@JsonIgnore
	public String getDefaultPassword() {
		return DigestUtils.sha1Hex(getUserName()+"_username_"+getId() + "_beehive");
	}

	public String getHashedPwd(String pwd){
		return DigestUtils.sha1Hex(pwd+"_user_id"+getId()+"_beehive");

	}


	public BeehiveUser cloneView(){

		BeehiveUser user=new BeehiveUser();
		BeanUtils.copyProperties(this, user, "kiiUserID","activityToken","defaultPassword","userPassword");

		return user;

	}
}
