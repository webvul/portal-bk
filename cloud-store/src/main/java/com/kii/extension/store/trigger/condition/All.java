package com.kii.extension.store.trigger.condition;


import com.kii.extension.store.trigger.Condition;
import com.kii.extension.store.trigger.ConditionType;

public class All implements Condition {
	
	public All(){
		
	}

	@Override
	public ConditionType getType() {
		return ConditionType.all;
	}

}
