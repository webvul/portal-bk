package com.kii.beehive.portal.web.entity;


import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

public class UserGroupRestBean extends UserGroup {

    public UserGroupRestBean() {
        super();
    }

    private Long userGroupID;

    private String userGroupName;
    
    private List<BeehiveUser> users;

    public UserGroupRestBean(UserGroup userGroup){
        BeanUtils.copyProperties(userGroup, this, "id", "name");

        Long id = userGroup.getId();
        if(id != null) {
            userGroupID = id;
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
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, HttpStatus.BAD_REQUEST);
        }

    }

    public Long getUserGroupID() {
        return userGroupID;
    }

    public void setUserGroupID(Long userGroupID) {
        this.userGroupID = userGroupID;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

	public List<BeehiveUser> getUsers() {
		return users;
	}

	public void setUsers(List<BeehiveUser> users) {
		this.users = users;
	}
    
}
