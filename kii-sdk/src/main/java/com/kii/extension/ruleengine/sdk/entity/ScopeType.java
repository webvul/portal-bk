package com.kii.extension.ruleengine.sdk.entity;

public enum ScopeType {

	App(null),
	Group("groups"),
	LoginName("users","LOGIN_NAME"),UserID("users"),Email("users","EMAIL"),Phone("users","PHONE"),
	VenderThing("things","VENDOR_THING_ID"),Thing("things");

	private String prefix;

	private String subUrl;

	ScopeType(String subUrl,String prefix){
		this.prefix=prefix;
		this.subUrl=subUrl;
	}

	ScopeType(String subUrl){
		this.prefix=null;
		this.subUrl=subUrl;
	}

	public String getSubUrl(String name){

		if(subUrl==null){
			return "";
		}

		if(prefix==null){
			return "/"+subUrl+"/"+name;
		}
		return "/"+subUrl+"/"+prefix+":"+name;
	}
}
