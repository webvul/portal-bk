package com.kii.beehive.portal.service;

import java.util.*;


import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal")
@Component
public class BeehiveUserDao extends AbstractDataAccess<BeehiveUser> {


	public void createUser(BeehiveUser user){

		super.addEntity(user, user.getKiiUserID());


	}


	public void updateUser(BeehiveUser user) {

		super.updateEntity(user, user.getBeehiveUserID());
	}

	public void updateUserGroups(String userID, Set<String> groups){

		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("groups", groups);

		super.updateEntity(paramMap, userID);

	}

	public Map<String,Object> getUserCustomInfoByID(String userID,Set<String> field){

		BeehiveUser user=super.getObjectByID(userID);


		return user.getCustomFields().filter(field);

	}

	public void updateUserCustomFields(String userID,Map<String,Object> fieldMap){

		Map<String,Object> paramMap=new HashMap<>();

		paramMap.putAll(fieldMap);

		super.updateEntity(paramMap, userID);

	}

	public void deleteUser(String userID){
		super.removeEntity(userID);
	}

	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveUser");
	}

	/**
	 * get beehive user
	 *
	 * @param beehiveUserID
	 * @return
	 */
	public BeehiveUser getUserByID(String beehiveUserID) {

		BeehiveUser user = getEntity("beehiveUserID", beehiveUserID);

		return user;
	}

	/**
	 * get list of beehive users
	 *
	 * @param beehiveUserIDList
	 * @return
     */
	public List<BeehiveUser> getUserByIDs(List<String> beehiveUserIDList) {

		List<BeehiveUser> resultList = getEntitys("beehiveUserID", Arrays.asList(beehiveUserIDList));

		return resultList;
	}

	/**
	 * get the list of non existing beehive user ID
	 * @param beehiveUserIDList
	 * @return
     */
	public List<String> getNonExistUserIDs(List<String> beehiveUserIDList) {

		List<BeehiveUser> resultList = this.getUserByIDs(beehiveUserIDList);

		List<String> existList = new ArrayList<>();
		resultList.stream().forEach((e)->{
			existList.add(e.getBeehiveUserID());
		});

		List<String> nonExistList = new ArrayList<String>();
		nonExistList.addAll(beehiveUserIDList);
		nonExistList.removeAll(existList);

		return nonExistList;
	}

}
