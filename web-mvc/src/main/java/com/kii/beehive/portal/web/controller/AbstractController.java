package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.common.manager.PortalSyncUserManager;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.manager.AuthManager;

public abstract class AbstractController {

	@Autowired
	protected AuthManager authManager;

	@Autowired
	protected PortalSyncUserManager userManager;

	@Autowired
	protected UserGroupDao userGroupDao;

	@Autowired
	protected GroupUserRelationDao groupUserRelationDao;


}
