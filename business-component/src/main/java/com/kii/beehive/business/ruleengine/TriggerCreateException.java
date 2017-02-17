package com.kii.beehive.business.ruleengine;

public class TriggerCreateException extends RuntimeException{


	private String reason;

	public TriggerCreateException(String reason){
		super();
		this.reason=reason;
	}

	public TriggerCreateException(String reason, Exception e){
		super(e);
		this.reason=reason;
	}

	public String getReason(){
		return reason;
	}

}
