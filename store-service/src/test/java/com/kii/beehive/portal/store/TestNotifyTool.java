package com.kii.beehive.portal.store;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.common.helper.NotifySenderTool;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.service.UserSyncMsgDao;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsg;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsgType;

public class TestNotifyTool extends StoreServiceTestInit {


	@Autowired
	private NotifySenderTool tool;



	@Autowired
	private DeviceSupplierDao supplierDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private UserSyncMsgDao msgDao;


	@Test
	public void test() throws JsonProcessingException {



		SupplierPushMsgTask task=new SupplierPushMsgTask();

		UserSyncMsg msg=new UserSyncMsg();
		msg.setUserID("user-001");
		msg.setType(UserSyncMsgType.Create);

		task.setMsgContent(msg);

		task.setSourceSupplier("d31032a0-8ebf-11e5-9560-00163e02138f");


		Map<String,String> urlMap=supplierDao.getUrlMap();
		for(String k:urlMap.keySet()){
			urlMap.put(k,"http://127.0.0.1:7080/user-sync");
		}

		msgDao.addUserSyncMsg(task);

		tool.doMsgSendTask(task,urlMap);

	}
}
