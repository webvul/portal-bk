package com.kii.beehive.portal.auth;


import com.kii.beehive.portal.common.utils.SafeThreadLocal;

public class AuthInfoStore {

	private static SafeThreadLocal<Long> user = SafeThreadLocal.withInitial(() -> 0L);

	private static SafeThreadLocal<Long> team = SafeThreadLocal.withInitial(() -> null);


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

	public static String getUserIDStr() {
		return String.valueOf(user.get());
	}

	public static void setUserInfo(Long beehiveUserID) {
		user.set(beehiveUserID);
	}


	public static void clear() {
		user.remove();
		team.remove();
	}
}
