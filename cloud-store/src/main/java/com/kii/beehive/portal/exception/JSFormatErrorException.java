package com.kii.beehive.portal.exception;

import javax.script.ScriptException;

public class JSFormatErrorException extends StoreException {


	public JSFormatErrorException(ScriptException ex){

		super.setErrorCode("JSFormatError");

		super.setStatusCode(500);

		super.setMessage(" js file format error:line number:"+ex.getLineNumber()+"  column number: "+ex.getColumnNumber());



	}



}
