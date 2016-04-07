package com.kii.beehive.portal.jdbc.dao;

import java.util.List;

import com.kii.beehive.portal.jdbc.entity.BeehiveUser;

public class BeehiveUserDao extends  SpringBaseDao<BeehiveUser> {
	@Override
	protected String getTableName() {
		return "beehive_user";
	}

	@Override
	protected String getKey() {
		return "beehive_user_id";
	}
	
	public List<BeehiveUser> getUserByIDs(List<Long> userIDList) {

		return super.findByIDs(userIDList);
	}
	
	public BeehiveUser getUserByID(Long userID) {

		return super.findByID(userID);

	}
	
	public void deleteUser(Long userID) {

		super.deleteByID(userID);

	}
}
