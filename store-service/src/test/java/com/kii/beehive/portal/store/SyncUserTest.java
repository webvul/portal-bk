package com.kii.beehive.portal.store;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.notify.UserSyncNotifier;

public class SyncUserTest extends TestInit{

	@Autowired
	private UserSyncNotifier notify;

	@Test
	public void testSync(){

		notify.init();

	}

}
