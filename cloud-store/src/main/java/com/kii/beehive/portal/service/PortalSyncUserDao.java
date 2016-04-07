package com.kii.beehive.portal.service;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.PortalSyncUser;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName = "portal", appBindSource = "propAppBindTool")
@Component
public class PortalSyncUserDao extends AbstractDataAccess<PortalSyncUser> {

	private Logger log = LoggerFactory.getLogger(PortalSyncUserDao.class);


//	@Autowired
//	private SimpleQueryTool queryTool;

	public String createUser(PortalSyncUser user) {

		String id = null;

		if (user.getAliUserID() == null) {
			id = user.getKiiUserID();
		} else {
			id = user.getAliUserID();
		}
		super.addEntity(user, id);

		return id;

	}


	public void updateUser(PortalSyncUser user, String userID) {

		boolean isExist = super.checkExist(userID);
		if (isExist) {
			user.setId(null);
			super.updateEntity(user, userID);
		} else {
			throw new ObjectNotFoundException();
		}
	}


	public void updateUserGroups(String userID, Set<String> groups) {


		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("groups", groups);

		super.updateEntity(paramMap, userID);

	}


//	public void updateUserCustomFields(String userID,Map<String,Object> fieldMap){
//
//		Map<String,Object> paramMap=new HashMap<>();
//
//		paramMap.putAll(fieldMap);
//
//		super.updateEntity(paramMap, userID);
//
//	}


	public List<PortalSyncUser> getUserByIDs(List<String> beehiveUserIDList) {
		if (null == beehiveUserIDList || beehiveUserIDList.isEmpty()) {
			return Collections.emptyList();
		}
		return super.getEntitys(beehiveUserIDList.toArray(new String[beehiveUserIDList.size()]));
	}

	public PortalSyncUser getUserByID(String userID) {
		return super.getObjectByID(userID);
	}


	public void deleteUser(String userID) {
		super.removeEntity(userID);
	}

	public List<PortalSyncUser> getUsersBySimpleQuery(Map<String, Object> params) {
		QueryParam query = getEntitysByFields(params);

		return super.fullQuery(query);
	}


	public QueryParam getEntitysByFields(Map<String, Object> fields) {

		ConditionBuilder builder = ConditionBuilder.andCondition();

		fields.forEach((k, v) -> {


			builder.equal(k, v);
		});

		return builder.getFinalCondition().build();

	}

	public List<PortalSyncUser> getAllUsers() {
		return super.getAll();
	}

	@Override
	protected Class<PortalSyncUser> getTypeCls() {
		return PortalSyncUser.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("beehiveUser");
	}


}
