package com.kii.beehive.portal.jdbc.helper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.jdbc.annotation.JdbcFieldType;
import com.kii.extension.tools.AdditionFieldType;

public class JdbcConvertTool {
	
	private static Logger log= LoggerFactory.getLogger(JdbcConvertTool.class);
	
	
	private  static ObjectMapper mapper=new ObjectMapper();
	
	private static  String MASK= StringUtils.repeat(" ",20);
	
	public static final int NUMBER_LEN=12;
	
//	private static Object getValueByRS(ResultSet rs, String field,Class  propCls,JdbcFieldType type)throws SQLException{
//
//
//		switch (type){
//			case Auto:
//				return autoConvert(rs,field,propCls);
//			case EnumInt:
//				return rs.getInt(field);
//			case EnumStr:
//				return rs.getString(field);
//			case Json:
//
//				return rs.getString(field);
//
//			case AdditionStr:
//				return rs.getString(field);
//			case AdditionInt:
//				return rs.getString(field);
//			default:
//				return rs.getObject(field);
//		}
//	}

	
	public static Object getEntityValue(ResultSet rs, String field,Class  propCls,JdbcFieldType type) throws SQLException {
		
		
		switch (type){
			case Auto:
				return autoConvert(rs,field,propCls);
			case EnumInt:
				int val=rs.getInt(field);
				return propCls.getEnumConstants()[val];
			case EnumStr:
				String strVal=rs.getString(field);
				return Enum.valueOf(propCls,strVal);
			case Json:
				
				String json=rs.getString(field);
				try {
					if(StringUtils.isBlank(json)){
						return null;
					}
					Map<String,Object> map = mapper.readValue(json,Map.class);
					return map;
				} catch (IOException e) {
					log.error("get json field fail",e);
					return null;
				}
				
			case AdditionStr:
				String addStr=rs.getString(field);
				Map<Integer,String> list=new HashMap<>();
				if(StringUtils.isBlank(addStr)){
					return list;
				}
				String[] array=StringUtils.split(String.valueOf(addStr),"^");
				
				for(String str:array){
					String valStr=StringUtils.substringAfter(str,":");
					int idx= AdditionFieldType.getIndex(str);
					list.put(idx,valStr);
				}
				return list;
			case AdditionInt:
				String addInt=rs.getString(field);
				Map<Integer,Integer>  intList=new HashMap<>();
				
				if(StringUtils.isBlank(addInt)){
					return intList;
				}
				String[] intArray=StringUtils.split(String.valueOf(addInt),"#");
				
				for(int i=0;i<intArray.length;i++){
					String intStr=StringUtils.trim(intArray[i]);
					if(StringUtils.isNotBlank(intStr)) {
						intList.put(i, Integer.parseInt(intStr));
					}
				}
				return intList;
			default:
				return rs.getObject(field);
		}
		
	}
	
	private static Object autoConvert(ResultSet rs, String key, Class target) throws SQLException {
		
		Object result=null;
		try{
			if(target.equals(Date.class)){
				java.sql.Timestamp date=rs.getTimestamp(key);
				if(date!=null) {
					result =  new Date(date.getTime());
				}
			}else if(target.isPrimitive()){
				result=rs.getObject(key,target);
			}else if(target.equals(String.class)){
				
				result= rs.getString(key);
				
			}else if(target.equals(Boolean.class)){
				result=  new Boolean(rs.getBoolean(key));
			}else if( Number.class.isAssignableFrom(target)){
				result=  rs.getObject(key);
			}else{
				result=rs.getObject(key,target);
			}
		} catch (SQLException sqlex){
			result = null;
		}
		return result;
	}
	
	public static Object getSqlValue(Object value,JdbcFieldType type) {
		
		switch(type){
			case Auto:return value;
			case Json:
				if(value instanceof String){
					return value;
				}
				try {
					return mapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					log.error("json write fail",e);
					return "{}";
				}
			case EnumInt:return ((Enum)value).ordinal();
			case EnumStr:return ((Enum)value).name();
			case AdditionStr:
				return getCombineStr((Map<Integer,String>) value);
			case AdditionInt:
				return getCombineInt((Map<Integer,Integer>) value);
			default:return value;
		}
	}
	
	private static Object getCombineInt(Map<Integer,Integer> value) {
		StringBuilder sbi=new StringBuilder();
		
		for(int i=0;i<10;i++){
			Integer intVal=value.get(i);
			if(intVal==null){
				intVal=0;
			}
			String strVal=String.valueOf(intVal);
			String fullStrVal= StringUtils.substring(MASK+strVal, -NUMBER_LEN);
			
			sbi.append(fullStrVal).append("#");
		}
		return  sbi.toString();
	}
	
	private static Object getCombineStr(Map<Integer,String> value) {
		if(value.isEmpty()){
			return "";
		}
		StringBuilder sb=new StringBuilder("^");
		
		value.forEach((k,v)->{
			sb.append(AdditionFieldType.Str.name()).append(k).append(":").append(v).append("^");
		});
	
		return sb.toString();
	}
	
}
