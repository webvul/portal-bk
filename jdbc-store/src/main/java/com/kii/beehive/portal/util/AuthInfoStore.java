package com.kii.beehive.portal.util;


import com.kii.beehive.portal.jdbc.entity.AuthInfo;

public class AuthInfoStore {

	private static ThreadLocal<AuthInfo>  local=new ThreadLocal<>();


	public static void setAuthInfo(AuthInfo info){

		local.set(info);

	}

	public static String getUserID(){

		AuthInfo info=local.get();
		if(info==null){
			return "anonymous";
		}else{
			return info.getUserID();
		}
	}

	public static void clear(){

		local.remove();
	}
}
