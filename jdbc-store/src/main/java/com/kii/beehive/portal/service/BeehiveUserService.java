package com.kii.beehive.portal.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.dao.ArchiveBeehiveUserDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserGroupRelationDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;

@Component
public class BeehiveUserService {
	private Logger log= LoggerFactory.getLogger(BeehiveUserService.class);

	@Autowired
	private ArchiveBeehiveUserDao archiveBeehiveUserDao;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private BeehiveUserGroupRelationDao beehiveUserGroupRelationDao;


//	@Autowired
//	private KiiUserSyncDao kiiUserDao;

//	@Autowired
//	private SyncMsgService msgService;

	public long addUser(BeehiveUser user){

		BeehiveUser archiveUser=archiveBeehiveUserDao.queryInArchive(user);

		// TODO need to merge kiiUserDao and archiveBeehiveUserDao
		//old user restore
		if(archiveUser!=null){

//			archiveBeehiveUserDao.removeArchive(archiveUser.getId());
//			kiiUserDao.enableUser(archiveUser.getKiiUserID());

		}else {

//			kiiUserDao.addBeehiveUser(user);

		}
		log.debug("kiiUserID:" + user.getKiiUserID());

		long id=userDao.createUser(user);
// TODO need to merge msgService
//		msgService.addInsertMsg(String.valueOf(id), user);
		return id;
	}



	public void updateUser(BeehiveUser user,long userID) {

		userDao.updateUser(user,userID);
// TODO need to merge msgService
//		msgService.addUpdateMsg(String.valueOf(userID), user);

	}

//	public void updateCustomProp(long userID,Map<String,Object> customProps){
//
//		BeehiveUser user = userDao.findByID(userID);
//
//		Map<String, Object> custom = user.getCustom();
//
//		Set<String> keys = customProps.keySet();
//		for(String key : keys) {
//			custom.put(key, customProps.get(key));
//		}
//
//		userDao.updateUser(user);
//
//		msgService.addUpdateMsg(String.valueOf(userID), user);
//
//	}

	public List<BeehiveUser> simpleQueryUser(Map<String,Object> queryMap){



		StringBuffer buffer = new StringBuffer();

		List<Object> valueList = new ArrayList<>();

		appendWhere(buffer, valueList, queryMap, BeehiveUser.USER_ID);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.KII_USER_ID);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.KII_LOGIN_NAME);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.USER_NAME);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.PHONE);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.MAIL);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.COMPANY);
		appendWhere(buffer, valueList, queryMap, BeehiveUser.ROLE);

		String where = buffer.toString();
		if(where.length() > 0) {
			where = where.substring(0, where.length() - " AND ".length());
		} else {
			where = " 1=1";
		}

		String sql = "SELECT * FROM " + userDao.getTableName() + " WHERE " + where + " ORDER BY " + BeehiveUser.USER_ID;
		return userDao.query(sql, valueList.toArray(new Object[valueList.size()]));
	}

	private StringBuffer appendWhere(StringBuffer condition, List<Object> valueList, Map<String, Object> searchFields, String fieldName) {

		final String AND = " AND ";

		final String LIKE = " like ?";

		Object value = searchFields.get(fieldName);
		if(value != null) {
			condition.append(fieldName).append(LIKE).append(AND);
			valueList.add("%"+value+"%");
		}

		return condition;
	}

	public void deleteUser(long userID) {

		BeehiveUser user = userDao.findByID(userID);
		// TODO need to merge kiiUserDao
//		kiiUserDao.removeBeehiveUser(user.getKiiUserID());
		archiveBeehiveUserDao.archive(user);

		userDao.deleteByID(userID);
// TODO need to merge msgService
//		msgService.addDeleteMsg(String.valueOf(userID));

	}

	public BeehiveUser getUserByID(long userID) {
		return userDao.findByID(userID);
	}


	public List<BeehiveUser> findUsersByUserGroupID(long userGroupID) {
		String sql = "SELECT user.* FROM " + userDao.getTableName() + " user WHERE user.user_id in " +
				"(SELECT rel.user_id FROM " + beehiveUserGroupRelationDao.getTableName() + " rel WHERE rel.user_group_id=?) " +
				"ORDER BY user." + BeehiveUser.USER_ID;

		List<BeehiveUser> list = userDao.query(sql, userGroupID);
		return list;
	}
}