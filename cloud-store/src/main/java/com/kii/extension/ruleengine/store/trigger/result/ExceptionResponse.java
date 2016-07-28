package com.kii.extension.ruleengine.store.trigger.result;

import java.util.ArrayList;
import java.util.List;

import com.kii.extension.sdk.entity.KiiEntity;

public class ExceptionResponse extends KiiEntity implements TriggerResult {
	@Override
	public String getType() {
		return "exception";
	}

	private String message;

	private ExceptionResponse cause;

	private List<String> stackTrace=new ArrayList<>();

	private String triggerID;

	public ExceptionResponse(){


	}

	public ExceptionResponse(Throwable excep){

		this.message=excep.getMessage();

		if(excep.getCause()!=null  &&  !excep.getCause().getClass().equals(excep.getCause())) {
			this.cause = new ExceptionResponse(excep.getCause());
		}

		for(StackTraceElement elem:excep.getStackTrace()){


			stackTrace.add(elem.toString());

		}


	}



	public ExceptionResponse getCause() {
		return cause;
	}

	public void setCause(ExceptionResponse cause) {
		this.cause = cause;
	}

	public String getTriggerID() {
		return triggerID;
	}

	public void setTriggerID(String triggerID) {
		this.triggerID = triggerID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(List<String> stackTrace) {
		this.stackTrace = stackTrace;
	}
}
