package com.kii.beehive.portal.jdbc.helper;

import static com.kii.beehive.portal.jdbc.helper.JdbcConvertTool.NUMBER_LEN;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.annotation.DisplayField;
import com.kii.beehive.portal.jdbc.annotation.JdbcField;
import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;

public class BindClsRowMapper<T> implements RowMapper<T> {

	private Logger log= LoggerFactory.getLogger(BindClsRowMapper.class);


	private final Map<String,String> fieldMapper;

	private final Map<String,JdbcFieldType> sqlTypeMapper;
	
	private final Map<String,String> propertyMap;

	
	private final BeanWrapper beanWrapper;

	private final Class<T> cls;
	
	public BindClsRowMapper(Class<T> cls){

		this.cls=cls;

		this.beanWrapper=PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls));

		Map<String,String> searchMap=new HashMap<>();

		Map<String,JdbcFieldType> typeMap=new HashMap<>();
		
		Map<String,String> propMap=new HashMap<>();
		for(PropertyDescriptor descriptor: beanWrapper.getPropertyDescriptors()){
			Method method = descriptor.getReadMethod();
			if(method.isAnnotationPresent(JdbcField.class)){
				JdbcField fieldDesc=method.getDeclaredAnnotation(JdbcField.class);
				searchMap.put(fieldDesc.column(),descriptor.getDisplayName());
				typeMap.put(fieldDesc.column(),fieldDesc.type());
				propMap.put(descriptor.getDisplayName(),fieldDesc.column());
				
			}else if(method.isAnnotationPresent(DisplayField.class)){
				DisplayField fieldDesc=method.getDeclaredAnnotation(DisplayField.class);
				searchMap.put(fieldDesc.column(),descriptor.getDisplayName());
				typeMap.put(fieldDesc.column(),fieldDesc.type());
			}
		}

		fieldMapper= Collections.unmodifiableMap(searchMap);

		sqlTypeMapper=Collections.unmodifiableMap(typeMap);

		propertyMap=Collections.unmodifiableMap(propMap);
		
	}


	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {

//		T inst=BeanUtils.instantiate(cls);
		
		BeanWrapper  beanWrapperInst=PropertyAccessorFactory.forBeanPropertyAccess(BeanUtils.instantiate(cls));

		for(String field:fieldMapper.keySet()){

			JdbcFieldType type=sqlTypeMapper.get(field);

			String propName=fieldMapper.get(field);

			Class propCls=beanWrapper.getPropertyDescriptor(propName).getPropertyType();

			Object fieldInst=JdbcConvertTool.getEntityValue(rs,field,propCls,type);
			
			log.debug(" fill row target "+fieldInst+" to field "+field);
			
			beanWrapperInst.setPropertyValue(fieldMapper.get(field), fieldInst);
		}


		return (T)beanWrapperInst.getWrappedInstance();
	}

	
	
	
	public  SqlParam getSqlParamInstance(String tableName){
		SqlParam param=new SqlParam();
		
		param.fullSql.append("select * from "+tableName+" where 1=1 ");
		return param;
	}
	
	
	public static class Pager{
		
		private static Pattern pagerPat=Pattern.compile("^((\\d+)\\/)?(\\d+)$");
		
		
		private int start=0;
		
		private int size=0;
		
		
		public static Pager getInstance(String sign){
			
			if(StringUtils.isBlank(sign)){
				return null;
			}
			
			Matcher matcher=pagerPat.matcher(sign);
			
			if(matcher.find()){
			
				Pager pager=new Pager();
				String a=matcher.group(2);
				String b=matcher.group(3);
				
				pager.size=Integer.parseInt(b);
				
				if(StringUtils.isNotBlank(a)){
					pager.start=Integer.parseInt(a);
				}
				
				return pager;
			}else{
				return null;
			}
		}
		
		public int getStart() {
			return start;
		}
		
		public void setStart(int start) {
			this.start = start;
		}
		
		public int getSize() {
			return size;
		}
		
		public void setSize(int size) {
			this.size = size;
		}
	}
	
	private static String REG_LIKE_TMP="\\^str${0}:([^\\^]*)\\${1}([^\\^]*)\\^";
	private static String REG_EQ_TMP="\\^str${0}:${1}\\^";
	
	private static String NUM_TMP="substring(${0},${1},${2})";
	
	
	public static final String ADDITIION_STRING="addition_strprop";
	
	public static final String ADDITIION_INT="addition_intprop";
	
	public class SqlParam {
		
		private StringBuilder fullSql=new StringBuilder();
		
		private List<Object> list=new ArrayList<>();
		
		public void addIsNull(String fieldName){
			
			String field = getFieldStr(fieldName);
			fullSql.append(" and ").append(field).append(" is null ");
			
			
		}
		
		public void addStrCustom(SqlCondition query){
			
			if(query.getExpress()== SqlCondition.SqlExpress.Eq){
				addStrCustomEq(ADDITIION_STRING,query.getAdditionIdx(),String.valueOf(query.getValue()));
			}else{
				addStrCustomLike(ADDITIION_STRING,query.getAdditionIdx(),String.valueOf(query.getValue()));
			}
			
		}

		
		private  void addStrCustomLike(String field,int idx,String value){
			
			String fullRegExp= StrTemplate.gener(REG_LIKE_TMP,String.valueOf(idx),value);
			
			list.add(fullRegExp);
			
			fullSql.append(" and ").append(field).append(" REGEXP ? ");
		}
		
		private  void addStrCustomEq(String field,int idx,String value){
			
			String fullRegExp= StrTemplate.gener(REG_EQ_TMP,String.valueOf(idx),value);
			
			list.add(fullRegExp);
			
			fullSql.append(" and ").append(field).append(" REGEXP ? ");
		}
		
		
		
		
		public void addIntCustom(SqlCondition query){
			
			if(query.getExpress()== SqlCondition.SqlExpress.Eq){
				addNumCustomEq(ADDITIION_INT,query.getAdditionIdx(),(Integer)query.getValue());
			}else{
				addNumCustomRange(ADDITIION_INT,query.getAdditionIdx(),(Integer)query.getStart(),(Integer)query.getEnd());
			}
			
			
		}
		
		private void addNumCustomEq(String field,int idx,Integer value) {
			String fullField = generCustomNumField(field, idx);
			addEq(fullField,value);
		}
			
		private void addNumCustomRange(String field,int idx,Integer start,Integer end){
			
			String fullField = generCustomNumField(field, idx);
			
			addBetween(fullField,start,end);
		}
		
		private String generCustomNumField(String field, int idx) {
			int begin=idx*(NUMBER_LEN+1)+1;
			int offset=NUMBER_LEN;
			
			return StrTemplate.gener(NUM_TMP,field,String.valueOf(begin),String.valueOf(offset));
		}
		
		public void addEq(String fieldName,Object val){
			
			if(val==null) {
				return;
			}
			String field = getFieldStr(fieldName);
			fullSql.append(" and ").append(field).append(" = ? ");
			list.add(getValue(val, fieldName));
			
		}
		
		public void addLike(String fieldName,String val){
			if(val==null){
				return;
			}
			String field=getFieldStr(fieldName);
			
			fullSql.append(" and ").append(field).append(" like ? ");
			list.add("%"+val+"%");
			
		}
		
		public <T> void addBetween(String fieldName, T start, T end) {
			
			if(start==null&&end==null){
				return;
			}
			
			String field=getFieldStr(fieldName);
			
			
			if(start!=null&&end!=null){
				fullSql.append(" and ").append(field).append(" between ? and ? ");
				list.add(getValue(start,fieldName));
				list.add(getValue(end,fieldName));
				return;
			}
			
			if(start!=null){
				fullSql.append(" and ").append(field).append(" > ? ");
				list.add(getValue(start,fieldName));
			}else{
				fullSql.append(" and ").append(field).append(" < ? ");
				list.add(getValue(end,fieldName));
			}
			return;
		}
		
		
		
		public void addPager(Pager page){
			
			if(page!=null) {
				
				fullSql.append(" LIMIT ? , ? ");
				list.add(page.getStart());
				list.add(page.getSize());
			}
		}
		
		private String getFieldStr(String field){
			
			return propertyMap.get(field);
			
		}
		
		private Object getValue(Object val,String field){
			String fieldName=propertyMap.get(field);
			Class propCls=beanWrapper.getPropertyType(field);
			JdbcFieldType type=sqlTypeMapper.get(fieldName);
			
			switch (type){
				
				case EnumInt:return ((Enum)val).ordinal();
				case Auto:return  String.valueOf(val);
				case EnumStr:return ((Enum)val).name();
				default:return val;
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
