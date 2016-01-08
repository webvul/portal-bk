package com.kii.beehive.portal.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.UserManager;
import com.kii.beehive.portal.web.constant.Constants;

public abstract class AbstractController {
	
	@Autowired
	protected AuthManager authManager;
	
	@Autowired
	protected UserManager userManager;
	
	@Autowired
	protected UserGroupDao userGroupDao;
	
	@Autowired
	protected PermissionDao permissionDao;
    
    @Autowired
    protected GroupUserRelationDao groupUserRelationDao;
    
    @Autowired
    protected GroupPermissionRelationDao groupPermissionRelationDao;

	protected String getLoginUserID(HttpServletRequest httpRequest) {
		String auth = httpRequest.getHeader(Constants.ACCESS_TOKEN);
		String token = auth.substring(auth.indexOf(" ") + 1).trim();
		AuthInfo authInfo = authManager.getAuthInfo(token);
		return authInfo.getUserID();
	}
}
