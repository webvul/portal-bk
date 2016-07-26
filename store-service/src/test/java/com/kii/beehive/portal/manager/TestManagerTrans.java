package com.kii.beehive.portal.manager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.kii.beehive.business.manager.UserGroupManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.store.StoreServiceTestInit;

public class TestManagerTrans extends StoreServiceTestInit {


	@Autowired
	private UserGroupManager manager;


	@Rollback(false)
	@Test
	public void testUserGroup() {


		UserGroup group = new UserGroup();
		group.setName("test001");
		group.setDescription("desc");

		AuthInfoStore.setUserInfo(100L);

		manager.addUserGroup(group);

	}
}
