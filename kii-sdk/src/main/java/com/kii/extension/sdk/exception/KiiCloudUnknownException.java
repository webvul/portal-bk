package com.kii.extension.sdk.exception;

import org.apache.http.HttpResponse;

public class KiiCloudUnknownException extends KiiCloudException {
	
	public KiiCloudUnknownException(HttpResponse response){
		super(response);
	}
}
