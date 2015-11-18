package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.sdk.entity.KiiEntity;


public class BeehiveUserGroup extends KiiEntity {

    public static final String PREFIX = "custom-";


    private String userGroupName;

    private String description;

    private Set<String> users;

    private Map<String, Object> customFields = new HashMap<>();

    public String getUserGroupID() {
        return getId();
    }

    public void setUserGroupID(String userGroupID) {
        this.setId(userGroupID);
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