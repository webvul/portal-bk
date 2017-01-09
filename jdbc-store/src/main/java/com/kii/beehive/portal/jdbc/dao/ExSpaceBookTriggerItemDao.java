package com.kii.beehive.portal.jdbc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBookTriggerItem;

@Repository
public class ExSpaceBookTriggerItemDao extends SpringBaseDao<ExSpaceBookTriggerItem> {

	
	public static final String TABLE_NAME = "ex_space_book_trigger_item";
	public static final String KEY = "id";


	private static final String NEED_DELETE_SQL = "select esbi.* from  ex_space_book esb, ex_space_book_trigger_item esbi where esb.id = esbi.ex_space_book_id and\n" +
			"esb.end_date <= CURRENT_TIMESTAMP() and esb.is_deleted = FALSE and esb.is_added_trigger = FALSE";



	public List<ExSpaceBookTriggerItem> getNeedDeleteRule() {

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
