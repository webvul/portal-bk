package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.kii.beehive.portal.jdbc.entity.DBEntity;
import com.kii.beehive.portal.jdbc.helper.AnnationBeanSqlParameterSource;
import com.kii.beehive.portal.jdbc.helper.BindClsFullUpdateTool;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

public abstract class SpringBaseDao<T extends DBEntity> {


	private Logger log= LoggerFactory.getLogger(BaseDao.class);

	protected JdbcTemplate jdbcTemplate;


	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	private SimpleJdbcInsert insertTool;

	private RowMapper<T> rowMapper;

	private BindClsFullUpdateTool updateTool;


	protected abstract String getTableName();

	protected  abstract String getKey();

	protected abstract Class<T> getEntityCls();


	@Autowired
	public void setDataSource(DataSource dataSource) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate=new NamedParameterJdbcTemplate(dataSource);

		this.insertTool=new SimpleJdbcInsert(dataSource)
				.withTableName(getTableName())
				.usingGeneratedKeyColumns(getKey());

		this.updateTool=new BindClsFullUpdateTool(dataSource,getTableName());

		this.rowMapper=new BindClsRowMapper<T>(getEntityCls());
	}


	protected RowMapper getRowMapper(){
		return rowMapper;
	}

	public long insert(T entity){

		entity.setCreateBy("");
		entity.setCreateDate(new Date());
		entity.setModifyBy("");
		entity.setModifyDate(new Date());
		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);
		Number id=insertTool.executeAndReturnKey(parameters);
		return id.longValue();
	}

	public T findByID(Serializable id){
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ getKey() +"=?";
		return (T) jdbcTemplate.queryForObject(sql, getRowMapper());
	}

	public List<T> findByIDs(long[] ids){


			String sql = "SELECT * FROM " + this.getTableName() + " WHERE "+ getKey() +" IN (:list)";
			Map<String,Object> param= Collections.singletonMap("list",ids);
			return  namedJdbcTemplate.query(sql, param,getRowMapper());

	}

	public int deleteByID(Serializable id){
		String sql = "DELETE FROM " + this.getTableName() + " WHERE "+ getKey() +"=?";
		return jdbcTemplate.update(sql,id);
	}

	public int updateEntity(T entity) {

		entity.setModifyDate(new Date());
		entity.setModifyBy("");

		return updateTool.executeUpdate(entity);
	}

}
