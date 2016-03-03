package com.kii.extension.ruleengine.sdk.query.condition;

import com.kii.extension.ruleengine.sdk.query.Condition;
import com.kii.extension.ruleengine.sdk.query.ConditionType;

public class All implements Condition {
	
	public All(){
		
	}

	@Override
	public ConditionType getType() {
		return ConditionType.all;
	}

}
