package com.kii.extension.ruleengine.drools.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.ruleengine.ExpressTool;

public class ThingStatusInRule implements RuntimeEntry,CanUpdate<ThingStatusInRule>{

	private Logger log= LoggerFactory.getLogger(ThingStatusInRule.class);

	private final String thingID;

	private Date createAt;

	private Map<String,Object> values=new HashMap<>();
	
	private Map<String,Object> previousValues=new HashMap<>();

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
	
	public Map<String,Object> getPrevious(){
		return previousValues;
	}

	public void setValues(Map<String, Object> values) {
		
		previousValues=new HashMap<>(this.values);
		this.values = new HashMap<>(values);
	}

//	public void addValue(String field,Object value){
//		this.values.put(field,value);
//	}



	public Object getNumValue(String field){

		return  ExpressTool.getNumValue(this,field);
	}

	public Object getValue(String field){

		Object value= ExpressTool.getObjValue(this,field);

		return value;
	}
	
//	public Object getPreviousNumValue(String field){
//
//		return  ExpressTool.getNumValue(this,field);
//	}
//
//	public String getPreviousValue(String field){
//
//		String value= ExpressTool.getValue(this,field,String.class);
//
//		return value;
//	}

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
			this.previousValues=new HashMap<>(values);
			
			this.values.putAll(vals);
		}

	}
}
