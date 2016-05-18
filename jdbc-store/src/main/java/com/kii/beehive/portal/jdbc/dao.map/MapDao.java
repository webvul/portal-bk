package com.kii.beehive.portal.jdbc.dao.map;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by USER on 2/26/16.
 */
@Repository
public class MapDao {

    protected JdbcTemplate jdbcTemplate;

    protected void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> findAll(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return rows;
    }

    public List<Map<String, Object>> findBySingleField(String tableName, String fieldName, Object value) {
        String sql = "SELECT * FROM " + tableName + " WHERE "+ fieldName +"=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, value);
        return rows;
    }

    public List<Map<String, Object>> findByField(String tableName, String[] fieldNames, Object[] values) {

        StringBuffer where = new StringBuffer();

        for(String fieldName : fieldNames) {
            where.append(" AND ").append(fieldName).append("=?");
        }

        String sql = "SELECT * FROM " + tableName + " WHERE 1=1" + where.toString();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, values);
        return rows;
    }

    public List<Map<String, Object>> findByField(String tableName, List<String> fieldNames, List<Object> values) {

        return this.findByField(tableName,
                fieldNames.toArray(new String[fieldNames.size()]),
                values.toArray(new Object[values.size()]));
    }

    public int insert(String tableName, String[] fieldNames, Object[] values) {

        StringBuffer fieldSql = new StringBuffer();
        StringBuffer valueSql = new StringBuffer();

        for(String fieldName : fieldNames) {
            fieldSql.append(",").append(fieldName);
            valueSql.append(",?");
        }
        fieldSql.deleteCharAt(0);
        valueSql.deleteCharAt(0);

        String sql = "INSERT INTO " + tableName + " (" + fieldSql.toString() + ") VALUES (" + valueSql.toString() + ")";
        int result = jdbcTemplate.update(sql, values);
        return result;
    }
    public List<Map<String, Object>> query(String sql, List<Object> values) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, values.toArray(new Object[]{values.size()}));
        return rows;
    }

    public List<Map<String, Object>> query(String sql, Object[] values) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, values);
        return rows;
    }

    public int execute(String sql, List<Object> params) {
        return jdbcTemplate.update(sql, params.toArray(new Object[]{params.size()}));
    }

    public int execute(String sql, Object[] params) {
        return jdbcTemplate.update(sql, params);
    }

    public int[] batchExecute(String sql, List<Object[]> paramsList) {
        return jdbcTemplate.batchUpdate(sql, paramsList);
    }

}
