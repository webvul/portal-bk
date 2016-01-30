package com.kii.beehive.portal.jdbc.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.kii.beehive.portal.jdbc.helper.AnnationBeanSqlParameterSource;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

public abstract class SpringBaseRelDao<T> {

	private Logger log= LoggerFactory.getLogger(SpringBaseRelDao.class);

	protected JdbcTemplate jdbcTemplate;


	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	private SimpleJdbcInsert insertTool;

	private RowMapper<T> rowMapper;

	protected abstract String getTableName();

	protected  abstract String getKey();
	
	private Class<T> entityClass;

	@Autowired
	public void setDataSource(DataSource dataSource) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate=new NamedParameterJdbcTemplate(dataSource);

		this.insertTool=new SimpleJdbcInsert(dataSource)
				.withTableName(getTableName())
				.usingGeneratedKeyColumns(getKey());

		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) type.getActualTypeArguments()[0];
		this.rowMapper=new BindClsRowMapper<T>(entityClass);
	}


	protected RowMapper getRowMapper(){
		return rowMapper;
	}

	public long insert(T entity){
		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);
		Number id=insertTool.executeAndReturnKey(parameters);
		return id.longValue();
	}
	
	public List<T> findAll() {  
        String sql = "SELECT * FROM " + this.getTableName();  
        return (List<T>) jdbcTemplate.query(sql,getRowMapper());
    }

	public T findByID(Serializable id){
		String sql = "SELECT t.* FROM " + this.getTableName() + " t WHERE t."+ getKey() +"=?";

		List<T> rows = jdbcTemplate.query(sql, new Object[]{id}, getRowMapper());
		if(rows.size() > 0){
			 return rows.get(0);
		}else{
			return null;
		}
	}

	public List<T> findByIDs(List<Long> ids){
			String sql = "select t.* from " + this.getTableName() + " t where t."+ getKey() +" in (:list) ";
			Map<String,Collection> param= Collections.singletonMap("list", ids);
			return  namedJdbcTemplate.query(sql, param,getRowMapper());
	}
	
	public List<T> findBySingleField(String fieldName, Object value) {  
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ fieldName +"=?";
        return jdbcTemplate.query(sql, new Object[]{value}, getRowMapper());
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
	
	public int[] batchInsert(List<T> entityList){

		if(entityList == null) {
			return new int[0];
		}
		SqlParameterSource[] sqlParameterSources = new SqlParameterSource[entityList.size()];

		for(int i=0;i<sqlParameterSources.length;i++){
			sqlParameterSources[i] = new AnnationBeanSqlParameterSource(entityList.get(i));
		}

		return insertTool.executeBatch(sqlParameterSources);
	}

}
