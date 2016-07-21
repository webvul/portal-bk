package com.kii.beehive.portal.auth;


public class AuthInfoStore {

	private static ThreadLocal<String>  user=ThreadLocal.withInitial(()-> "anonymous");

	private static ThreadLocal<Long>  team=ThreadLocal.withInitial(()-> null);

	private static ThreadLocal<Long>  userIDInLong=new ThreadLocal<>();

	public static void setAuthInfo(String userID){
		user.set(userID);
	}
	
	public static void setTeamID(Long teamID){
		team.set(teamID);
	}
	
	public static Long getTeamID(){
		return team.get();
	}

	public static boolean isTeamIDExist(){
		return getTeamID() != null;
	}


	public static String getUserID(){

		return user.get();

	}

	public static void setUserInfo(String userID,Long userIDLong){
		user.set(userID);
		userIDInLong.set(userIDLong);
	}

	public static Long getUserIDInLong(){

		return userIDInLong.get();

	}

	public static void clear(){

		user.remove();
		team.remove();
		userIDInLong.remove();
	}
}
