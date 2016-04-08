package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.manager.AuthManager;
import com.kii.beehive.business.manager.PortalSyncUserManager;
import com.kii.beehive.portal.jdbc.dao.GroupPermissionRelationDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.PermissionDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;

public abstract class AbstractController {
	
	@Autowired
	protected AuthManager authManager;
	
	@Autowired
	protected PortalSyncUserManager userManager;
	
	@Autowired
	protected UserGroupDao userGroupDao;
	
	@Autowired
	protected PermissionDao permissionDao;
    
    @Autowired
    protected GroupUserRelationDao groupUserRelationDao;
    
    @Autowired
    protected GroupPermissionRelationDao groupPermissionRelationDao;

}
