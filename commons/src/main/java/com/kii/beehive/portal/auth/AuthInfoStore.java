package com.kii.beehive.portal.auth;


public class AuthInfoStore {

	private static ThreadLocal<Long> user = ThreadLocal.withInitial(() -> 0L);

	private static ThreadLocal<Long> team = ThreadLocal.withInitial(() -> null);


	public static void setAuthInfo(Long userID) {
		user.set(userID);
	}

	public static void setTeamID(Long teamID) {
		team.set(teamID);
	}

	public static Long getTeamID() {
		return team.get();
	}

	public static boolean isTeamIDExist() {
		return getTeamID() != null;
	}


	public static Long getUserID() {

		return user.get();

	}

	public static void setUserInfo(Long beehiveUserID) {
		user.set(beehiveUserID);
	}


	public static void clear() {
		user.remove();
		team.remove();
	}
}
