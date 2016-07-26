package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertFalse;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.BeehiveArchiveUserDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveArchiveUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;

public class TestUserDao extends TestTemplate {


	@Autowired
	private BeehiveUserJdbcDao  userDao;

	@Autowired
	private BeehiveArchiveUserDao archiveUserDao;


	private String userID="0e14db00-18d4-11e6-9c6d-00163e007aba";

	@Test
	public void testUserArchive(){


		userDao.updateEnableSign(userID,false);

		BeehiveJdbcUser user=userDao.getUserByUserID(userID);

		assertFalse(user.getEnable());

		BeehiveArchiveUser archiveUser=new BeehiveArchiveUser(user);

		archiveUserDao.insert(archiveUser);

	}


}

