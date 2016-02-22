package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.TeamGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.UserManager;

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
    
    @Autowired
    protected TeamGroupRelationDao teamGroupRelationDao;

	protected String getLoginUserID() {
		return AuthInfoStore.getUserID();
	}
	
	protected Long getLoginTeamID() {
		return AuthInfoStore.getTeamID();
	}
	
	protected boolean isTeamIDExist() {
		return getLoginTeamID() != null;
	}
}
