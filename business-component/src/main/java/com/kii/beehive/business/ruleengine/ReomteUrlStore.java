package com.kii.beehive.business.ruleengine;

import com.kii.beehive.portal.common.utils.SafeThreadLocal;

public class ReomteUrlStore {
	
	
	public static final String THIRD_PARTY_URL="/party3rd";
	public static final String RULEENG_CALLBACK_URL="/callback/ruleEngine";
	
	public static final String FIRE_THING_CMD=RULEENG_CALLBACK_URL+"/sendCommand";
	public static final String FIRE_BUSINESS_FUN=RULEENG_CALLBACK_URL+"/executeFunction";
	
	private  static SafeThreadLocal<String>  remoteUrl=SafeThreadLocal.getInstance();
	
	public static void setRemoteUrl(String url){
		
		remoteUrl.set(url);
	}
	
	public static String getThingCmdRemoteUrl(){
		return remoteUrl.get()+THIRD_PARTY_URL+FIRE_THING_CMD;
	}
	
	
	public static String getFIreBusinessFunUrl(){
		return remoteUrl.get()+THIRD_PARTY_URL+FIRE_BUSINESS_FUN;
	}
}
