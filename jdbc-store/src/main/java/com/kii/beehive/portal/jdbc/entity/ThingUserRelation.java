package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

/**
 * Created by hdchen on 3/18/16.
 */
public class ThingUserRelation extends DBEntity {
    final public static String ID = "id";
    final public static String THING_ID = "thing_id";
    final public static String USER_ID = "beehive_user_id";
	final public static String OLD_USER_ID = "user_id";

    private Long id;
    private Long thingId;
    private String userId;

	private Long  beehiveUserID;

	@JdbcField(column= USER_ID)
	public Long getBeehiveUserID() {
		return beehiveUserID;
	}

	public void setBeehiveUserID(Long beehiveUserID) {
		this.beehiveUserID = beehiveUserID;
	}

	@Override
    @JdbcField(column = ID)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @JdbcField(column = THING_ID)
    public Long getThingId() {
        return thingId;
    }

    public void setThingId(Long thingId) {
        this.thingId = thingId;
    }

//    @JdbcField(column = OLD_USER_ID)
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
}
