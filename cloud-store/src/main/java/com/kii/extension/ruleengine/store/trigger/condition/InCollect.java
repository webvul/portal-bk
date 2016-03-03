package com.kii.extension.ruleengine.store.trigger.condition;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.ruleengine.store.trigger.ConditionType;


public class InCollect extends SimpleCondition {

	
	@Override
	public ConditionType getType() {
		return ConditionType.in;
	}
	public InCollect(){
		
	}

	private List<?>  values=new ArrayList<Object>();

	
	public List<?> getValues() {
		return values;
	}

	public void setValues(List<?> values) {
		this.values = values;
	}
	
	
	

}
