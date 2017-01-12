package com.kii.extension.ruleengine.drools.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.ExpressTool;

public class BusinessObjInRule implements RuntimeEntry,CanUpdate<BusinessObjInRule>{

	private Logger log= LoggerFactory.getLogger(BusinessObjInRule.class);

	private final String thingID;

	private Date createAt;

	private Map<String,Object> values=new HashMap<>();
	
	private Map<String,Object> previousValues=new HashMap<>();

	public BusinessObjInRule(String thingID){
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
	
	public Map<String,Object> getPrevious(){
		return previousValues;
	}

	public void setValues(Map<String, Object> values) {
		
		previousValues=new HashMap<>(this.values);
		this.values = new HashMap<>(values);
	}
	
	
	private Object getValueByKey(String key){
		
		if(key.startsWith("previous")){
			String testKey= StringUtils.substring(key,9);
			return previousValues.get(testKey);
		}else{
			return values.get(key);
		}
	}
	
	
	public Object getNumValue(String field){

		Object obj=getValueByKey(field);
		if(obj!=null){
			return obj;
		}
		return  ExpressTool.getNumValue(this,field);
	}

	public Object getValue(String field){
		
		Object obj=getValueByKey(field);
		if(obj!=null){
			return obj;
		}

		Object value= ExpressTool.getObjValue(this,field);

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
	public void update(BusinessObjInRule update) {

		Map<String,Object>  vals=update.getValues();
		
		if(vals!=null){
			this.previousValues=new HashMap<>(values);
			
			this.values.putAll(vals);
		}

	}
}
