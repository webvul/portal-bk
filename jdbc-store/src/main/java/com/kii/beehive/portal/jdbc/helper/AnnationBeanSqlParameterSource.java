package com.kii.beehive.portal.jdbc.helper;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class AnnationBeanSqlParameterSource extends AbstractSqlParameterSource {


	private Logger log= LoggerFactory.getLogger(AnnationBeanSqlParameterSource.class);

	private final BeanWrapper beanWrapper;

	private final Map<String,String> fieldMapper;

	private final Map<String,JdbcFieldType> sqlTypeMapper;


	public AnnationBeanSqlParameterSource(Object object) {
		this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);

		Map<String,String> searchMap=new HashMap<>();

		Map<String,JdbcFieldType> typeMap=new HashMap<>();

		for(PropertyDescriptor  descriptor:beanWrapper.getPropertyDescriptors()){

			JdbcField fieldDesc=descriptor.getReadMethod().getDeclaredAnnotation(JdbcField.class);

			if(fieldDesc==null){
				continue;
			}
			searchMap.put(fieldDesc.column(),descriptor.getDisplayName());

			typeMap.put(fieldDesc.column(),fieldDesc.type());
		}

		fieldMapper= Collections.unmodifiableMap(searchMap);

		sqlTypeMapper=Collections.unmodifiableMap(typeMap);

	}


	@Override
	public boolean hasValue(String paramName) {

		String fieldName=fieldMapper.get(paramName);
		if(fieldName==null){
			return false;
		}
		return this.beanWrapper.isReadableProperty(fieldName);
	}

	@Override
	public Object getValue(String paramName)  {
		try {
			String fieldName=fieldMapper.get(paramName);


			Object value= this.beanWrapper.getPropertyValue(fieldName);


			log.debug(" bind param value:"+value+" to "+paramName);

			if(value==null){
				return null;
			}
			
			return JdbcConvertTool.getSqlValue(value,sqlTypeMapper.get(paramName));
			
//			switch(sqlTypeMapper.get(paramName)){
//				case Auto:return value;
//				case Json:
//					try {
//						return mapper.writeValueAsString(value);
//					} catch (JsonProcessingException e) {
//						log.error("json write fail",e);
//						return "{}";
//					}
//				case EnumInt:return ((Enum)value).ordinal();
//				case EnumStr:return ((Enum)value).name();
//				case AdditionStr:
//					StringBuilder sb=new StringBuilder("^");
//					List<String> col=(List<String>)value;
//
//					for(int i=0;i<col.size();i++){
//						sb.append("str").append(i).append(":").append(col.get(i)).append("^");
//					}
//					return sb.toString();
//				case AdditionInt:
//					StringBuilder sbi=new StringBuilder();
//
//					List<Integer> col=(List<Integer>)value;
//					for(int i=0;i<col.size();i++){
//						Integer intVal=col.get(i);
//						String strVal=String.valueOf(intVal);
//						String fullStrVal= StringUtils.substring(MASK+strVal,-10);
//
//						sbi.append(fullStrVal).append(".");
//					}
//					return  sbi.toString();
//				default:return value;
//			}


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
		String fieldName=this.fieldMapper.get(paramName);
		Class<?> propType = this.beanWrapper.getPropertyType(fieldName);
		sqlType= StatementCreatorUtils.javaTypeToSqlParameterType(propType);

		if(sqlType!=TYPE_UNKNOWN){
			return sqlType;
		}

		JdbcFieldType type=sqlTypeMapper.get(paramName);

		return type.getSqlType();

	}



}
