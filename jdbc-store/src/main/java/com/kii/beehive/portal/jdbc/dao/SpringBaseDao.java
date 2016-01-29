package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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

import com.kii.beehive.portal.auth.AuthInfoStore;
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

		this.updateTool= BindClsFullUpdateTool.newInstance(dataSource,getTableName(),"id",getEntityCls(),getKey());
		this.rowMapper=new BindClsRowMapper<T>(getEntityCls());
	}


	protected RowMapper getRowMapper(){
		return rowMapper;
	}

	public long insert(T entity){


		entity.setCreateBy(AuthInfoStore.getUserID());
		entity.setCreateDate(new Date());
		entity.setModifyBy(AuthInfoStore.getUserID());
		entity.setModifyDate(new Date());
		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);
		Number id=insertTool.executeAndReturnKey(parameters);
		return id.longValue();
	}

	public T findByID(Serializable id){
		String sql = "SELECT t.* FROM " + this.getTableName() + " t WHERE t."+ getKey() +"=?";
		Object[] param=new Object[]{id};

		return (T) jdbcTemplate.queryForObject(sql, param, getRowMapper());

	}

	public List<T> findByIDs(List<Long> ids){

			String sql = "select t.* from " + this.getTableName() + " t where t."+ getKey() +" in (:list) ";
			Map<String,Collection> param= Collections.singletonMap("list", new ArrayList<Long>(ids));
			return  namedJdbcTemplate.query(sql, param,getRowMapper());

	}

	public int deleteByID(Serializable id){
		String sql = "DELETE FROM " + this.getTableName() + " WHERE "+ getKey() +"=?";
		return jdbcTemplate.update(sql,id);
	}

	public int updateEntityAllByID(T entity) {

		return updateTool.execute(entity);
	}


	public int updateEntityByID(Map<String,Object> paramMap,long id) {

		paramMap.put("id",id);
		BindClsFullUpdateTool tool=updateTool.cloneInstance(paramMap,"id");

		return tool.execute(paramMap);
	}


	public int updateEntityByField(T entity,String conditionField) {

		BindClsFullUpdateTool tool=updateTool.cloneInstance(entity,conditionField,true);

		return tool.execute(entity);
	}

	public int updateEntityAllByField(T entity,String conditionField) {

		BindClsFullUpdateTool tool=updateTool.cloneInstance(entity,conditionField,false);

		return tool.execute(entity);
	}

	protected int doUpdate(String updateSql,Object... params){

		int start=updateSql.indexOf("set")+3;

		int end=updateSql.indexOf("where");

		String header=updateSql.substring(0,start);
		String tail=updateSql.substring(end);

		String updates=updateSql.substring(start+1,end);

		String newUpdates=" modify_by = ? , modify_date= ? , "+updates;

		String newUpdateSql=header+newUpdates+tail;

		Object[] newParams=new Object[params.length+2];

		newParams[0]=AuthInfoStore.getUserID();
		newParams[1]=new Date();
		System.arraycopy(params,0,newParams,2,params.length);

		return jdbcTemplate.update(newUpdateSql, newParams);

	}

}
