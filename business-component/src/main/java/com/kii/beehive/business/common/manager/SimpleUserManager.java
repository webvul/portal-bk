package com.kii.beehive.business.common.manager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

@Component
@Transactional
public class SimpleUserManager {

	@Autowired
	private BeehiveUserJdbcDao userDao;
	
	
	public BeehiveJdbcUser getUserByID(long userID) {

		return userDao.getUserByID(userID);
	}


	public BeehiveJdbcUser getUserByUserID(String  userID) {

		return userDao.getUserByUserID(userID);
	}
}
