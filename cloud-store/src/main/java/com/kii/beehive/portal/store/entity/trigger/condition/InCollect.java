package com.kii.beehive.portal.store.entity.trigger.condition;

import java.util.ArrayList;
import java.util.List;

import com.kii.beehive.portal.store.entity.trigger.ConditionType;


public class InCollect extends SimpleCondition {

	
	@Override
	public ConditionType getType() {
		return ConditionType.in;
	}
	public InCollect(){
		
	}

	private List<Object>  values=new ArrayList<Object>();

	
	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}
	
	
	

}
