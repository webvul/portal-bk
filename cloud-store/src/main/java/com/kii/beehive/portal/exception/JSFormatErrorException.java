package com.kii.beehive.portal.exception;

import javax.script.ScriptException;

public class JSFormatErrorException extends BusinessException {


	public JSFormatErrorException(ScriptException ex){

		super.setErrorCode("jS_FORMAT_ERROR");

		super.setStatusCode(500);

		super.addParam("lineNumber",String.valueOf(ex.getLineNumber()));
		super.addParam("columnNumber",String.valueOf(ex.getColumnNumber()));
	}



}
