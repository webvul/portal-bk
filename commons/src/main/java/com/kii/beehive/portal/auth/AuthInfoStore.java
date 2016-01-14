package com.kii.beehive.portal.auth;



public class AuthInfoStore {

	private static ThreadLocal<String>  local=ThreadLocal.withInitial(()-> "anonymous");


	public static void setAuthInfo(String userID){

		local.set(userID);

	}

	public static String getUserID(){

		return local.get();

	}

	public static void clear(){

		local.remove();
	}
}
