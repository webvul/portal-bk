package com.kii.beehive.portal.jdbc.dao;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingLocationRelation;

@Repository
public class ThingLocationRelDao extends SpringBaseDao<GlobalThingInfo> {


	public static final String TABLE_NAME="rel_thing_location";

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return ThingLocationRelation.ID;
	}
}
