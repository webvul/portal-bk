package com.kii.beehive.portal.store;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.helper.NotifySenderTool;
import com.kii.beehive.portal.helper.PortalTokenService;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.service.UserSyncMsgDao;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsg;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsgType;

public class TestNotifyTool extends TestInit {


	@Autowired
	private NotifySenderTool tool;

	@Autowired
	private PortalTokenService tokenService;

	@Autowired
	private DeviceSupplierDao supplierDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserSyncMsgDao msgDao;
	@Before
	public void before(){

		tokenService.setToken("d31032a0-8ebf-11e5-9560-00163e02138f", PortalTokenService.PortalTokenType.UserSync);

	}

	@Test
	public void test() throws JsonProcessingException {



		SupplierPushMsgTask task=new SupplierPushMsgTask();

		UserSyncMsg msg=new UserSyncMsg();
		msg.setUserID("user-001");
		msg.setType(UserSyncMsgType.Create);

		task.setMsgContent(msg);

		task.setSourceSupplier(tokenService.getSupplierInfo().getId());


		Map<String,String> urlMap=supplierDao.getUrlMap();
		for(String k:urlMap.keySet()){
			urlMap.put(k,"http://127.0.0.1:7080/supplier-callback/user-sync");
		}

		msgDao.addUserSyncMsg(task);

		tool.doMsgSendTask(task,urlMap);

	}
}
