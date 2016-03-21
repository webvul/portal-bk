package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

/**
 * Created by hdchen on 3/18/16.
 */
public class ThingUserGroupRelation extends DBEntity {
    final public static String ID = "id";
    final public static String THING_ID = "thing_id";
    final public static String USER_GROUP_ID = "user_group_id";
    private Long id;
    private Long thingId;
    private Long userGroupId;

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

    @JdbcField(column = USER_GROUP_ID)
    public Long getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Long userGroupId) {
        this.userGroupId = userGroupId;
    }
}
