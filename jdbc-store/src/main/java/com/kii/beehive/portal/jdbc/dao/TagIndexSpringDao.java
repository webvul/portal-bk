package com.kii.beehive.portal.jdbc.dao;

import com.kii.beehive.portal.jdbc.entity.TagIndex;

public class TagIndexSpringDao extends SpringBaseDao<TagIndex> {


	@Override
	protected String getTableName() {
		return TagIndexDao.TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return TagIndexDao.KEY;
	}

	@Override
	protected Class<TagIndex> getEntityCls() {
		return TagIndex.class;
	}




}
