package com.kii.beehive.portal.exception;

/**
 * Created by hdchen on 3/24/16.
 */
public class UnauthorizedException extends BusinessException {


	public static final String NOT_THING_CREATOR="NOT_THING_CREATOR";
	public static final String NOT_TAG_CREATER="NOT_TAG_CREATER";
	public static final String USER_BEEN_LOCKED="USER_BEEN_LOCKED";
	public static final String USER_ALREADY_ACTIVIED="USER_ALREADY_ACTIVIED";
	public static final String ACTIVITY_TOKEN_INVALID="ACTIVITY_TOKEN_INVALID";
	public static final String LOGIN_TOKEN_INVALID="LOGIN_TOKEN_INVALID";
	public static final String ACCESS_INVALID="ACCESS_INVALID";
	public static final String NOT_IN_CURR_TEAM="NOT_IN_CURR_TEAM";
	public static final String NOT_GROUP_CREATER="NOT_GROUP_CREATER";

	public UnauthorizedException(String msg){
		super.setErrorCode(msg);
	}


	public int getStatusCode(){

		return 401;

	}
}
