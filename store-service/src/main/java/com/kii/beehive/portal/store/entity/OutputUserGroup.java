package com.kii.beehive.portal.store.entity;


import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutputUserGroup extends BeehiveUserGroup {

    private Set<Object> users = new HashSet<>();

    public OutputUserGroup(BeehiveUserGroup userGroup, boolean includeUserData){
        BeanUtils.copyProperties(userGroup, this, "users", "customFields", "beehiveUserList");

        if(includeUserData) {
            this.users.addAll(userGroup.getBeehiveUserList());
        } else {
            this.users.addAll(userGroup.getUsers());
        }
    }

    @JsonProperty("users")
    public Set<Object> getOutputUsers() {
        return users;
    }

}
