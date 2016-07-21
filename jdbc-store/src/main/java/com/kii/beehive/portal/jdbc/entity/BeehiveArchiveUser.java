package com.kii.beehive.portal.jdbc.entity;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class BeehiveArchiveUser extends DBEntity {

	private long beehiveUserID;

	private String userName;

	private String phone;

	private String mail;

	private String displayName;

	private String  roleName;

	private String userID;

	private Integer  faceSubjectId;

	private Date createDate;




	public BeehiveArchiveUser(){

	}

	public BeehiveArchiveUser(BeehiveJdbcUser user){

		BeanUtils.copyProperties(user,this,"id");

		this.createDate=new Date();

		this.setBeehiveUserID(user.getId());
	}

	@JdbcField(column="create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@JdbcField(column="beehive_user_id")
	public long getBeehiveUserID() {
		return beehiveUserID;
	}

	public void setBeehiveUserID(long beehiveUserID) {
		this.beehiveUserID = beehiveUserID;
	}

	@JdbcField(column="face_subject_id")
	public Integer getFaceSubjectId() {
		return faceSubjectId;
	}

	public void setFaceSubjectId(Integer faceSubjectId) {
		this.faceSubjectId = faceSubjectId;
	}


	@JdbcField(column = "archive_user_id")
	@Override
	public Long getId(){

		return super.getId();
	}

	@JdbcField(column="user_id")
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@JdbcField(column="user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JdbcField(column="mobile")
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

}
