package com.kii.extension.sdk.query.condition;

import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionType;
import com.kii.extension.sdk.query.FieldType;

public class FieldExist extends SimpleCondition {

	@Override
	public ConditionType getType() {
		return ConditionType.hasField;
	}
	
	public FieldExist(){
	
	}
	
	public FieldExist(String field,String val){
		this();
		setField(field);
		setFieldType(FieldType.valueOf(val));

	}

	
	private FieldType fieldType;



	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}
	
	
}
