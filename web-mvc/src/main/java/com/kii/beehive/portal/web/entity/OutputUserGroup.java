package com.kii.beehive.portal.web.entity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;

public class OutputUserGroup extends BeehiveUserGroup {

    private Set<Object> users = new HashSet<>();

    public OutputUserGroup(BeehiveUserGroup userGroup, boolean includeUserData){
        BeanUtils.copyProperties(userGroup, this, "users", "customFields", "beehiveUserList");

        if(includeUserData) {
            List<BeehiveUser> beehiveUserList = userGroup.getBeehiveUserList();
            if(beehiveUserList == null) {
                this.users.addAll(new ArrayList<BeehiveUser>());
            } else {
                this.users.addAll(beehiveUserList);
            }

        } else {
            Set<String> users = userGroup.getUsers();
            if(users == null) {
                this.users.addAll(new HashSet<String>());
            } else {
                this.users.addAll(users);
            }
        }
    }

    @JsonProperty("users")
    public Set<Object> getOutputUsers() {
        return users;
    }

}
