package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.exception.UserNotExistException;
import com.kii.beehive.portal.helper.SyncMsgService;
import com.kii.beehive.portal.service.ArchiveBeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.BeehiveUserGroupDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.BeehiveUserGroup;
import com.kii.beehive.portal.store.entity.CustomProperty;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@Component
public class UserManager {

	private Logger logger= LoggerFactory.getLogger(UserManager.class);

	@Autowired
	private ArchiveBeehiveUserDao archiveUserDao;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private BeehiveUserGroupDao userGroupDao;

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

		String id=userDao.createUser(user);

		msgService.addInsertMsg(id,user);
		return id;
	}



	public void updateUser(BeehiveUser user,String userID) {



		try {
			userDao.updateUser(user, userID);

		}catch(ObjectNotFoundException e){
			throw new UserNotExistException(userID);
		}
		msgService.addUpdateMsg(userID, user);


	}

	public void updateCustomProp(String userID,Map<String,Object> customProps){

		BeehiveUser user=new BeehiveUser();
		user.setCustomFields(new CustomProperty(customProps));
		try{
			userDao.updateUser(user, userID);

		}catch(ObjectNotFoundException e){
			throw new UserNotExistException(userID);
		}
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

		this.removeUserFromUserGroup(userID, user.getGroups());

		kiiUserDao.disableBeehiveUser(user);
		archiveUserDao.archive(user);

		userDao.deleteUser(userID);

		msgService.addDeleteMsg(userID);

	}

	/**
	 * remove the userID from the user groups specified by the param "userGroupIDs"
	 * @param userID
	 * @param userGroupIDs
     */
	private void removeUserFromUserGroup(String userID, Set<String> userGroupIDs) {

		if(userGroupIDs == null) {
			return;
		}

		List<BeehiveUserGroup> beehiveUserGroupList = userGroupDao.getUserGroupByIDs(new ArrayList<>(userGroupIDs));

		if(beehiveUserGroupList != null) {
			// remove the user from each group
			for(BeehiveUserGroup beehiveUserGroup : beehiveUserGroupList) {
				Set<String> users = beehiveUserGroup.getUsers();
				if(users != null) {
					users.remove(userID);
					userGroupDao.updateUsers(beehiveUserGroup.getUserGroupID(), users);
				}
			}
		}
	}

	public BeehiveUser getUserByID(String userID) {
		return userDao.getUserByID(userID);
	}

	/**
	 * return the non existing userIDs
	 * @param userIDs
	 * @return
     */
	public Set<String> checkNonExistingUserID(Set<String> userIDs) {

		if(userIDs == null) {
			return new HashSet<String>();
		}

		// get the existing user IDs
		List<BeehiveUser> beehiveUserList = userDao.getUserByIDs(new ArrayList<>(userIDs));
		Set<String> existingUserIDList = new HashSet<>();
		for(BeehiveUser user : beehiveUserList) {
			existingUserIDList.add(user.getAliUserID());
		}

		// get the non existing user IDs
		Set<String> nonExistingUserIDs = new HashSet<>();
		nonExistingUserIDs.addAll(userIDs);
		nonExistingUserIDs.removeAll(existingUserIDList);

		return nonExistingUserIDs;
	}

	/**
	 * validate whether the userIDs in param "userIDList" existing
	 * if any userID not existing, throw UserNotExistException
	 * @param userIDs
     */
	public void validateUserIDExisting(Set<String> userIDs) {

		Set<String> nonExistingUserIDList = this.checkNonExistingUserID(userIDs);

		if(nonExistingUserIDList != null && !nonExistingUserIDList.isEmpty()) {
			StringBuffer buffer = new StringBuffer();

			for (String nonExistingUserID : nonExistingUserIDList) {
				buffer.append(nonExistingUserID).append(",");

			}
			buffer.deleteCharAt(buffer.length() - 1);

			throw new UserNotExistException(buffer.toString());
		}

	}
}
