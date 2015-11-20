package com.kii.beehive.portal.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.manager.NotifySenderTool;
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

	public void addUpdateMsg(String userID,BeehiveUser user){
		addSyncMsg(userID, UserSyncMsgType.Update,user);
	}


	public void addDeleteMsg(String userID){
		addSyncMsg(userID, UserSyncMsgType.Delete,null);

	}

	public void addInsertMsg(String userID,BeehiveUser user){
		addSyncMsg(user.getId(), UserSyncMsgType.Create,user);

	}

	private void addSyncMsg(String userID,UserSyncMsgType type,BeehiveUser user){

		UserSyncMsg  msg=new UserSyncMsg();
		if(user!=null) {
			msg.setUser(user);
		}

		msg.setUserID(userID);
		msg.setType(type);

		String msgJson=null;
		try {
			 msgJson=mapper.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}

		SupplierPushMsgTask entity=new SupplierPushMsgTask();

		entity.setMsgContent(msgJson);
		entity.setSourceSupplier(tokenService.getSupplierInfo().getId());

		msgDao.addUserSyncMsg(entity);

	}

//	@Scheduled(fixedRate=10000)
	public void executeSyncTask(){

		Map<String,String> urlMap=new HashMap<>();


		List<DeviceSupplier> supplierList=supplierDao.getAllSupplier();

		supplierList.forEach((s) -> urlMap.put(s.getId(), s.getUserInfoNotifyUrl()));


		List<SupplierPushMsgTask>  msgList= msgDao.getUnfinishMsgList();


		msgList.parallelStream().forEach((msg)->notifyTool.doSyncTask(msg,urlMap));

	}
}
