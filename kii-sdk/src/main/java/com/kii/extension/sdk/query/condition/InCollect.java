package com.kii.extension.sdk.query.condition;

import java.util.ArrayList;
import java.util.Collection;

import com.kii.extension.sdk.query.ConditionType;

public class InCollect extends SimpleCondition {

	
	@Override
	public ConditionType getType() {
		return ConditionType.in;
	}
	public InCollect(){
		
	}

	private Collection<?> values=new ArrayList<Object>();

	
	public Collection<?> getValues() {
		return values;
	}

	public void setValues(Collection<?> values) {
		this.values = values;
	}
	
	
	

}
