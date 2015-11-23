package com.kii.beehive.portal.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.service.UserSyncMsgDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.DeviceSupplier;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsg;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsgType;

@Component
public class SyncMsgService {

	@Autowired
	private UserSyncMsgDao msgDao;

	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private DeviceSupplierDao  supplierDao;

	@Autowired
	private PortalTokenService tokenService;

	@Autowired
	private NotifySenderTool notifyTool;

	@Async
	public void addUpdateMsg(String userID,BeehiveUser user){
		addSyncMsg(userID, UserSyncMsgType.Update,user);
	}


	@Async
	public void addDeleteMsg(String userID){
		addSyncMsg(userID, UserSyncMsgType.Delete,null);

	}

	@Async
	public void addInsertMsg(String userID,BeehiveUser user){
		addSyncMsg(user.getId(), UserSyncMsgType.Create,user);

	}

	private void addSyncMsg(String userID,UserSyncMsgType type,BeehiveUser user){

		UserSyncMsg  msg=new UserSyncMsg();

		msg.setUserID(userID);
		msg.setType(type);
		msg.setRetryCount(0);

//		String msgJson=null;
//		try {
//			 msgJson=mapper.writeValueAsString(msg);
//		} catch (JsonProcessingException e) {
//			throw new IllegalArgumentException(e);
//		}

		SupplierPushMsgTask entity=new SupplierPushMsgTask();

		entity.setMsgContent(msg);
		entity.setSourceSupplier(tokenService.getSupplierInfo().getId());

		msgDao.addUserSyncMsg(entity);

		notifyTool.doMsgSendTask(entity, supplierDao.getUrlMap());

	}



	@Scheduled(fixedRate=1000*60*60)
	public void executeSyncTask(){



		List<SupplierPushMsgTask>  msgList= msgDao.getUnfinishMsgList();


		msgList.parallelStream().forEach((msg)->notifyTool.doMsgSendTask(msg, supplierDao.getUrlMap()));

	}
}
