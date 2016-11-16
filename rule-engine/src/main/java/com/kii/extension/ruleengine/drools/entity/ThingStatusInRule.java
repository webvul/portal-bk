package com.kii.extension.ruleengine.drools.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.ExpressTool;

public class ThingStatusInRule implements RuntimeEntry,CanUpdate<ThingStatusInRule>{

	private Logger log= LoggerFactory.getLogger(ThingStatusInRule.class);

	private final String thingID;

	private Date createAt;

	private Map<String,Object> values=new HashMap<>();

	public ThingStatusInRule(String thingID){
		this.thingID=thingID;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public void addValue(String field,Object value){
		this.values.put(field,value);
	}


	private String  getFullFieldPath(String field){

		int idx=field.indexOf(".");
		if(idx==-1){
			idx=field.length();
		}
		String prefix= StringUtils.substring(field,0,idx);
		String fullField="values['"+prefix+"']"+StringUtils.substring(field,idx);

		log.debug("fullField:"+fullField);
		return fullField;
	}

	public Object getNumValue(String field){


		Object value= ExpressTool.getValue(this,getFullFieldPath(field));
		return value == null ? 0 : value;
	}

	public Object getValue(String field){

		Object value= ExpressTool.getValue(this,getFullFieldPath(field));

//		Object value = this.values.get(field);
		if(value==null){
			return null;
		}
		return value;
	}

	public String getThingID() {
		return thingID;
	}


	@Override
	public String toString() {
		return "ThingStatus{" +
				"thingID='" + thingID + '\'' +
				", values=" + values +
				'}';
	}

	@Override
	public String getID() {
		return thingID;
	}

	@Override
	public void update(ThingStatusInRule update) {

		Map<String,Object>  vals=update.getValues();
		if(vals!=null){
			this.values.putAll(vals);
		}

	}
}
