package com.kii.extension.sdk.entity;

public class BucketInfo {

	private String bucketName;

	private ScopeType scopeType;

	private String scopeName;

	public BucketInfo(){

	}

	public BucketInfo(String bucketName,ScopeType scope,String scopeName){
		this.bucketName=bucketName;
		this.scopeType=scope;
		this.scopeName=scopeName;
	}


	public BucketInfo(String bucketName){

		this.bucketName=bucketName;
		this.scopeType=ScopeType.App;
		this.scopeName=null;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public ScopeType getScopeType() {
		return scopeType;
	}

	public void setScopeType(ScopeType scopeType) {
		this.scopeType = scopeType;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
}
