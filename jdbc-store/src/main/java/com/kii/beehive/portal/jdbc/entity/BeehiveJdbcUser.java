package com.kii.beehive.portal.jdbc.entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class BeehiveJdbcUser extends BusinessEntity {


	private String kiiUserID;

	private String userName;

	private String phone;

	private String mail;

	private String displayName;

	private String roleName;


	private String activityToken;

	private String userPassword;

	private String userID;


	private Integer faceSubjectId; // face++

	private Boolean enable;

	@JdbcField(column = "enable")
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

//
//	public Boolean getEnable() {
//		return enable;
//	}
//
//	public void setEnable(Boolean enable) {
//		this.enable = enable;
//	}

	@JdbcField(column = "face_subject_id")
	public Integer getFaceSubjectId() {
		return faceSubjectId;
	}

	public void setFaceSubjectId(Integer faceSubjectId) {
		this.faceSubjectId = faceSubjectId;
	}


	@JdbcField(column = "beehive_user_id")
	@Override
	public Long getId() {

		return super.getId();
	}

	@JdbcField(column = "user_id")
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@JdbcField(column = "user_password")
	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	@JdbcField(column = "activity_token")
	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	@JdbcField(column = "kii_user_id")
	public String getKiiUserID() {
		return kiiUserID;
	}

	public void setKiiUserID(String kiiUserID) {
		this.kiiUserID = kiiUserID;
	}

	@JdbcField(column = "user_name")
	public String getUserName() {
		return userName;
	}


	@JsonIgnore
	public String getKiiCloudLoginName() {


		return StringUtils.replacePattern(userName, "\\W", ".");

	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JdbcField(column = "mobile")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@JdbcField(column = "user_mail")
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}


	@JdbcField(column = "role_name")
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@JdbcField(column = "display_name")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@JsonIgnore
	public String getDefaultPassword() {
		return DigestUtils.sha1Hex(getUserName() + "_username_" + userID + "_beehive");
	}

	@JsonIgnore
	public String getHashedPwd(String pwd) {
		return DigestUtils.sha1Hex(pwd + "_user_id" + userID + "_beehive");

	}


	public BeehiveJdbcUser cloneView() {

		BeehiveJdbcUser user = new BeehiveJdbcUser();
		BeanUtils.copyProperties(this, user, "kiiUserID", "activityToken", "defaultPassword", "userPassword");
		return user;

	}


}
