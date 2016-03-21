package com.kii.beehive.portal.jdbc.entity;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;

/**
 * Created by hdchen on 3/18/16.
 */
public class TagUserRelation extends DBEntity {
    final public static String ID = "id";
    final public static String TAG_ID = "tag_id";
    final public static String USER_ID = "user_id";

    private Long id;

    private Long tagId;

    private String userId;

    @Override
    @JdbcField(column = ID)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @JdbcField(column = TAG_ID)
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @JdbcField(column = USER_ID)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
