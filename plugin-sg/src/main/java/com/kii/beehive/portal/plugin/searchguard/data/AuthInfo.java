package com.kii.beehive.portal.plugin.searchguard.data;

/**
 * Created by hdchen on 8/25/16.
 */
public class AuthInfo {
	private Long id;

	private String userName;

	private String roleName;

	private String userID;

	private Long faceSubjectId;

	private Boolean enable;

	private String accessToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Long getFaceSubjectId() {
		return faceSubjectId;
	}

	public void setFaceSubjectId(Long faceSubjectId) {
		this.faceSubjectId = faceSubjectId;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
