package com.kii.beehive.portal.web.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/permission",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class PermissionController extends AbstractController{
	
    
    /**
     * 綁定用户群组權限
     * POST /permission/{permissionID}/userGroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     */
    @RequestMapping(path="/{permissionID}/userGroup/{userGroupID}",method={RequestMethod.POST})
    public ResponseEntity addPermissionToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("permissionID") Long permissionID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	
    	//loginUser can edit, when loginUser is in this group , 
    	List<UserGroup> checkAuth = userGroupDao.findUserGroup(loginUserID, userGroupID, null);
		
		if(checkAuth.size() == 1){
			List<UserGroup> orgiList = userGroupDao.findUserGroup(permissionID, userGroupID);
			if(orgiList.size() == 0){
				GroupPermissionRelation gpr = new GroupPermissionRelation(permissionID, userGroupID);
	    		groupPermissionRelationDao.saveOrUpdate(gpr);
			}
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 解除用户群组權限
     * PUT /permission/{permissionID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     */
    @RequestMapping(path="/{permissionID}/userGroup/{userGroupID}",method={RequestMethod.DELETE})
    public ResponseEntity removePermissionToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("permissionID") Long permissionID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	
    	//loginUser can edit, when loginUser is in this group , 
    	List<UserGroup> checkAuth = userGroupDao.findUserGroup(loginUserID, userGroupID, null);
		
		if(checkAuth.size() == 1){
			groupPermissionRelationDao.delete(permissionID, userGroupID);
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 删除權限
     * DELETE /permission/{permissionID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{permissionID}",method={RequestMethod.DELETE})
    public ResponseEntity deletePermission(@PathVariable("permissionID") Long permissionID){

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 取得群組權限
     * GET /permission/userGroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/userGroup/{userGroupID}",method={RequestMethod.GET})
    public ResponseEntity<List<Permission>> getUserGroupPermission(@PathVariable("userID") Long userGroupID, HttpServletRequest httpRequest){
    	List<Permission> pList = permissionDao.findByUserGroupID(userGroupID);
        return new ResponseEntity<>(pList, HttpStatus.OK);
    }

    /**
     * 列出所有權限
     * GET /permission/list
     *
     * refer to doc "Beehive API - User API" for request/response details
     *
     * @param queryMap
     */
    @RequestMapping(path = "/list", method = {RequestMethod.GET})
	public ResponseEntity<List<Permission>> getUserGroupList() {
		List<Permission> list = permissionDao.findAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
