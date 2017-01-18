package com.kii.beehive.portal.jdbc.dao;

import javax.sql.DataSource;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.entity.BusinessEntity;
import com.kii.beehive.portal.jdbc.helper.AnnationBeanSqlParameterSource;
import com.kii.beehive.portal.jdbc.helper.BindClsFullUpdateTool;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

public abstract class SpringBaseDao<T extends BusinessEntity> {


	protected JdbcTemplate jdbcTemplate;

	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	private Logger log = LoggerFactory.getLogger(SpringBaseDao.class);

	private SimpleJdbcInsert insertTool;

	private RowMapper<T> rowMapper;

	private BindClsFullUpdateTool updateTool;

	private Class<T> entityClass;

	private BeanWrapper beanWrapper;



	protected abstract String getTableName();

	protected abstract String getKey();

	@Autowired
	public void setDataSource(DataSource dataSource) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.insertTool = new SimpleJdbcInsert(dataSource)
				.withTableName(getTableName())
				.usingGeneratedKeyColumns(getKey());

		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) type.getActualTypeArguments()[0];
		this.updateTool = BindClsFullUpdateTool.newInstance(dataSource, getTableName(), "id", entityClass, getKey());
		this.rowMapper = new BindClsRowMapper<T>(entityClass);

		this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(entityClass));
	}


	protected RowMapper<T> getRowMapper() {
		return rowMapper;
	}


	protected List<T> query(String sql, Object... queryParams) {

		sql = addDelSignPrefix(sql);
		return jdbcTemplate.query(sql, queryParams, getRowMapper());

	}
