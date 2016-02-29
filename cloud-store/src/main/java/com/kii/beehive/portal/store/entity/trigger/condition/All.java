package com.kii.beehive.portal.store.entity.trigger.condition;


import com.kii.beehive.portal.store.entity.trigger.Condition;
import com.kii.beehive.portal.store.entity.trigger.ConditionType;

public class All implements Condition {
	
	public All(){
		
	}

	@Override
	public ConditionType getType() {
		return ConditionType.all;
	}

}
