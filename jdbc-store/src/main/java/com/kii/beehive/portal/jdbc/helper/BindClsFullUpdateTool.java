package com.kii.beehive.portal.jdbc.helper;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;
import com.kii.beehive.portal.jdbc.entity.BusinessEntity;
import com.kii.beehive.portal.jdbc.entity.DBEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.sql.Types;
import java.util.*;


public class BindClsFullUpdateTool extends SqlUpdate {


	private Logger log = LoggerFactory.getLogger(BindClsFullUpdateTool.class);

	private static final String updateSqlTemplate = "update ${0} set  ${1} where ${2}  =  :${3} and is_deleted =  false ";

	private final String tableName;

	private final BeanWrapper wrapper;


	private final String pkFieldName;

	@Override
	protected boolean allowsUnusedParameters() {
		return true;
	}

	private BindClsFullUpdateTool(DataSource ds, String tableName, BeanWrapper wrapper, String pkName) {
		setDataSource(ds);
		this.tableName = tableName;
		this.wrapper = wrapper;
		this.pkFieldName = pkName;

	}

	private <T extends DBEntity> BindClsFullUpdateTool(DataSource ds, String tableName, Class<T> cls, String pkName) {
		this(ds, tableName, PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls)), pkName);
	}

	public static <E extends DBEntity> BindClsFullUpdateTool newInstance(DataSource ds, String tableName, String key, Class<E> cls, String pkName) {
		BindClsFullUpdateTool inst = new BindClsFullUpdateTool(ds, tableName, cls, pkName);
		inst.compileWithClass(key);
		return inst;
	}

	public BindClsFullUpdateTool cloneInstance(BusinessEntity entity, String key, boolean ignoreNull) {

		BindClsFullUpdateTool inst = new BindClsFullUpdateTool(super.getJdbcTemplate().getDataSource(), tableName, wrapper, pkFieldName);

		inst.compileWithEntity(entity, key, ignoreNull);
		return inst;
	}


	public BindClsFullUpdateTool cloneInstance(Map<String, Object> paramMap, String key) {

		BindClsFullUpdateTool inst = new BindClsFullUpdateTool(super.getJdbcTemplate().getDataSource(), tableName, wrapper, pkFieldName);

		inst.compileWithFieldSet(new HashSet<>(paramMap.keySet()), key);
		return inst;
	}


	private void compileWithClass(String key) {

		List<SqlParameter> paramList = new ArrayList<>();

		StringBuilder fields = new StringBuilder();

		int pkType = Types.BIGINT;
		String pkName = null;

		for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {

			JdbcField fieldDesc = descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			if (fieldDesc == null) {
				continue;
			}

			String column = fieldDesc.column();

			if (descriptor.getName().equals(key)) {
				//record pk's db field name &  type.
				if (fieldDesc.type() != JdbcFieldType.Auto) {
					pkType = fieldDesc.type().getSqlType();
				}
				pkName = column;
				continue;
			}

			if (column.equals(BusinessEntity.CREATE_DATE) || column.equals(BusinessEntity.CREATE_BY) || column.equals(pkFieldName)) {
				//these field never appear in  update field list
				continue;
			}

			SqlParameter param = new SqlParameter(descriptor.getName(), fieldDesc.type().getSqlType());
			paramList.add(param);

			fields.append(fieldDesc.column()).append(" =  ").append(":").append(descriptor.getName()).append(" ,");

		}

		//in default,always using pk as condition
		String fullSql = StrTemplate.gener(updateSqlTemplate, tableName, removeDot(fields), pkName, "id");
		super.setSql(fullSql);

		paramList.add(new SqlParameter(key, pkType));
		paramList.forEach(super::declareParameter);

		super.compile();

	}

	private String removeDot(StringBuilder fields) {

		int idx = fields.lastIndexOf(",");

		return fields.delete(idx, fields.length()).toString();

	}

	private void compileWithEntity(BusinessEntity entity, String key, boolean ignoreNull) {

		List<SqlParameter> paramList = new ArrayList<>();

		StringBuilder fields = new StringBuilder();

		BeanWrapper localWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

		JdbcField conditionField = null;
		for (PropertyDescriptor descriptor : localWrapper.getPropertyDescriptors()) {

			JdbcField fieldDesc = descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			if (fieldDesc == null) {
				continue;
			}

			Object val = localWrapper.getPropertyValue(descriptor.getName());
			if (val == null && ignoreNull && (!descriptor.getName().equals("modifyBy")) && (!descriptor.getName().equals("modifyDate"))) {
				continue;
			}
//				paramMap.put(descriptor.getName(),val);

			if (descriptor.getName().equals(key)) {
				conditionField = fieldDesc;
				continue;
			}

			String column = fieldDesc.column();
			if (column.equals(BusinessEntity.CREATE_DATE) || column.equals(BusinessEntity.CREATE_BY) || column.equals(pkFieldName)) {
				//these field never appear in  update field list
				continue;
			}

			SqlParameter param = new SqlParameter(descriptor.getName(), fieldDesc.type().getSqlType());
			paramList.add(param);
			fields.append(fieldDesc.column()).append(" =  ").append(":").append(descriptor.getName()).append(" ,");


			log.debug(" fill update param " + fieldDesc.column() + " with " + val);
		}

		if (conditionField == null) {

			throw new IllegalArgumentException("special condition field not exist or is null");
		}

		String fullSql = StrTemplate.gener(updateSqlTemplate, tableName, removeDot(fields), conditionField.column(), key);
		setSql(fullSql);

		paramList.add(new SqlParameter(key, conditionField.type().getSqlType()));
		paramList.forEach(this::declareParameter);

		compile();
	}

	private List<SqlParameter> compileWithFieldSet(Set<String> inputSet, String key) {

		inputSet.add("modifyBy");
		inputSet.add("modifyDate");

		List<SqlParameter> paramList = new ArrayList<>();

		StringBuilder fields = new StringBuilder();

		inputSet.remove(key);

		inputSet.forEach(fieldName -> {

			PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(fieldName);
			if (descriptor == null) {
				return;
			}

			JdbcField fieldDesc = descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);
			if (fieldDesc == null) {
				return;
			}

			String column = fieldDesc.column();
			if (column.equals(BusinessEntity.CREATE_DATE) ||
					column.equals(BusinessEntity.CREATE_BY) ||
					column.equals(pkFieldName) ||
					column.equals(BusinessEntity.IS_DELETED)) {
				//these field never appear in  update field list
				return;
			}

			SqlParameter param = new SqlParameter(descriptor.getName(), fieldDesc.type().getSqlType());
			paramList.add(param);
			fields.append(fieldDesc.column()).append(" =  ").append(":").append(descriptor.getName()).append(" ,");

			log.debug(" fill update param " + fieldDesc.column());
		});

		PropertyDescriptor pkDesc = wrapper.getPropertyDescriptor(key);
		JdbcField conditionField = pkDesc.getReadMethod().getDeclaredAnnotation(JdbcField.class);

		String fullSql = StrTemplate.gener(updateSqlTemplate, tableName, removeDot(fields), conditionField.column(), key);
		setSql(fullSql);

		paramList.add(new SqlParameter(key, conditionField.type().getSqlType()));
		paramList.forEach(this::declareParameter);

		compile();

		return paramList;
	}

	public <T extends BusinessEntity> int execute(Map<String, Object> paramMap) {

		fillParamMap(paramMap);

		return updateByNamedParam(paramMap);
	}

	public <T extends BusinessEntity> int execute(T entity) {

		BeanWrapper localWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

		Map<String, Object> paramMap = new HashMap<>();

		for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {

			JdbcField fieldDesc = descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			if (fieldDesc == null) {
				continue;
			}
			Object val = localWrapper.getPropertyValue(descriptor.getName());

			paramMap.put(descriptor.getName(), val);

			log.debug(" fill update param " + fieldDesc.column() + " with " + val);
		}

		fillParamMap(paramMap);

		return updateByNamedParam(paramMap);
	}

	public <T extends DBEntity> int executeSimple(T entity) {

		BeanWrapper localWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

		Map<String, Object> paramMap = new HashMap<>();

		for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {

			JdbcField fieldDesc = descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			if (fieldDesc == null) {
				continue;
			}
			Object val = localWrapper.getPropertyValue(descriptor.getName());

			paramMap.put(descriptor.getName(), val);

			log.debug(" fill update param " + fieldDesc.column() + " with " + val);
		}

		return updateByNamedParam(paramMap);
	}

	private void fillParamMap(Map<String, Object> paramMap) {
		paramMap.put("modifyBy", AuthInfoStore.getUserIDStr());
		paramMap.put("modifyDate", new Date());
	}

}