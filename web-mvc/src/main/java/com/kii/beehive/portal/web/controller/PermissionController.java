package com.kii.beehive.portal.web.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.web.constant.Constants;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/permission",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class PermissionController extends AbstractController{
	
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
    public ResponseEntity createPermission(@RequestBody UserGroup userGroup, HttpServletRequest httpRequest){

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
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
    
    @RequestMapping(path="/{permissionID}/userGroup/{userGroupID}",method={RequestMethod.PUT})
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
     * 删除用户群组
     * DELETE /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Delete User Group (删除用户群组)" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{permissionID}",method={RequestMethod.DELETE})
    public ResponseEntity deleteUserGroup(@PathVariable("permissionID") Long permissionID){

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 取得群組用戶
     * DELETE /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Detail User Group" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{permissionID}",method={RequestMethod.GET})
    public ResponseEntity getPermissionIDDetail(@PathVariable("permissionID") Long permissionID, HttpServletRequest httpRequest){
    	

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 查询用户群组
     * POST /usergroup/search
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
     *
     * @param queryMap
     */
    @RequestMapping(path = "/list", method = {RequestMethod.GET})
	public ResponseEntity<List<Permission>> getUserGroupList() {
		List<Permission> list = permissionDao.findAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
