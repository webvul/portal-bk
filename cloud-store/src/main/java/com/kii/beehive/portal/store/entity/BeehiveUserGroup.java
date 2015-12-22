package com.kii.beehive.portal.store.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;


public class BeehiveUserGroup extends PortalEntity {

    public static final String PREFIX = "custom-";


    private String userGroupName;

    private String description;

    private Set<String> users;

    private Map<String, Object> custom = new HashMap<>();
    
    private List<BeehiveUser> beehiveUserList;

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

    @JsonIgnore
    public Set<String> getUserIDs() {
        Set<String> IDs = new HashSet<>();
        if(users != null) {
            users.forEach(ID->IDs.add((String)ID));
        }
        return IDs;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    @JsonUnwrapped(prefix = PREFIX)
    public Map<String, Object> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, Object> customFields) {
        this.custom = customFields;
    }

    @JsonAnySetter
    public void setCustom(String key, Object value) {
        this.custom.put(key, value);
    }

    @JsonIgnore
	public List<BeehiveUser> getBeehiveUserList() {
		return beehiveUserList;
	}

	public void setBeehiveUserList(List<BeehiveUser> beehiveUserList) {
		this.beehiveUserList = beehiveUserList;
	}
}
