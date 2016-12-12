package com.kii.beehive.business.entity;

import org.springframework.beans.BeanUtils;

import com.kii.beehive.portal.jdbc.helper.SqlCondition;
import com.kii.extension.sdk.query.ConditionBuilder;

public class FieldQuery {
	
	
	
	
	private String fieldName;
	
	private Object value;
	
	private Number start;
	
	private Number end;
	
	private ConditionExpress express;
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		
		this.fieldName = fieldName;
		
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Number getStart() {
		return start;
	}
	
	public void setStart(Number start) {
		this.start = start;
	}
	
	public Number getEnd() {
		return end;
	}
	
	public void setEnd(Number end) {
		this.end = end;
	}
	
	public ConditionExpress getExpress() {
		return express;
	}
	
	public void setExpress(ConditionExpress express) {
		this.express = express;
	}
	
	public SqlCondition getSqlQuery() {
		
		SqlCondition sqlQuery=new SqlCondition();
		
		BeanUtils.copyProperties(this,sqlQuery,"express");
		sqlQuery.setExpress(SqlCondition.SqlExpress.valueOf(express.name()));
		sqlQuery.setFieldName(fieldName);
		
		return sqlQuery;
	}
	
	public ConditionBuilder getKiiCondition() {
		
		ConditionBuilder builder =ConditionBuilder.newCondition();
		
		String fullFieldName="additions."+fieldName;
		switch (express){
			
			case Eq:
				builder.equal(fullFieldName,value);
				break;
			case Like:
				builder.prefixLike(fullFieldName,String.valueOf(value));
				break;
			case Range:
				builder.betweenIn(fullFieldName,start,true,end,true);
				break;
		}
		return builder;
	}
	
	public enum ConditionExpress {
		
		Eq,Like,Range;
		
	}
	

}
