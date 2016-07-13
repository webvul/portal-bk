package com.kii.beehive.portal.exception;

import javax.script.ScriptException;

public class JSFormatErrorException extends RuntimeException {


	private int lineNumber;

	private int columnNumber;

	private String fileName;

	private String message;

	public JSFormatErrorException(ScriptException ex){

		lineNumber=ex.getLineNumber();

		columnNumber=ex.getColumnNumber();

		fileName=ex.getFileName();

		message=ex.getMessage();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
