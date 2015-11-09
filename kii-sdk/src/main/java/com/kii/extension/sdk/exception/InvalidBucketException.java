package com.kii.extension.sdk.exception;

public class InvalidBucketException extends KiiCloudException {

	private ObjectScope scope;

	private String bucketID;

	@Override
	public int getStatusCode(){
		return 400;
	}


	public ObjectScope getScope() {
		return scope;
	}

	public void setScope(ObjectScope scope) {
		this.scope = scope;
	}

	public String getBucketID() {
		return bucketID;
	}

	public void setBucketID(String bucketID) {
		this.bucketID = bucketID;
	}


}
