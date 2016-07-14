package com.kii.beehive.portal.web.exception;

import org.apache.http.HttpStatus;

public class ErrorCode {
	public static final ErrorCode NOT_FOUND = new ErrorCode("NOT_FOUND",HttpStatus.SC_NOT_FOUND);
	public static final ErrorCode REQUIRED_FIELDS_MISSING = new ErrorCode("REQUIRED_FIELDS_MISSING",HttpStatus.SC_BAD_REQUEST);

	public static final ErrorCode INVALID_INPUT = new ErrorCode("INVALID_INPUT",HttpStatus.SC_BAD_REQUEST);

	public static final ErrorCode METHOD_NOT_ALLOWED = new ErrorCode("METHOD_NOT_ALLOWED",HttpStatus.SC_METHOD_NOT_ALLOWED);
	public static final ErrorCode INVALID_TOKEN=new ErrorCode("INVALID_TOKEN",HttpStatus.SC_FORBIDDEN);
	public static final ErrorCode TAG_NO_PRIVATE=new ErrorCode("TAG_NO_PRIVATE",HttpStatus.SC_FORBIDDEN);
	public static final ErrorCode THING_NO_PRIVATE=new ErrorCode("THING_NO_PRIVATE",HttpStatus.SC_FORBIDDEN);
	public static final ErrorCode BAD_REQUEST=new ErrorCode("BAD_REQUEST",HttpStatus.SC_BAD_REQUEST);
	public static final ErrorCode DUPLICATE_OBJECT=new ErrorCode("DUPLICATE_OBJECT",HttpStatus.SC_CONFLICT);
	public static final ErrorCode INVALID_PASSWORD=new ErrorCode("INVALID_PASSWORD",HttpStatus.SC_BAD_REQUEST);




	private String name;
	private int status;

	private ErrorCode(String name, int status){
		this.name=name;
		this.status=status;

	}

	public String getName() {
		return name;
	}

	public int getStatus() {
		return status;
	}



}
