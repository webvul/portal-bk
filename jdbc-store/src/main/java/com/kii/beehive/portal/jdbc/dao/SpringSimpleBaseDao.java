package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.jdbc.entity.DBEntity;
import com.kii.beehive.portal.jdbc.helper.AnnationBeanSqlParameterSource;
import com.kii.beehive.portal.jdbc.helper.BindClsFullUpdateTool;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

public abstract  class SpringSimpleBaseDao<T extends DBEntity> {

	private Logger log = LoggerFactory.getLogger(SpringSimpleBaseDao.class);


	protected abstract String getTableName();

	protected abstract String getKey();


	protected JdbcTemplate jdbcTemplate;

	protected NamedParameterJdbcTemplate namedJdbcTemplate;


	private SimpleJdbcInsert insertTool;

	private BindClsFullUpdateTool updateTool;

	private RowMapper<T> rowMapper;

	private Class<T> entityClass;


	private BeanWrapper beanWrapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public void setDataSource(DataSource dataSource) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.insertTool = new SimpleJdbcInsert(dataSource)
				.withTableName(getTableName())
				.usingGeneratedKeyColumns(getKey());

		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) type.getActualTypeArguments()[0];
		this.updateTool = BindClsFullUpdateTool.newInstance(dataSource, getTableName(), "id", entityClass, getKey(),objectMapper);
		this.rowMapper = new BindClsRowMapper<T>(entityClass,objectMapper);

		this.beanWrapper= PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(entityClass));
	}


	protected RowMapper<T> getRowMapper() {
		return rowMapper;
	}



	public <T extends DBEntity> long insert(T entity) {

		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity,objectMapper);
		Number id = insertTool.executeAndReturnKey(parameters);
		return id.longValue();
	}


	public T findByID(Serializable id) {
		String sql = "SELECT t.* FROM " + this.getTableName() + " t WHERE t." + getKey() + "=?";

		List<T> rows = jdbcTemplate.query(sql, new Object[]{id}, getRowMapper());
		if (rows.size() > 0) {
			return rows.get(0);
		} else {
			return null;
		}
	}

	public List<T> findBySingleField(String fieldName, Object value) {
		String sql = "SELECT * FROM " + this.getTableName() + " WHERE " + fieldName + "=?";
		return jdbcTemplate.query(sql, new Object[]{value}, getRowMapper());
	}

	public int deleteByID(Serializable id) {
		String sql = "DELETE FROM " + this.getTableName() + " WHERE " + getKey() + "=?";
		return jdbcTemplate.update(sql, id);
	}

	public int updateEntityAllByID(T entity) {

		return updateTool.executeSimple(entity);
	}

	public int updateEntityByID(Map<String, Object> paramMap, long id) {

		paramMap.put("id", id);
		BindClsFullUpdateTool tool = updateTool.cloneInstance(paramMap, "id");

		return tool.execute(paramMap);
	}

	public <E> List<E> findSingleFieldBySingleField(String returnField, String matchField, Collection<?> value,
													Class<E> elementType) {
		if (null == value || value.isEmpty()) {
			return Collections.emptyList();
		}
		String sql = "SELECT DISTINCT " + returnField + " FROM " + this.getTableName() + " WHERE " + matchField +
				" IN (:list)";
		Map<String, Object> params = new HashMap();
		params.put("list", value);
		return namedJdbcTemplate.queryForList(sql, params, elementType);
	}

	public <E> List<E> findSingleFieldBySingleField(String returnField, String matchField, Object value,
													Class<E> elementType) {
		String sql = "SELECT DISTINCT " + returnField + " FROM " + this.getTableName() + " WHERE " + matchField +
				" = ?";
		return jdbcTemplate.queryForList(sql, new Object[]{value}, elementType);
	}

	public Long saveOrUpdate(T entity) {
		if (entity.getId() == null || entity.getId() == 0) {
			return this.insert(entity);
		} else {
			this.updateEntityAllByID(entity);
			return entity.getId();
		}
	}

	public int[] batchInsert(List<T> entityList) {
		if (entityList == null) {
			return new int[0];
		}

		SqlParameterSource[] sqlParameterSources = new SqlParameterSource[entityList.size()];

		for (int i = 0; i < sqlParameterSources.length; i++) {
			sqlParameterSources[i] = new AnnationBeanSqlParameterSource(entityList.get(i),objectMapper);
		}

		return insertTool.executeBatch(sqlParameterSources);
	}

}
