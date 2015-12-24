package com.kii.beehive.portal.jdbc.helper;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StreamUtils;

import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class BindClsRowMapper<T> implements RowMapper<T> {

	private Logger log= LoggerFactory.getLogger(BindClsRowMapper.class);


	private final Map<String,String> fieldMapper;

	private final Map<String,JdbcFieldType> sqlTypeMapper;

	private final BeanWrapper beanWrapper;


	private final Class<T> cls;
	public BindClsRowMapper(Class<T> cls){

		this.cls=cls;

		beanWrapper=PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls));

		Map<String,String> searchMap=new HashMap<>();

		Map<String,JdbcFieldType> typeMap=new HashMap<>();

		for(PropertyDescriptor descriptor: beanWrapper.getPropertyDescriptors()){

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
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {

//		T inst=BeanUtils.instantiate(cls);

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

					fieldInst=rs.getString(field);

					break;
				default:
					fieldInst=rs.getObject(field);

			}

			log.debug(" fill row result "+fieldInst+" to field "+field);

			beanWrapper.setPropertyValue(fieldMapper.get(field),fieldInst);

		}


		return (T)beanWrapper.getWrappedInstance();
	}

	private Object autoConvert(ResultSet rs,String key,Class target) throws SQLException {

		Object result=null;
		if(target.equals(Date.class)){
				java.sql.Date date=rs.getDate(key);
				if(date!=null) {
					result = new Date(date.getTime());
				}
		}else if(target.isPrimitive()){
			result=rs.getObject(key,target);
		}else if(target.equals(String.class)){
			result=rs.getString(key);
		}
		return result;
	}
}
