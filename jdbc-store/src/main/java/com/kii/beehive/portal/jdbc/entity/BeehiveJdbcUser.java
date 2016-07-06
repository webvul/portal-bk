package com.kii.beehive.portal.jdbc.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class BeehiveJdbcUser extends DBEntity{


	private String kiiUserID;

	private String userName;

	private String phone;

	private String mail;

	private String displayName;

	private String  roleName;

	private Integer  faceSubjectId; // face++

	public Integer getFaceSubjectId() {
		return faceSubjectId;
	}

	public void setFaceSubjectId(Integer faceSubjectId) {
		this.faceSubjectId = faceSubjectId;
	}

	private String activityToken;

	private String userPassword;

	private String userID;



	@JdbcField(column = "beehive_user_id")
	@Override
	public Long getId(){

		return super.getId();
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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



	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@JsonIgnore
	public String getDefaultPassword() {
		return DigestUtils.sha1Hex(getUserName()+"_username_"+userID + "_beehive");
	}

	@JsonIgnore
	public String getHashedPwd(String pwd){
		return DigestUtils.sha1Hex(pwd+"_user_id"+userID+"_beehive");

	}


	public BeehiveJdbcUser cloneView(){

		BeehiveJdbcUser user=new BeehiveJdbcUser();
		BeanUtils.copyProperties(this, user, "kiiUserID","activityToken","defaultPassword","userPassword","id");

		return user;

	}
	

}
