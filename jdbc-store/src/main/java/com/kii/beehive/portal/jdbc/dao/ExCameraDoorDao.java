package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jdbc.entity.ExCameraDoor;

@Repository
public class ExCameraDoorDao extends SpringBaseDao<ExCameraDoor> {

	
	public static final String TABLE_NAME = "ex_camera_door";
	public static final String KEY = "id";





	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
}
