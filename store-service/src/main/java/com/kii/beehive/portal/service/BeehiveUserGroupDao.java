package com.kii.beehive.portal.service;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;
import org.springframework.stereotype.Component;

import java.util.*;


@BindAppByName(appName = "portal")
@Component
public class BeehiveUserGroupDao extends AbstractDataAccess<BeehiveUserGroup> {


    public void createUserGroup(BeehiveUserGroup userGroup) {

        super.addEntity(userGroup, userGroup.getUserGroupID());

    }

    public void updateUserGroup(String userGroupID, BeehiveUserGroup userGroup) {

        super.updateEntity(userGroup, userGroupID);

    }

    public void updateUsers(String userGroupID, Set<String> users){

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("users", users);

        super.updateEntity(paramMap, userGroupID);

    }

    public void deleteUserGroup(String userGroupID) {
        super.removeEntity(userGroupID);
    }

    public BeehiveUserGroup getUserGroupByID(String userGroupID) {
        return super.getEntity("userGroupID", userGroupID);
    }

    public List<BeehiveUserGroup> getUserGroupByIDs(List<String> userGroupIDs) {
        return super.getEntitys("userGroupID", Arrays.asList(userGroupIDs));
    }

    @Override
    protected Class<BeehiveUserGroup> getTypeCls() {
        return BeehiveUserGroup.class;
    }

    @Override
    protected BucketInfo getBucketInfo() {
        return new BucketInfo("beehiveUserGroup");
    }


}
