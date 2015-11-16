package com.kii.beehive.portal.store.entity;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kii.extension.sdk.entity.KiiEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BeehiveUserGroup extends KiiEntity {

    public static final String PREFIX = "custom-";

    private String userGroupID;

    private String userGroupName;

    private String description;

    private Set<String> users;

    private Map<String, Object> customFields = new HashMap<>();

    public String getUserGroupID() {
        return userGroupID;
    }

    public void setUserGroupID(String userGroupID) {
        this.userGroupID = userGroupID;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    @JsonUnwrapped(prefix = PREFIX)
    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

    @JsonAnySetter
    public void setCustomField(String key, Object value) {
        this.customFields.put(key, value);
    }
}
