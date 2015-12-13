package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.List;
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

import com.kii.beehive.portal.service.BeehiveUserGroupService;
import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroup;
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
    private BeehiveUserGroupService userGroupService;

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
        if(userGroupService.checkUserGroupNameExist(userGroupName)) {
            throw new PortalException("DuplicatedData", "userGroupName already exists", HttpStatus.CONFLICT);
        }

        // creat user group
        long userGroupID = userGroupService.createUserGroup(userGroup);

        Map<String, Object> resultMap = new HashMap<>();
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
    public ResponseEntity updateUserGroup(@PathVariable("userGroupID") long userGroupID, @RequestBody BeehiveUserGroup userGroup){

        // check whether userGroupID existing
        if(userGroupService.checkUserGroupIDExist(userGroupID) == false) {
            throw new PortalException("DataNotFound", "userGroupID not found", HttpStatus.NOT_FOUND);
        }

        // if userGroupName is set in request, check whether the user group with the same userGroupName already existing
        String userGroupName = userGroup.getUserGroupName();
        if(!Strings.isBlank(userGroupName)) {
            BeehiveUserGroup tempUserGroup = userGroupService.getUserGroupByName(userGroupName);
            if(tempUserGroup != null && userGroupID != tempUserGroup.getId()) {
                throw new PortalException("DuplicatedData", "userGroupName already exists", HttpStatus.CONFLICT);
            }
        }

        // update user group
        userGroup.setId(userGroupID);
        userGroupService.updateUserGroup(userGroup);

        Map<String, Object> resultMap = new HashMap<>();
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
    @RequestMapping(path="/{userGroupID}",method={RequestMethod.DELETE},consumes={"*"})
    public ResponseEntity deleteUserGroup(@PathVariable("userGroupID") long userGroupID){

        // check whether userGroupID existing
        if(userGroupService.checkUserGroupIDExist(userGroupID) == false) {
            throw new PortalException("DataNotFound", "userGroupID not found", HttpStatus.NOT_FOUND);
        }

        // delete user group
        userGroupService.deleteUserGroup(userGroupID);

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
// TODO input param "includeUserData" and "userGroupID" are not supported any more, only "userGroupName" is supported, need to update document accordingly


        String userGroupName = (String)queryMap.get("userGroupName");

        List<BeehiveUserGroup> userGroupList = userGroupService.findUserGroupsByNameLike(userGroupName);

        return new ResponseEntity<>(userGroupList, HttpStatus.OK);
    }

    @RequestMapping(path="/{userGroupID}/addusers",method={RequestMethod.PUT})
    public ResponseEntity addUsers(@PathVariable("userGroupID") long userGroupID, @RequestBody List<Long> userIDList){
    // TODO new interface, need to add to document

        if(!userGroupService.checkUserGroupIDExist(userGroupID)) {
            throw new PortalException("DataNotFound", "userGroupID not found", HttpStatus.NOT_FOUND);
        }

        userGroupService.addUsers(userGroupID, userIDList);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path="/{userGroupID}/removeusers",method={RequestMethod.PUT})
    public ResponseEntity removeUsers(@PathVariable("userGroupID") long userGroupID, @RequestBody List<Long> userIDList){
        // TODO new interface, need to add to document

        if(!userGroupService.checkUserGroupIDExist(userGroupID)) {
            throw new PortalException("DataNotFound", "userGroupID not found", HttpStatus.NOT_FOUND);
        }

        userGroupService.removeUsers(userGroupID, userIDList);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
