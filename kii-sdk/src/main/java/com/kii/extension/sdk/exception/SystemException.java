package com.kii.extension.sdk.exception;

import org.apache.http.HttpResponse;

public class SystemException extends KiiCloudException {
	
	SystemException(HttpResponse response ){
		super(response);
	}
	
}
