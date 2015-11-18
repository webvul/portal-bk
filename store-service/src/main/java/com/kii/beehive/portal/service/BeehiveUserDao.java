package com.kii.beehive.portal.service;


import javax.management.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.helper.SimpleQueryTool;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal")
@Component
public class BeehiveUserDao extends AbstractDataAccess<BeehiveUser> {

	private Logger log= LoggerFactory.getLogger(BeehiveUserDao.class);


	@Autowired
	private SimpleQueryTool queryTool;

	public void createUser(BeehiveUser user){

		super.addEntity(user, user.getKiiUserID());


	}


	public void updateUser(BeehiveUser user) {

		super.updateEntity(user, user.getId());
	}



	public void updateUserGroups(String userID, Set<String> groups){

		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("groups", groups);

		super.updateEntity(paramMap, userID);

	}


	public void updateUserCustomFields(String userID,Map<String,Object> fieldMap){

		Map<String,Object> paramMap=new HashMap<>();

		paramMap.putAll(fieldMap);

		super.updateEntity(paramMap, userID);

	}


	public List<BeehiveUser> getUserByIDs(List<String> beehiveUserIDList) {


		return super.getEntitys(beehiveUserIDList.toArray(new String[0]));

	}

	public BeehiveUser  getUserByID(String userID){
		return super.getObjectByID(userID);
	}


	public void deleteUser(String userID){
		super.removeEntity(userID);
	}

	public List<BeehiveUser>  getUsersBySimpleQuery(Map<String,Object> params){
		QueryParam query=queryTool.getEntitysByFields(params);

		return super.fullQuery(query);
	}

	public List<BeehiveUser> getAllUsers(){
		return super.getAll();
	}

	@Override
	protected Class<BeehiveUser> getTypeCls() {
		return BeehiveUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveUser");
	}


}
