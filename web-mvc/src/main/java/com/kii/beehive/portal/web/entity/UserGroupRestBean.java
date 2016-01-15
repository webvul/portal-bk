package com.kii.beehive.portal.web.entity;


import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.web.exception.PortalException;

public class UserGroupRestBean extends UserGroup {

    public UserGroupRestBean() {
        super();
    }

    private String userGroupID;

    private String userGroupName;

    public UserGroupRestBean(UserGroup userGroup){
        BeanUtils.copyProperties(userGroup, this, "id", "name");

        Long id = userGroup.getId();
        if(id != null) {
            userGroupID = String.valueOf(id);
        }

        userGroupName = userGroup.getName();

    }

    @JsonIgnore
    public UserGroup getUserGroup() {

        UserGroup userGroup = new UserGroup();

        BeanUtils.copyProperties(this, userGroup, "userGroupID", "userGroupName");

        if(userGroupID != null) {
            userGroup.setId(Long.valueOf(userGroupID));
        }

        userGroup.setName(userGroupName);

        return userGroup;
    }

    public void verifyInput(){

        if(Strings.isBlank(userGroupName)) {
            throw new PortalException("RequiredFieldsMissing", "userGroupName is null", HttpStatus.BAD_REQUEST);
        }

    }

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

}
