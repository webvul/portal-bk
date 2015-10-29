package com.kii.extension.sdk.query.condition;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionType;

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
