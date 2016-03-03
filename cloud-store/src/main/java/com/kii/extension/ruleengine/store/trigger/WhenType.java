package com.kii.extension.ruleengine.store.trigger;

public enum WhenType {


	CONDITION_TRUE,
	CONDITION_FALSE_TO_TRUE,
	CONDITION_TRUE_TO_FALSE,
	CONDITION_CHANGED;
	
	

	public boolean checkStatus(boolean oldStatus, boolean newStatus){

		switch(this){
			case CONDITION_TRUE:
				return  newStatus;
			case CONDITION_FALSE_TO_TRUE:
				return  (!oldStatus)&&newStatus;
			case CONDITION_TRUE_TO_FALSE:
				return (oldStatus)&&(!newStatus);
			case CONDITION_CHANGED:
				return oldStatus!=newStatus;
			default:
				return false;
		}

	}
}
