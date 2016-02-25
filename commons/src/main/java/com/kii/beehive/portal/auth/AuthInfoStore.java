package com.kii.beehive.portal.auth;



public class AuthInfoStore {

	private static ThreadLocal<String>  local=ThreadLocal.withInitial(()-> "anonymous");
	
	private static ThreadLocal<Long>  team=ThreadLocal.withInitial(()-> null);

	private static ThreadLocal<Boolean>  isAdmin=ThreadLocal.withInitial(()->false);

	public static void setAuthInfo(String userID){
		local.set(userID);
	}
	
	public static void setTeamID(Long teamID){
		team.set(teamID);
	}
	
	public static Long getTeamID(){
		return team.get();
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
