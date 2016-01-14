package com.kii.beehive.business.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.service.DeviceSupplierDao;
import com.kii.beehive.portal.service.UserSyncMsgDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsg;
import com.kii.beehive.portal.store.entity.usersync.UserSyncMsgType;

@Component
public class SyncMsgService {

	//@Value("${spring.profile}")
	//private String profile;

	@Autowired
	private UserSyncMsgDao msgDao;

	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private DeviceSupplierDao  supplierDao;


	@Autowired
	private NotifySenderTool notifyTool;


	public void addUpdateMsg(String userID,BeehiveUser user){
		addSyncMsg(userID, UserSyncMsgType.Update,user);
	}



	public void addDeleteMsg(String userID){
		addSyncMsg(userID, UserSyncMsgType.Delete,null);

	}


	public void addInsertMsg(String userID,BeehiveUser user){
		addSyncMsg(userID, UserSyncMsgType.Create,user);

	}

	private void addSyncMsg(String userID,UserSyncMsgType type,BeehiveUser user){

		UserSyncMsg  msg=new UserSyncMsg();

		msg.setUserID(userID);
		msg.setType(type);
		msg.setRetryCount(0);

		SupplierPushMsgTask entity=new SupplierPushMsgTask();

		entity.setMsgContent(msg);
		entity.setSourceSupplier(AuthInfoStore.getUserID());

		msgDao.addUserSyncMsg(entity);

//		if("production".equals(profile)) {

		notifyTool.doMsgSendTask(entity, supplierDao.getUrlMap());
//		}
	}



	@Scheduled(fixedRate=1000*60*60)
	public void executeSyncTask(){

//		if(!"production".equals(profile)) {
//			return;
//		}

		List<SupplierPushMsgTask>  msgList= msgDao.getUnfinishMsgList();


		msgList.parallelStream().forEach((msg)->notifyTool.doMsgSendTask(msg, supplierDao.getUrlMap()));

	}
}
