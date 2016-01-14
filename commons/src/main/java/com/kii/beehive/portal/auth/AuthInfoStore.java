package com.kii.beehive.portal.auth;



public class AuthInfoStore {

	private static ThreadLocal<String>  local=ThreadLocal.withInitial(()-> "anonymous");

	private static ThreadLocal<Boolean>  isAdmin=ThreadLocal.withInitial(()->false);

	public static void setAuthInfo(String userID){

		local.set(userID);

	}

	public static void setAdmin(){
		isAdmin.set(true);
	}



	public static boolean isAmin(){
		return isAdmin.get();
	}

	public static String getUserID(){

		return local.get();

	}

	public static void clear(){

		local.remove();
		isAdmin.remove();
	}
}
