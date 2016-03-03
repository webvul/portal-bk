package com.kii.extension.ruleengine.sdk.exception;

public class ObjectNotFoundException extends KiiCloudException {


	private ObjectScope scope;

	private String bucketID;

	private String objectID;

	public ObjectNotFoundException() {
		super();
	}

	@Override
	public int getStatusCode(){
		return 404;
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

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
}
