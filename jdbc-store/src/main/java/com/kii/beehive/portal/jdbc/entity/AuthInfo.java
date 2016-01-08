package com.kii.beehive.portal.jdbc.entity;

import java.util.Date;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

public class AuthInfo extends DBEntity {

	private String userID = null;
	private String token = null;
	private Date expireTime = null;

	public final static String ID = "id";
	public final static String USER_ID = "user_id";
	public final static String TOKEN = "token";
	public final static String EXPIRE_TIME = "expire_time";

	@Override
	@JdbcField(column=ID)
	public Long getId() {
		return super.getId();
	}

	@JdbcField(column=USER_ID)
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@JdbcField(column=TOKEN)
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@JdbcField(column=EXPIRE_TIME)
	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

}
