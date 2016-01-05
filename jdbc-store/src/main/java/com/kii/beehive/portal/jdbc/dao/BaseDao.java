package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.kii.beehive.portal.jdbc.entity.DBEntity;
import com.kii.beehive.portal.jdbc.helper.AnnationBeanSqlParameterSource;

public abstract class BaseDao<T extends DBEntity> {

	private Logger log= LoggerFactory.getLogger(BaseDao.class);

	protected JdbcTemplate jdbcTemplate;
	
	//private Class<T> entityClass;
	
	private SimpleJdbcInsert insertTool;
	

	public abstract String getTableName();
	
	public abstract String getKey();
	
	public abstract long update(T entity);
	
	public abstract List<T> mapToList(List<Map<String, Object>> rows);

	protected T mapToListForDBEntity(T entity, Map<String, Object> row) {
		entity.setCreateBy((String)row.get(DBEntity.CREATE_BY));
		entity.setCreateDate((Date)row.get(DBEntity.CREATE_DATE));
		entity.setModifyBy((String)row.get(DBEntity.MODIFY_BY));
		entity.setModifyDate((Date)row.get(DBEntity.MODIFY_DATE));
		return entity;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		//ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();  
        //entityClass = (Class<T>) type.getActualTypeArguments()[0];  

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.insertTool=new SimpleJdbcInsert(dataSource)
				.withTableName(getTableName())
				.usingGeneratedKeyColumns(getKey());
	}
	
	public List<T> findAll() {  
        String sql = "SELECT * FROM " + this.getTableName();  
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return mapToList(rows);
    }
	
	public List<T> findBySingleField(String fieldName, Object value) {  
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ fieldName +"=?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, value);
        return mapToList(rows);
    }

	public List<T> query(String sql, Object... values) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, values);
		return mapToList(rows);
	}
	
	public T findByID(Serializable id){
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ getKey() +"=?";  
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
		if(rows.size() > 0){
			 return mapToList(rows).get(0);
		}else{
			return null;
		}
	}
	
	public List<T> findByIDs(Object[] ids){
		if(ids.length > 0){
			StringBuilder sb = new StringBuilder(ids.length*2-1);
			for(int i=0; i<ids.length ; i++){
				if(sb.length() > 0) 
					sb.append(",");
				sb.append("?");
			}
			
			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ getKey() +" IN ("+sb.toString()+")";
	        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, ids);
	        return mapToList(rows);
		}else{
			return null;
		}
	}
	
	public int deleteByID(Serializable id){
		String sql = "DELETE FROM " + this.getTableName() + " WHERE "+ getKey() +"=?";  
        return jdbcTemplate.update(sql,id);
	}
	
	public boolean IsIdExist(Serializable id){
		boolean result = false;
		String sql = "SELECT count(1) FROM " + this.getTableName() + " WHERE "+ getKey() +"=?";  
		Long count =  jdbcTemplate.queryForObject(sql,new Object[]{id}, Long.class);
        if (count > 0) {
    		result = true;
    	}

    	return result;
	}

	public int execute(String sql, List<Object> values) {

		Object[] params = null;
		if(values != null) {
			params = values.toArray(new Object[values.size()]);
		} else {
			params = new Object[0];
		}

		return execute(sql, params);
	}

	public int execute(String sql, Object[] params) {
		return jdbcTemplate.update(sql, params);
	}

	public long saveOrUpdate(T entity){
		if(entity.getId() == 0){
			SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);
			Number id=insertTool.executeAndReturnKey(parameters);
			return id.longValue();
		}else{
			this.update(entity);
			return entity.getId();
		}
	}

	protected int update(T entity, String[] columns) {

		// get column info by reflection
		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);

		List<Object> fieldValues = new ArrayList<>();
		StringBuffer update = new StringBuffer();

		// append each column and value
		for(String column : columns) {
			Object value = parameters.getValue(column);

			if(value != null) {
				update.append(column).append("=?, ");
				fieldValues.add(value);
			}
		}

		// combine sql string
		String updateString = update.toString();
		if(updateString.length() > 0) {
			updateString = updateString.substring(0, updateString.length() - 2);
		}

		StringBuffer sql = new StringBuffer("UPDATE ").append(getTableName()).append(" SET ");
		sql.append(updateString).append(" WHERE ").append(getKey()).append("=?");
		fieldValues.add(entity.getId());

		// update
		int updateResult = jdbcTemplate.update(sql.toString(), fieldValues.toArray(new Object[fieldValues.size()]));

		log.debug("BaseDao Update SQL: " + sql.toString());
		log.debug("BaseDao Update Values: " + fieldValues);

		return updateResult;
	}
	
}
