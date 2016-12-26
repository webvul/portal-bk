//package com.kii.beehive.business.manager;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.business.helper.SyncMsgService;
//import com.kii.beehive.portal.exception.UserNotExistException;
//import com.kii.beehive.portal.service.ArchiveBeehiveUserDao;
//import com.kii.beehive.portal.service.PortalSyncUserDao;
//import com.kii.beehive.portal.store.entity.CustomProperty;
//import com.kii.beehive.portal.store.entity.PortalSyncUser;
//import com.kii.extension.sdk.exception.ObjectNotFoundException;
//
//@Component
//public class PortalSyncUserManager {
//
//	private Logger logger = LoggerFactory.getLogger(PortalSyncUserManager.class);
//
//	@Autowired
//	private ArchiveBeehiveUserDao archiveUserDao;
//
//	@Autowired
//	private PortalSyncUserDao userDao;
//
//	@Autowired
//	private SyncMsgService msgService;
//
//
//	public String addUser(PortalSyncUser user) {
//
//		PortalSyncUser archiveUser = archiveUserDao.queryInArchive(user);
//
//		//old user restore
//		if (archiveUser != null) {
//			archiveUserDao.removeArchive(archiveUser.getId());
//		}
//
//		String id = userDao.createUser(user);
//
//		msgService.addInsertMsg(id, user);
//		return id;
//	}
//
//
//	public void updateUser(PortalSyncUser user, String userID) {
//
//
//		try {
//			userDao.updateUser(user, userID);
//
//		} catch (ObjectNotFoundException e) {
//			throw new UserNotExistException(userID);
//		}
//		msgService.addUpdateMsg(userID, user);
//
//
//	}
//
//	public void updateCustomProp(String userID, Map<String, Object> customProps) {
//
//		PortalSyncUser user = new PortalSyncUser();
//		user.setCustomFields(new CustomProperty(customProps));
//		try {
//			userDao.updateUser(user, userID);
//
//		} catch (ObjectNotFoundException e) {
//			throw new UserNotExistException(userID);
//		}
//		msgService.addUpdateMsg(userID, user);
//
//	}
//
//	public List<PortalSyncUser> simpleQueryUser(Map<String, Object> queryMap) {
//
//		if (queryMap.isEmpty()) {
//			return userDao.getAllUsers();
//		} else {
//
//			Map<String, Object> map = new HashMap<>();
//			queryMap.forEach((k, v) -> {
//				if (k.startsWith("custom.")) {
//					String newK = k.replace(".", "-");
//					map.put(newK, v);
//				} else {
//					map.put(k, v);
//				}
//			});
//
//			return userDao.getUsersBySimpleQuery(map);
//		}
//	}
//
//	public void deleteUser(String userID) {
//
//		PortalSyncUser user = userDao.getUserByID(userID);
//
//		archiveUserDao.archive(user);
//
//		userDao.deleteUser(userID);
//
//		msgService.addDeleteMsg(userID);
//
//	}
//
//	public PortalSyncUser getUserByID(String userID) {
//		return userDao.getUserByID(userID);
//	}
//
//
//}
