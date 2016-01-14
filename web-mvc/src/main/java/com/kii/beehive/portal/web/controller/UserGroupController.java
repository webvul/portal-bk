package com.kii.beehive.portal.web.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/usergroup",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserGroupController extends AbstractController{

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
    public ResponseEntity createUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest httpRequest){

        if(Strings.isBlank(userGroup.getName())) {
            throw new PortalException("RequiredFieldsMissing", "userGroupName is null", HttpStatus.BAD_REQUEST);
        }
        
        String loginUserID = getLoginUserID(httpRequest);
        Long userGroupID = null;
        if(userGroup.getId() == null){//create
        	userGroupID = userManager.createUserGroup(userGroup, loginUserID);
        }else{//update
        	userGroupID = userManager.updateUserGroup(userGroup, loginUserID);
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("userGroupID", userGroupID);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    /**
     * 用户加入群组
     * POST /usergroup/{userGroupID}/user/{userID}
     *
     * @param userGroup
     */
    @RequestMapping(path="/{userGroupID}/user/{userID}",method={RequestMethod.POST})
    public ResponseEntity addUserToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userID") String userID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	
		if(checkUserGroup(loginUserID, userGroupID)){
			List<UserGroup> orgiList = userGroupDao.findUserGroup(userID, userGroupID, null);
			if(orgiList.size() == 0){
				GroupUserRelation gur = new GroupUserRelation(userID, userGroupID);
	    		groupUserRelationDao.saveOrUpdate(gur);
			}
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 用户從群组刪除
     * PUT /usergroup/{userGroupID}/user/{userID}
     *
     * @param userGroup
     */
    @RequestMapping(path="/{userGroupID}/user/{userID}",method={RequestMethod.DELETE})
    public ResponseEntity removeUserToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userID") String userID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	
		if(checkUserGroup(loginUserID, userGroupID)){
    		groupUserRelationDao.delete(userID, userGroupID);
		}
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity deleteUserGroup(@PathVariable("userGroupID") Long userGroupID){

    	UserGroup orig =  userGroupDao.findByID(userGroupID);
		
		if(orig == null){
			throw new PortalException("Thing Not Found", "Thing with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		}
		
		userManager.deleteUserGroup(userGroupID);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 取得群組內的用戶
     * GET /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Detail User Group" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}",method={RequestMethod.GET})
    public ResponseEntity getUserGroupDetail(@PathVariable("userGroupID") Long userGroupID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	List<GroupUserRelation> list = null;
		if(checkUserGroup(loginUserID, userGroupID)){
			list = groupUserRelationDao.findByUserGroupID(userGroupID);
		}

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    /**
     * 列出用户群组
     * POST /usergroup/list
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
     *
     * @param queryMap
     */
    @RequestMapping(path = "/list", method = {RequestMethod.GET})
	public ResponseEntity<List<UserGroup>> getUserGroupList(HttpServletRequest httpRequest) {
    	String loginUserID = getLoginUserID(httpRequest);
		List<UserGroup> list = userGroupDao.findUserGroup(loginUserID, null , null);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
    
    @RequestMapping(path = "/all", method = {RequestMethod.GET})
	public ResponseEntity<List<UserGroup>> getUserGroupAll(HttpServletRequest httpRequest) {
		List<UserGroup> list = userGroupDao.findAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
    
    private boolean checkUserGroup(String loginUserID, Long userGroupID){
    	//loginUser can edit, when loginUser is in this group ,
    	List<UserGroup> checkAuth = userGroupDao.findUserGroup(loginUserID, userGroupID, null);
		if(checkAuth.size() == 1){
			return true;
		}else{
			return false;
		}
    }

}
