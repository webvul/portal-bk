package com.kii.beehive.portal.jdbc.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;

@Repository
public class IndustryTemplateDao extends SpringBaseDao<IndustryTemplate> {

	
	public static final String TABLE_NAME = "rel_industry_template";
	public static final String KEY = "id";
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

	public List<IndustryTemplate> findByField(List<String> fieldNames, List<Object> values) {

		StringBuffer where = new StringBuffer();

		for(String fieldName : fieldNames) {
			where.append(" AND ").append(fieldName).append("=?");
		}

		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE 1=1" + where.toString();
		List<IndustryTemplate> rows = jdbcTemplate.query(sql, values.toArray(), getRowMapper() );
		return rows;
	}
}
