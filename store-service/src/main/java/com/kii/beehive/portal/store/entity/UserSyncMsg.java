package com.kii.beehive.portal.store.entity;

import java.util.Map;

public class UserSyncMsg {

	private MsgType type;

	private BeehiveUser user;

	private Map<String,Object> userMap;

	private String userID;



	public static enum MsgType{

		Create,Update,Delete;
	}
}
