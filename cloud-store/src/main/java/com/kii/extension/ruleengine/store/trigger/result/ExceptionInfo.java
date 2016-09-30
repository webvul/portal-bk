package com.kii.extension.ruleengine.store.trigger.result;

import java.util.ArrayList;
import java.util.List;

public class ExceptionInfo {


	private String message;

	private ExceptionInfo cause;

	private List<String> stackTrace=new ArrayList<>();



	public ExceptionInfo(Throwable excep){

		this.message=excep.getMessage();


		for(StackTraceElement elem:excep.getStackTrace()){

			stackTrace.add(elem.toString());

		}

		if(excep.getCause()!=null  &&  !excep.getCause().getClass().equals(excep.getCause())) {
			this.cause = new ExceptionInfo(excep.getCause());
		}



	}



	public ExceptionInfo getCause() {
		return cause;
	}

	public void setCause(ExceptionInfo cause) {
		this.cause = cause;
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
