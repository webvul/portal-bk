package com.kii.beehive.portal.jdbc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;

@Repository
public class ExSpaceBookDao extends SpringBaseDao<ExSpaceBook> {

	
	public static final String TABLE_NAME = "ex_space_book";
	public static final String KEY = "id";
	private static final String NEED_CREATE_SQL = "select * from  ex_space_book esb where esb.begin_date <= CURRENT_TIMESTAMP() " +
			"and esb.is_deleted = FALSE and esb.is_added_trigger = FALSE and esb.create_trigger_error = FALSE ";

	private static final String NEED_DELETE_SQL = "select * from  ex_space_book esb where esb.end_date <= CURRENT_TIMESTAMP() " +
			"and esb.is_deleted = FALSE and esb.is_added_trigger = TRUE and esb.is_deleted_trigger = FALSE and esb.create_trigger_error = FALSE ";


	public List<ExSpaceBook> getNeedCreateRule() {

		Map<String, Object> params = new HashMap<>();

		return queryByNamedParam(NEED_CREATE_SQL, params);

	}

	public List<ExSpaceBook> getNeedDeleteRule() {

		Map<String, Object> params = new HashMap<>();

		return queryByNamedParam(NEED_DELETE_SQL, params);

	}





	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
}
