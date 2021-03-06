package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PortalSyncUser extends PortalEntity{


		public static final String PREFIX = "custom-";

		private String aliUserID;

		private String kiiUserID;

		private String kiiLoginName;

		private String userName;

		private String phone;

		private String mail;

		private String role;

		private String company;

		private Set<String> groups;

		private Map<String,Object> customFields=new HashMap<>();

		private CustomProperty properties=new CustomProperty();

		public PortalSyncUser(){

		}

		@JsonProperty("userID")
		public String getAliUserID() {
			return aliUserID;
		}

		public void setAliUserID(String aliUserID) {
			this.aliUserID = aliUserID;
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

		public Set<String> getGroups() {
			return groups;
		}

		public void setGroups(Set<String> groups) {
			this.groups = groups;
		}

		public String getKiiLoginName() {
			return kiiLoginName;
		}

		public void setKiiLoginName(String kiiLoginName) {
			this.kiiLoginName = kiiLoginName;
		}

		@JsonUnwrapped
		public CustomProperty getCustomFields() {
			return properties;
		}

		public void setCustomFields(CustomProperty properties) {
			this.properties = properties;
		}

		@JsonIgnore
		public void setCustomField(String key,Object val){
			this.properties.setCustomField(key,val);
		};


		@JsonIgnore
		public Object getCustomField(String key){
			return this.properties.getValueByKey(key);
		}


}
