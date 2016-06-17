package com.kii.extension.ruleengine.drools.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;

public class MultiplesValueMap {

	private String triggerID;

	private Map<String,Object> valueMap=new HashMap<>();

	public void setSummaryValue(SummaryResult result){

		valueMap.put(result.getSummaryField(),result.getValue());
	}


	public void setFieldValueSet(String name, Set<String> fieldSet, Map<String,Object> values){

		fieldSet.forEach((field)->{
			String fullName=name+"."+field;
			valueMap.put(fullName,values.get(field));
		});

	}

	public void setUnitValue(String  name,Object value){
		valueMap.put(name,value);
	}

	public Map<String,Object> getValues(){
		return valueMap;
	}

	public Object getValue(String key){
		return valueMap.get(key);
	}


	public Object getNumValue(String key){
		Object val= valueMap.get(key);
		if(val==null){
			return 0;
		}else if(val instanceof  String) {
			return Double.parseDouble((String)val);
		}else{
			return val;
		}
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	@Override
	public String toString() {
		return "MultipleValueMap{" +
				"triggerID='" + triggerID + '\'' +
				", valueMap=" + valueMap +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultiplesValueMap that = (MultiplesValueMap) o;
		return Objects.equal(triggerID, that.triggerID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(triggerID);
	}
}
