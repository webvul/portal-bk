package com.kii.beehive.portal.web.controller;


import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.GroupPermissionRelation;
import com.kii.beehive.portal.jdbc.entity.Permission;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Beehive API - User API
 * <p>
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(value = "/permission", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class PermissionController extends AbstractController {


	/**
	 * 綁定用户群组權限
	 * POST /permission/{permissionID}/userGroup/{userGroupID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 */
	@RequestMapping(value = "/{permissionID}/userGroup/{userGroupID}", method = {RequestMethod.POST})
	public ResponseEntity addPermissionToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("permissionID") Long permissionID, HttpServletRequest httpRequest) {

		UserGroup ug = userGroupDao.findByID(userGroupID);
		if (ug == null) {
			throw new PortalException("UserGroup Not Found", "UserGroup with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		}
		//loginUser can edit, when loginUser is in this group ,
		List<UserGroup> checkAuth = userGroupDao.findUserGroup(AuthInfoStore.getUserID(), userGroupID, null);

		if (checkAuth.size() == 1) {
			List<UserGroup> orgiList = userGroupDao.findUserGroup(permissionID, userGroupID);
			if (orgiList.size() == 0) {
				GroupPermissionRelation gpr = new GroupPermissionRelation(permissionID, userGroupID);
				groupPermissionRelationDao.insert(gpr);
			}
		} else {
			throw new BeehiveUnAuthorizedException("loginUser isn't in the group");
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 解除用户群组權限
	 * PUT /permission/{permissionID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 */
	@RequestMapping(value = "/{permissionID}/userGroup/{userGroupID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public ResponseEntity removePermissionToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("permissionID") Long permissionID, HttpServletRequest httpRequest) {

		//loginUser can edit, when loginUser is in this group ,
		List<UserGroup> checkAuth = userGroupDao.findUserGroup(AuthInfoStore.getUserID(), userGroupID, null);

		if (checkAuth.size() == 1) {
			groupPermissionRelationDao.delete(permissionID, userGroupID);
		} else {
			throw new BeehiveUnAuthorizedException("loginUser isn't in the group");
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}


	/**
	 * 取得群組權限
	 * GET /permission/userGroup/{userGroupID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userGroupID
	 */
	@RequestMapping(value = "/userGroup/{userGroupID}", method = {RequestMethod.GET}, consumes = {"*"})
	public ResponseEntity<List<Permission>> getUserGroupPermission(@PathVariable("userGroupID") Long userGroupID, HttpServletRequest httpRequest) {
		UserGroup ug = userGroupDao.findByID(userGroupID);
		if (ug == null) {
			throw new PortalException("UserGroup Not Found", "UserGroup with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		}
		List<Permission> pList = permissionDao.findByUserGroupID(userGroupID);
		return new ResponseEntity<>(pList, HttpStatus.OK);
	}

	/**
	 * 列出所有權限
	 * GET /permission/list
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.GET}, consumes = {"*"})
	public ResponseEntity<List<Permission>> getUserGroupList() {
		List<Permission> list = permissionDao.findAll();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
