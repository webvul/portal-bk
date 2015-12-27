package com.kii.beehive.portal.jdbc;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.AuthInfoDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.AuthInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestAuthInfoDao extends TestTemplate{

	@Autowired
	private AuthInfoDao authInfoDao;

	private AuthInfo authInfo;

	@Before
	public void init() {
		authInfo = new AuthInfo();
		authInfo.setUserID("some user id");
		authInfo.setToken("some token");
		authInfo.setExpireTime(new Date(System.currentTimeMillis() + 60000));
	}

	@Test
	public void testInsert() {

		long id = authInfoDao.saveOrUpdate(authInfo);
		authInfo.setId(id);

		AuthInfo authInfo = authInfoDao.findByID(id);

		assertEquals(this.authInfo.getId(), authInfo.getId());
		assertEquals(this.authInfo.getUserID(), authInfo.getUserID());
		assertEquals(this.authInfo.getToken(), authInfo.getToken());

	}

	@Test
	public void testUpdate() {

		// create
		this.testInsert();

		// update
		authInfo.setUserID("some new user id");
		authInfo.setToken("some new token");

		authInfoDao.saveOrUpdate(authInfo);

		// query
		AuthInfo authInfo = authInfoDao.findByID(this.authInfo.getId());

		assertEquals(this.authInfo.getId(), authInfo.getId());
		assertEquals(this.authInfo.getUserID(), authInfo.getUserID());
		assertEquals(this.authInfo.getToken(), authInfo.getToken());

	}

	@Test
	public void testDelete() {

		// create
		this.testInsert();

		// delete
		authInfo.setUserID("some new user id");
		authInfo.setToken("some new token");

		authInfoDao.deleteByID(authInfo.getId());

		// query
		AuthInfo authInfo = authInfoDao.findByID(this.authInfo.getId());

		assertNull(authInfo);

	}

	
}
