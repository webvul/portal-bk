package com.kii.extension.sdk.query.condition;

import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionType;

public class All implements Condition {
	
	public All(){
		
	}

	@Override
	public ConditionType getType() {
		return ConditionType.all;
	}

}
