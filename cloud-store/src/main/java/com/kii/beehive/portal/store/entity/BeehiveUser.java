package com.kii.beehive.portal.store.entity;


import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BeehiveUser extends  PortalEntity {

	private String kiiUserID;

	private String userName;

	private String phone;

	private String mail;

	private String displayName;

	private Set<String> roles=new HashSet<>();


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

	public Set<String> getRoles() {
		//TODO:just for Backward compatibility old data
		if(roles.isEmpty()){
			roles.add("root");
		}
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

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
