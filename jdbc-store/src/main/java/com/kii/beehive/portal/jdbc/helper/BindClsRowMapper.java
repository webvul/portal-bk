package com.kii.beehive.portal.jdbc.helper;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.jdbc.annotation.DisplayField;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class BindClsRowMapper<T> implements RowMapper<T> {

	private Logger log= LoggerFactory.getLogger(BindClsRowMapper.class);


	private final Map<String,String> fieldMapper;

	private final Map<String,JdbcFieldType> sqlTypeMapper;

	private final  ObjectMapper  objectMapper;
	
	private final BeanWrapper beanWrapper;

	private final Class<T> cls;
	
	public BindClsRowMapper(Class<T> cls,ObjectMapper objectMapper){

		this.cls=cls;

		this.beanWrapper=PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls));

		Map<String,String> searchMap=new HashMap<>();

		Map<String,JdbcFieldType> typeMap=new HashMap<>();

		for(PropertyDescriptor descriptor: beanWrapper.getPropertyDescriptors()){
			Method method = descriptor.getReadMethod();
			if(method.isAnnotationPresent(JdbcField.class)){
				JdbcField fieldDesc=method.getDeclaredAnnotation(JdbcField.class);
				searchMap.put(fieldDesc.column(),descriptor.getDisplayName());
				typeMap.put(fieldDesc.column(),fieldDesc.type());
			}else if(method.isAnnotationPresent(DisplayField.class)){
				DisplayField fieldDesc=method.getDeclaredAnnotation(DisplayField.class);
				searchMap.put(fieldDesc.column(),descriptor.getDisplayName());
				typeMap.put(fieldDesc.column(),fieldDesc.type());
			}
		}

		fieldMapper= Collections.unmodifiableMap(searchMap);

		sqlTypeMapper=Collections.unmodifiableMap(typeMap);

		this.objectMapper=objectMapper;
	}


	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {

//		T inst=BeanUtils.instantiate(cls);
		
		BeanWrapper  beanWrapperInst=PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls));

		for(String field:fieldMapper.keySet()){

			JdbcFieldType type=sqlTypeMapper.get(field);

			String propName=fieldMapper.get(field);

			Class propCls=beanWrapper.getPropertyDescriptor(propName).getPropertyType();

			Object fieldInst=null;
			switch (type){
				case Auto:
					fieldInst=autoConvert(rs,field,propCls);
					break;
				case EnumInt:
					int val=rs.getInt(field);
					fieldInst=propCls.getEnumConstants()[val];
					break;
				case EnumStr:
					String strVal=rs.getString(field);
					fieldInst=Enum.valueOf(propCls,strVal);
					break;
				case Json:

					String json=rs.getString(field);
					try {
						if(StringUtils.isBlank(json)){
							fieldInst=null;
							break;
						}
						Map<String,Object>  map = objectMapper.readValue(json,Map.class);
						fieldInst=map;
					} catch (IOException e) {
						log.error("get json field fail",e);
					}
					break;
				default:
					fieldInst=rs.getObject(field);

			}

			log.debug(" fill row target "+fieldInst+" to field "+field);
			
			beanWrapperInst.setPropertyValue(fieldMapper.get(field), fieldInst);
		}


		return (T)beanWrapperInst.getWrappedInstance();
	}

	private Object autoConvert(ResultSet rs,String key,Class target) throws SQLException {

		Object result=null;
		try{
			if(target.equals(Date.class)){
					java.sql.Date date=rs.getDate(key);
					if(date!=null) {
						result = new Date(date.getTime());
					}
			}else if(target.isPrimitive()){
				result=rs.getObject(key,target);
			}else if(target.equals(String.class)){
				
					result=rs.getString(key);
				
			}else if(target.equals(Boolean.class)){
				result=rs.getBoolean(key);
			}else if( Number.class.isAssignableFrom(target)){
				result=rs.getObject(key);
			}else{
				result=rs.getObject(key,target);
			}
		} catch (SQLException sqlex){
			result = null;
		}
		return result;
	}
	
	
	public  SqlParam getSqlParamInstance(String tableName){
		SqlParam param=new SqlParam();
		
		param.fullSql.append("select * from "+tableName+" where 1=1 ");
		return param;
	}
	
	public class SqlParam {
		
		private StringBuilder fullSql=new StringBuilder();
		
		private List<Object> list=new ArrayList<>();
		
		public void addEqCondition(String fieldName,Object val){
			if(val!=null){
				fullSql.append(" and ").append(getValueStr(val,beanWrapper.getPropertyType(fieldName),sqlTypeMapper.get(fieldName))).append(" = ? ");
				list.add(val);
			}
		}
		
		public void addPageEnd(int from){
			
			fullSql.append(" LIMIT ? , 50 ");
			list.add(from);
		}
		
		private String getValueStr(Object val,Class propCls,JdbcFieldType type){
			
			
			if(type == JdbcFieldType.EnumInt){
				
				return String.valueOf(((Enum)val).ordinal());
				
			}else if(propCls.equals(String.class)&&type==JdbcFieldType.Auto){
				
				return "'"+String.valueOf(val)+"'";
				
			}else if(type == JdbcFieldType.EnumStr){
				
				return ((Enum)val).name();
				
			}else {
				return String.valueOf(val);
			}
			
		}
		
		public String getFullSql(){
			return fullSql.toString();
		}
		
		public Object[] getParamArray(){
			return list.toArray();
		}
		
		
	}
}
