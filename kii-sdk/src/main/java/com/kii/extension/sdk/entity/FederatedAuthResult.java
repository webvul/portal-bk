package com.kii.extension.sdk.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FederatedAuthResult {

	private Logger log= LoggerFactory.getLogger(FederatedAuthResult.class);

	private String providerUserID;

	private String  appAuthToken;

	private long expiresIn;

	private boolean isNewUser;

	private String userID;

	private long tokenExpiresIn;

	private String masterAuthToken;

	private String idToken;

	private String flowID;

	private boolean success;

	private String code;

	public FederatedAuthResult(){

	}

	public FederatedAuthResult(String url){

		/*
		http://c1744915.development-beehivecn3.internal.kiiapps.com/api/apps/c1744915/integration/webauth/result?
kii_access_token=wiQSvcOsDL-3qwKcSZmmmMbq2i8i07DB1Lnr2DzZ_qg&
provider_user_id=aba700e36100-9eca-5e11-9e98-0edbde87&
kii_expires_in=2147483646&
kii_new_user=false&
kii_user_id=f83120e36100-0659-5e11-9e98-0c322bd7&
oauth_token_expires_in=2147483586&
oauth_token=IPnjJMpHQYBRT_OP6CjjdFTY5VbrK9lbGXY9RRPxszk&
id_token=eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXBpLWRldmVsb3BtZW50LWJlZWhpdmVjbjMuaW50ZXJuYWwua2lpLmNvbVwvYXBpXC9hcHBzXC9kYTBiNmEyNSIsInN1YiI6ImFiYTcwMGUzNjEwMC05ZWNhLTVlMTEtOWU5OC0wZWRiZGU4NyIsImF1ZCI6ImE5ZjAyM2J2M2NqYmhpcTJuZmhhZG9wdjhwaHVpbzJ1NzBmdCIsImV4cCI6MzU5NjA5ODg1OSwiaWF0IjoxNDQ4NjE1MjcyLCJhcHBJRCI6ImRhMGI2YTI1In0.Wb-tQoowU8jMEEzVM5plgQ3IDwAWOLsEYj_BCuA7p3BcY-mAN96LBP2mRe8yza7lknc3B03tpOFr-pnWJchtz-njpUSohEkvqgOD8sstVsAGrRp2XgbnRuXZg3PxFYy8sC789oDM4EZ_Nxn6itXsWG8Zs-BqQDshUSmdtrHZcto&
kii_flow_id=2fdb0ngdw5vxrgtoil0f3uvn1&
kii_succeeded=true&
code=7cc0samu6ijbeoomvpq328cugsk4so0lcqh9bl6nll9apb3krdk1q93a72rm21kb&state= */


		String[] arrays=url.split("\\&");

		for(String seg:arrays){
			int idx=seg.indexOf("=");
			String key=seg.substring(0,idx);
			String value=seg.substring(idx+1);

			switch(key) {
				case "kii_access_token":
					this.appAuthToken = value;
					break;
				case "provider_user_id":
					this.providerUserID=value;
					break;
				case "kii_expires_in":
					this.expiresIn=Long.parseLong(value);
					break;
				case "kii_new_user":
					this.isNewUser=Boolean.getBoolean(value);
					break;
				case "kii_user_id":
					this.userID=value;
					break;
				case "oauth_token_expires_in":
					this.tokenExpiresIn=Long.parseLong(value);
					break;
				case "oauth_token":
					this.masterAuthToken=value;
					break;
				case "id_token":
					this.idToken=value;
					break;
				case "kii_flow_id":
					this.flowID=value;
					break;
				case "kii_succeeded":
					this.success=Boolean.parseBoolean(value);
					break;
				case "code":
					this.code=value;
					break;
				default:
					log.error("invalid field name:{}/{}",key,value);
				}


			}
		}

	public String getProviderUserID() {
		return providerUserID;
	}

	public void setProviderUserID(String providerUserID) {
		this.providerUserID = providerUserID;
	}

	public String getAppAuthToken() {
		return appAuthToken;
	}

	public void setAppAuthToken(String appAuthToken) {
		this.appAuthToken = appAuthToken;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean newUser) {
		isNewUser = newUser;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public long getTokenExpiresIn() {
		return tokenExpiresIn;
	}

	public void setTokenExpiresIn(long tokenExpiresIn) {
		this.tokenExpiresIn = tokenExpiresIn;
	}

	public String getMasterAuthToken() {
		return masterAuthToken;
	}

	public void setMasterAuthToken(String masterAuthToken) {
		this.masterAuthToken = masterAuthToken;
	}

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getFlowID() {
		return flowID;
	}

	public void setFlowID(String flowID) {
		this.flowID = flowID;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
