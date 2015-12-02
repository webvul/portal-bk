package com.kii.beehive.portal.jdbc.helper;

import java.beans.PropertyDescriptor;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class AnnationBeanSqlParameterSource extends AbstractSqlParameterSource {



	private final BeanWrapper beanWrapper;

	private final Map<String,String> fieldMapper;

	private final Map<String,JdbcFieldType> sqlTypeMapper;


	public AnnationBeanSqlParameterSource(Object object) {
		this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);

		Map<String,String> searchMap=new HashMap<>();

		Map<String,JdbcFieldType> typeMap=new HashMap<>();

		for(PropertyDescriptor  descriptor:beanWrapper.getPropertyDescriptors()){

			JdbcField fieldDesc=descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			searchMap.put(fieldDesc.column(),descriptor.getDisplayName());

			typeMap.put(fieldDesc.column(),fieldDesc.type());
		}

		fieldMapper= Collections.unmodifiableMap(searchMap);

		sqlTypeMapper=Collections.unmodifiableMap(typeMap);
	}


	@Override
	public boolean hasValue(String paramName) {

		String fieldName=fieldMapper.get(paramName);
		return this.beanWrapper.isReadableProperty(fieldName);
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		try {
			String fieldName=fieldMapper.get(paramName);


			Object value= this.beanWrapper.getPropertyValue(fieldName);

			if(value==null){
				return null;
			}

			switch(sqlTypeMapper.get(paramName)){
				case Auto:return value;
				case Json:return ((String)value).getBytes(StandardCharsets.UTF_8);
				case EnumInt:return ((Enum)value).ordinal();
				case EnumStr:return ((Enum)value).name();
				default:return value;
			}

		}
		catch (NotReadablePropertyException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
	}


	@Override
	public int getSqlType(String paramName) {

		int sqlType = super.getSqlType(paramName);
		if (sqlType != TYPE_UNKNOWN) {
			return sqlType;
		}
		Class<?> propType = this.beanWrapper.getPropertyType(paramName);
		sqlType= StatementCreatorUtils.javaTypeToSqlParameterType(propType);

		if(sqlType!=TYPE_UNKNOWN){
			return sqlType;
		}

		JdbcFieldType type=sqlTypeMapper.get(paramName);


		switch(type){
			case Json:
				return Types.BLOB;
			case EnumInt:
				return Types.INTEGER;
			case EnumStr:
				return Types.VARCHAR;
			default:
				return TYPE_UNKNOWN;
		}


	}



}
