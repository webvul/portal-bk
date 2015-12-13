package com.kii.beehive.portal.service;


import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.dao.BeehiveUserGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserGroupDao;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroup;
import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroupRelation;

@Component
public class BeehiveUserGroupService {
	private Logger log= LoggerFactory.getLogger(BeehiveUserGroupService.class);

	@Autowired
	private BeehiveUserDao beehiveUserDao;

	@Autowired
	private BeehiveUserGroupDao beehiveUserGroupDao;

	@Autowired
	private BeehiveUserGroupRelationDao beehiveUserGroupRelationDao;

	/**
	 * check whether the param userGroupID existing or not
	 * @param userGroupID
	 * @return
	 */
	public boolean checkUserGroupIDExist(long userGroupID) {
		BeehiveUserGroup userGroup = beehiveUserGroupDao.findByID(userGroupID);

		return userGroup != null;
	}

	public boolean checkUserGroupNameExist(String userGroupName) {
		BeehiveUserGroup userGroup = getUserGroupByName(userGroupName);

		return userGroup != null;
	}

	public BeehiveUserGroup getUserGroupByName(String userGroupName) {

		List<BeehiveUserGroup> list = beehiveUserGroupDao.findUserGroupByUserGroupName(userGroupName);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public List<BeehiveUserGroup> findUserGroupsByNameLike(String userGroupName) {

		List<BeehiveUserGroup> list = beehiveUserGroupDao.findUserGroupByUserGroupNameLike(userGroupName);

		return list;
	}

	/**
	 * create user group in Beehive (table BeehiveUserGroup)
	 *
	 * @param userGroup
	 */
	public long createUserGroup(BeehiveUserGroup userGroup) {
		// creat user group
		long userGroupID = beehiveUserGroupDao.createUserGroup(userGroup);

		return userGroupID;
	}

	/**
	 * update user group in Beehive (table BeehiveUserGroup)
	 * if any change on the relation between user and user group, update in user info too(table BeehiveUser)
	 *
	 * @param userGroup
	 */
	public void updateUserGroup(BeehiveUserGroup userGroup) {

		beehiveUserGroupDao.updateUserGroup(userGroup.getId(), userGroup);

	}

	/**
	 * delete user group in Beehive (table BeehiveUserGroup)
	 * if there is user info under the user group, add the group to these user info(table BeehiveUser)
	 *
	 * @param userGroupID
	 */
	public void deleteUserGroup(long userGroupID) {

		beehiveUserGroupDao.deleteByID(userGroupID);

	}

	public List<Long> addUsers(long userGroupID, List<Long> userIDList) {

		List<Long> nonExistUserIDList = new ArrayList<>();

		for(long userID : userIDList){
			BeehiveUser beehiveUser = beehiveUserDao.findByID(userID);
			if(beehiveUser != null){
				beehiveUserGroupRelationDao.saveOrUpdate(new BeehiveUserGroupRelation(userGroupID, userID));
			}else{
				log.warn("BeehiveUser is null, id = " + userID);
				nonExistUserIDList.add(userID);
			}
		}

		return nonExistUserIDList;
	}

	public List<Long> removeUsers(long userGroupID, List<Long> userIDList) {

		List<Long> nonExistUserIDList = new ArrayList<>();

		for(long userID : userIDList){
			BeehiveUser beehiveUser = beehiveUserDao.findByID(userID);
			if(beehiveUser != null){
				beehiveUserGroupRelationDao.delete(userGroupID, userID);
			}else{
				log.warn("BeehiveUser is null, id = " + userID);
				nonExistUserIDList.add(userID);
			}
		}

		return nonExistUserIDList;
	}

}