//
//	protected  List<T> queryForList(String sql, Object... queryParams) {
//
//		sql = addDelSignPrefix(sql);
//		return jdbcTemplate.queryForList(sql, queryParams, entityClass);
//
//	}

	protected StringBuilder addDelSignPrefix(StringBuilder sb) {

		return new StringBuilder(addDelSignPrefix(sb.toString()));
	}

	private static Pattern wherePtn = Pattern.compile("\\swhere\\s", Pattern.CASE_INSENSITIVE);
	private static Pattern fromPtn = Pattern.compile("select\\s?(?:distinct)?\\s+(\\w+\\.)(\\*|\\w+)\\s+from", Pattern.CASE_INSENSITIVE);

	protected String addDelSignPrefix(String sql) {

		Matcher seleMatcher = fromPtn.matcher(sql);
		String selPrefix = "";
		if (seleMatcher.find()) {
			selPrefix = seleMatcher.group(1);
		}

		String subSeq = selPrefix + "is_deleted = false ";

		Matcher matcher = wherePtn.matcher(sql);
		if (!matcher.find()) {
			sql += " where " + subSeq;

		} else {
			int idx = matcher.end();

			String prefix = sql.substring(0, idx);
			String subfix = sql.substring(idx, sql.length());

			sql = prefix + subSeq + " and " + subfix;

		}
		return sql;
	}

	;


	protected List<T> queryByNamedParamNotAddDelSignPrefix(String sql, Map<String, Object> queryParams) {
		return namedJdbcTemplate.query(sql, queryParams, getRowMapper());
	}

	protected List<T> queryByNamedParam(String sql, Map<String, Object> queryParams) {
		sql = addDelSignPrefix(sql);
		return namedJdbcTemplate.query(sql, queryParams, getRowMapper());

	}

	;

	protected <E> List<E> queryForListByNamedParam(String sql, Class<E> clsType, Map<String, Object> queryParams) {
		sql = addDelSignPrefix(sql);
		return namedJdbcTemplate.queryForList(sql, queryParams, clsType);

	}

	;

	protected T queryForObject(String sql, Object... queryParams) {
		sql = addDelSignPrefix(sql);
		try {
			return jdbcTemplate.queryForObject(sql, queryParams, getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	;

	protected T queryForObjectByNamedParam(String sql, Map<String, Object> queryParams) {
		sql = addDelSignPrefix(sql);
		try {
			return namedJdbcTemplate.queryForObject(sql, queryParams, getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	;

	public <T extends BusinessEntity> long insert(T entity) {

		if (entity.getCreateBy() == null) {
			entity.setCreateBy(AuthInfoStore.getUserIDStr());
		}
		entity.setCreateDate(new Date());
		entity.setModifyBy(AuthInfoStore.getUserIDStr());
		entity.setModifyDate(new Date());
		entity.setDeleted(false);
		SqlParameterSource parameters = new AnnationBeanSqlParameterSource(entity);
		Number id = insertTool.executeAndReturnKey(parameters);
		return id.longValue();
	}

	public List<T> findAll() {
		String sqlTmp = "SELECT * FROM ${0} where ${1} = false ";

		String sql = StrTemplate.gener(sqlTmp, getTableName(), BusinessEntity.IS_DELETED);

		return (List<T>) jdbcTemplate.query(sql, getRowMapper());
	}

	public T findByID(Serializable id) {
		String sqlTmp = "SELECT t.* FROM ${0}  t WHERE t.${1} =?  ";

		String sql = StrTemplate.gener(sqlTmp, getTableName(), getKey(), BusinessEntity.IS_DELETED);

		List<T> rows = jdbcTemplate.query(sql, new Object[]{id}, getRowMapper());
		if (rows.size() > 0) {
			return rows.get(0);
		} else {
			return null;
		}
	}

	protected final String SQL_FIND_BY_IDS_TMP = "SELECT t.${0} FROM ${1}  t WHERE t.${0}  IN (:list) ";

	public Set<Long> checkIdList(Collection<Long> ids) {

		if (null == ids || ids.isEmpty()) {
			return Collections.emptySet();
		}

		String sql = StrTemplate.gener(SQL_FIND_BY_IDS_TMP, this.getKey(), this.getTableName());

		Set<Long> set = new HashSet<>(namedJdbcTemplate.queryForList(addDelSignPrefix(sql), Collections.singletonMap("list", ids), Long.class));

		Set<Long> oldSet = new HashSet(ids);
		oldSet.removeAll(set);

		return oldSet;
	}

	protected final String SQL_FIND_BY_IDS = "SELECT t.* FROM " + this.getTableName() + " t WHERE t." +
			this.getKey() + " IN (:list) ";


	public List<T> findByIDs(Collection<Long> ids) {
		if (null == ids || ids.isEmpty()) {
			return Collections.emptyList();
		}
		Map<String, Collection> param = Collections.singletonMap("list", ids);

		return namedJdbcTemplate.query(this.addDelSignPrefix(SQL_FIND_BY_IDS), param, getRowMapper());
	}


	public List<T> findByFields(Map<String, Object> queryParam) {

		String sql = "SELECT t.* FROM " + this.getTableName() + " t WHERE t.is_deleted = false  ";


		List<Object> params = new ArrayList<>();

		for (Map.Entry<String, Object> entry : queryParam.entrySet()) {

			String field = entry.getKey();

			JdbcField jdbc = beanWrapper.getPropertyDescriptor(field).getReadMethod().getAnnotation(JdbcField.class);

			Object o = entry.getValue();

			sql += " and  t." + jdbc.column() + " = ? ";
			params.add(o);

		}

		return jdbcTemplate.query(sql, params.toArray(new Object[0]), getRowMapper());

	}

	public List<T> findBySingleField(String fieldName, Object value) {
		String sql = "SELECT DISTINCT * FROM " + this.getTableName() + " WHERE " + fieldName + "=? and is_deleted = " +
				"false  ";
		return jdbcTemplate.query(sql, new Object[]{value}, getRowMapper());
	}

	public <T> List<T> findSingleFieldBySingleField(String returnField, String matchField, Object value,
													Class<T> elementType) {
		String sql = "SELECT DISTINCT " + returnField + " FROM " + this.getTableName() + " WHERE " + matchField +
				" = ? and is_deleted = false  ";
		return jdbcTemplate.queryForList(sql, new Object[]{value}, elementType);
	}


	public int deleteByID(Long id) {
		String sql = "update " + this.getTableName() + " set  is_deleted = true where " + getKey() + "=?";
		return jdbcTemplate.update(sql, id);
	}


	public int hardDeleteByID(Long id) {

		String sql = "delete from " + this.getTableName() + "  where " + getKey() + " =? ";
		return jdbcTemplate.update(sql, id);

	}

	public int updateEntityAllByID(T entity) {

		return updateTool.execute(entity);
	}


	public int updateEntityByID(Map<String, Object> paramMap, long id) {

		paramMap.put("id", id);
		BindClsFullUpdateTool tool = updateTool.cloneInstance(paramMap, "id");

		return tool.execute(paramMap);
	}

	public int updateFieldByID(String field,Object obj, long id) {

		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put(field,obj);

		return updateEntityByID(paramMap,id);

	}



	public int updateEntityByID(T entity, long id) {

		entity.setId(id);

		BindClsFullUpdateTool tool = updateTool.cloneInstance(entity, "id", true);

		return tool.execute(entity);
	}

	public int updateEntityByField(Map<String,Object> paramMap, String conditionField) {

		paramMap.put(conditionField, paramMap.get(conditionField));
		BindClsFullUpdateTool tool = updateTool.cloneInstance(paramMap, conditionField);

		return tool.execute(paramMap);
	}

	public int updateEntityByField(T entity, String conditionField) {

		BindClsFullUpdateTool tool = updateTool.cloneInstance(entity, conditionField, true);

		return tool.execute(entity);
	}

	public int updateEntityAllByField(T entity, String conditionField) {

		BindClsFullUpdateTool tool = updateTool.cloneInstance(entity, conditionField, false);

		return tool.execute(entity);
	}

	private int doUpdate(String updateSql, Object... params) {

		int start = updateSql.indexOf("set") + 3;

		int end = updateSql.indexOf("where");

		String header = updateSql.substring(0, start);
		String tail = updateSql.substring(end);

		String updates = updateSql.substring(start + 1, end);

		String newUpdates = " modify_by = ? , modify_date= ? , " + updates;

		String newUpdateSql = header + newUpdates + tail + " and is_deleted = false";

		Object[] newParams = new Object[params.length + 2];

		newParams[0] = AuthInfoStore.getUserIDStr();
		newParams[1] = new Date();
		System.arraycopy(params, 0, newParams, 2, params.length);

		return jdbcTemplate.update(newUpdateSql, newParams);

	}

	public Long saveOrUpdate(T entity) {
		if (entity.getId() == null || entity.getId() == 0) {
			return this.insert(entity);
		} else {
			this.updateEntityAllByID(entity);
			return entity.getId();
		}
	}

	public boolean IsIdExist(Serializable id) {
		boolean result = false;
		String sql = "SELECT count(1) FROM " + this.getTableName() + " WHERE " + getKey() + "=? and is_deleted = false  ";
		Long count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Long.class);
		if (count > 0) {
			result = true;
		}

		return result;
	}

	public int[] batchInsert(List<T> entityList) {
		if (entityList == null) {
			return new int[0];
		}

		SqlParameterSource[] sqlParameterSources = new SqlParameterSource[entityList.size()];

		for (int i = 0; i < sqlParameterSources.length; i++) {
			sqlParameterSources[i] = new AnnationBeanSqlParameterSource(entityList.get(i));
		}

		return insertTool.executeBatch(sqlParameterSources);
	}

	public List<T> queryWithPage(String sql, Object[] params, PagerTag pager) {

		sql = this.addDelSignPrefix(sql);

		String fullSql = sql + "  limit ?,? ";

		Object[] newParams = new Object[params.length + 2];
		System.arraycopy(params, 0, newParams, 0, params.length);
		newParams[params.length] = pager.getStartRow();
		newParams[params.length + 1] = pager.getPageSize();


		List<T> list = jdbcTemplate.query(fullSql, newParams, getRowMapper());

		pager.addStartRow(list.size());

		if (list.size() < pager.getPageSize()) {
			pager.setHasNext(false);
			return list;
		}

		return list;
	}

	public List<T> queryWithPage(String sql, Map<String, Object> params, PagerTag pager) {

		sql = this.addDelSignPrefix(sql);

		String fullSql = sql + "  limit :startRow,:pageSize ";

		params.put("startRow", pager.getStartRow());
		params.put("pageSize", pager.getPageSize());

		fullSql = this.addDelSignPrefix(fullSql);
		List<T> list = namedJdbcTemplate.query(fullSql, params, getRowMapper());

		pager.addStartRow(list.size());

		if (list.size() < pager.getPageSize()) {
			pager.setHasNext(false);
			return list;
		}

		return list;
	}


	private static String ExistSql="select 1  from ${0} t where t.${1} =  ? ";
	public void existEntity(Long id) {

		String fullSql=StrTemplate.gener(ExistSql,this.getTableName(),this.getKey());

		fullSql=this.addDelSignPrefix(fullSql);


		this.jdbcTemplate.queryForObject(fullSql, new Object[]{id}, Long.class);

	}
}
