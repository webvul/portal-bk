package com.kii.beehive.portal.store.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TokenStore {

	@Id
	private String token;

	private Date validPeriod;

	private TokenType type;

	private String userID;


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getValidPeriod() {
		return validPeriod;
	}

	public void setValidPeriod(Date validPeriod) {
		this.validPeriod = validPeriod;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
}
