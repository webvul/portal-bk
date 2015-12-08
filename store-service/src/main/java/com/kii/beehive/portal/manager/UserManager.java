package com.kii.beehive.portal.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.helper.SyncMsgService;
import com.kii.beehive.portal.service.ArchiveBeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.CustomProperty;


@Component
public class UserManager {

	private Logger logger= LoggerFactory.getLogger(UserManager.class);

	@Autowired
	private ArchiveBeehiveUserDao archiveUserDao;

	@Autowired
	private BeehiveUserDao userDao;

//	@Autowired
//	private BeehiveUserGroupDao userGroupDao;

	@Autowired
	private KiiUserSyncDao kiiUserDao;

	@Autowired
	private SyncMsgService msgService;



	public String addUser(BeehiveUser user){

		BeehiveUser archiveUser=archiveUserDao.queryInArchive(user);

		//old user restore
		if(archiveUser!=null){

			archiveUserDao.removeArchive(archiveUser.getId());
			kiiUserDao.enableUser(archiveUser.getKiiUserID());

		}else {

			kiiUserDao.addBeehiveUser(user);

		}
		logger.debug("kiiUserID:" + user.getKiiUserID());

		String id=userDao.createUser(user);

		msgService.addInsertMsg(id,user);
		return id;
	}



	public void updateUser(BeehiveUser user,String userID) {



		userDao.updateUser(user,userID);

		msgService.addUpdateMsg(userID, user);


	}

	public void updateCustomProp(String userID,Map<String,Object> customProps){

		BeehiveUser user=new BeehiveUser();
		user.setCustomFields(new CustomProperty(customProps));

		userDao.updateUser(user, userID);

		msgService.addUpdateMsg(userID, user);

	}

	public List<BeehiveUser> simpleQueryUser(Map<String,Object> queryMap){

		if(queryMap.isEmpty()){
			return userDao.getAllUsers();
		}else {

			Map<String,Object> map=new HashMap<>();
			queryMap.forEach((k,v)->{
				if(k.startsWith("custom.")){
					String newK=k.replace(".","-");
					map.put(newK,v);
				}else{
					map.put(k,v);
				}
			});

			return userDao.getUsersBySimpleQuery(map);
		}
	}


	public void deleteUser(String userID) {



		BeehiveUser user = userDao.getUserByID(userID);
		archiveUserDao.archive(user);

		kiiUserDao.removeBeehiveUser(userID);

//		userGroupDao.checkUserGroupsChange(user,null);

		userDao.deleteUser(userID);

		msgService.addDeleteMsg(userID);


	}



	public BeehiveUser getUserByID(String userID) {
		return userDao.getUserByID(userID);
	}
}
