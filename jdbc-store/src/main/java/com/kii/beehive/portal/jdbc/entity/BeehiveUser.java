package com.kii.beehive.portal.jdbc.entity;

import java.util.HashMap;
import java.util.Map;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class BeehiveUser extends DBEntity{

	private String kiiUserID;

	private String kiiLoginName;

	private String userName;

	private String phone;

	private String mail;

	private String company;

	private String role;

	private Map<String,Object> custom=new HashMap<>();

	public final static String USER_ID = "user_id";
	public final static String KII_USER_ID = "kii_user_id";
	public final static String KII_LOGIN_NAME = "kii_login_name";
	public final static String USER_NAME = "user_name";
	public final static String PHONE = "phone";
	public final static String MAIL = "mail";
	public final static String COMPANY = "company";
	public final static String ROLE = "role";
//	public final static String CUSTOM = "custom";


	@Override
	@JdbcField(column=USER_ID)
	public long getId(){
		return super.getId();
	}

	@JdbcField(column=KII_USER_ID)
	public String getKiiUserID() {
		return kiiUserID;
	}

	public void setKiiUserID(String kiiUserID) {
		this.kiiUserID = kiiUserID;
	}

	@JdbcField(column=KII_LOGIN_NAME)
	public String getKiiLoginName() {
		return kiiLoginName;
	}

	public void setKiiLoginName(String kiiLoginName) {
		this.kiiLoginName = kiiLoginName;
	}

	@JdbcField(column=USER_NAME)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JdbcField(column=PHONE)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@JdbcField(column=MAIL)
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@JdbcField(column=COMPANY)
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@JdbcField(column=ROLE)
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

//	@JdbcField(column=CUSTOM, type=JdbcFieldType.Json)
//	public Map<String, Object> getCustom() {
//		return custom;
//	}
//
//	public void setCustom(Map<String, Object> customFields) {
//		this.custom = customFields;
//	}
}
