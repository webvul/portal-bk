package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.UserGroupManager;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.beehive.portal.store.entity.OutputUserGroup;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/usergroup",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserGroupController {

    @Autowired
    private UserGroupManager userGroupManager;

    /**
     * 创建用户群组
     * POST /usergroup
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Create User Group (创建用户群组)" for more details
     *
     * @param userGroup
     */
    @RequestMapping(path="",method={RequestMethod.POST})
    public ResponseEntity createUserGroup(@RequestBody BeehiveUserGroup userGroup){

        // check whether userGroupName available
        String userGroupName = userGroup.getUserGroupName();
        if(userGroupName == null || userGroupName.trim().length() == 0) {
            throw new PortalException("RequiredFieldsMissing", "userGroupName cannot be null", HttpStatus.BAD_REQUEST);
        }

        // check whether userGroupName existing
        if(userGroupManager.checkUserGroupNameExist(userGroupName)) {
            throw new PortalException("DuplicatedData", "userGroupName already exists", HttpStatus.CONFLICT);
        }

        // creat user group
        String userGroupID = userGroupManager.createUserGroup(userGroup, null);

        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("userGroupID", userGroupID);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * 更新用户群组
     * PATCH /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Update User Group (更新用户群组)" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}",method={RequestMethod.PATCH})
    public ResponseEntity updateUserGroup(@PathVariable("userGroupID") String userGroupID, @RequestBody BeehiveUserGroup userGroup){

        // check whether userGroupID existing
        if(userGroupManager.checkUserGroupIDExist(userGroupID) == false) {
            throw new PortalException("DataNotFound", "userGroupID not found", HttpStatus.NOT_FOUND);
        }

        // if userGroupName is set in request, check whether the user group with the same userGroupName already existing
        String userGroupName = userGroup.getUserGroupName();
        if(!Strings.isBlank(userGroupName)) {
            BeehiveUserGroup tempUserGroup = userGroupManager.getUserGroupByName(userGroupName);
            if(tempUserGroup != null && !userGroupID.equals(tempUserGroup.getUserGroupID())) {
                throw new PortalException("DuplicatedData", "userGroupName already exists", HttpStatus.CONFLICT);
            }
        }

        // update user group
        userGroup.setUserGroupID(userGroupID);
        userGroupManager.updateUserGroup(userGroup, null);

        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("userGroupID", userGroupID);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * 删除用户群组
     * DELETE /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Delete User Group (删除用户群组)" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}",method={RequestMethod.DELETE})
    public ResponseEntity deleteUserGroup(@PathVariable("userGroupID") String userGroupID){

        // check whether userGroupID existing
        if(userGroupManager.checkUserGroupIDExist(userGroupID) == false) {
            throw new PortalException("DataNotFound", "userGroupID not found", HttpStatus.NOT_FOUND);
        }

        // delete user group
        userGroupManager.deleteUserGroup(userGroupID, null);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 查询用户群组
     * POST /usergroup/simplequery
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
     *
     * @param queryMap
     */
    @RequestMapping(path="/simplequery",method={RequestMethod.POST})
    public ResponseEntity queryUserGroup(@RequestBody Map<String,Object> queryMap){

        String includeUserData = (String)queryMap.remove("includeUserData");

        if(queryMap.containsKey("userGroupID")) {
            Object userGroupID = queryMap.remove("userGroupID");
            queryMap.put("_id", userGroupID);
        }

        boolean isIncludeUserData = "1".equals(includeUserData);
        BeehiveUserGroup userGroup = userGroupManager.getUserGroupBySimpleQuery(queryMap, isIncludeUserData);

        OutputUserGroup output = new OutputUserGroup(userGroup, isIncludeUserData);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

}
