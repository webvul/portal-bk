package com.kii.beehive.portal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;

public abstract class AbstractController {



	@Autowired
	protected UserGroupDao userGroupDao;

	@Autowired
	protected GroupUserRelationDao groupUserRelationDao;


}
