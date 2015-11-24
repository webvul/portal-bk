package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.query.QueryParam;
import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.helper.SimpleQueryTool;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName = "portal")
@Component
public class BeehiveUserGroupDao extends AbstractDataAccess<BeehiveUserGroup> {

	@Autowired
	private SimpleQueryTool queryTool;

    public String createUserGroup(BeehiveUserGroup userGroup) {

        return super.addKiiEntity(userGroup);

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

		return super.getObjectByID(userGroupID);
    }

    public List<BeehiveUserGroup> getUserGroupByIDs(List<String> userGroupIDs) {

        return super.getEntitys(userGroupIDs.toArray(new String[0]));
    }

    public List<BeehiveUserGroup>  getUserGroupsBySimpleQuery(Map<String,Object> params){
        QueryParam query=queryTool.getEntitysByFields(params);

        return super.fullQuery(query);
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
