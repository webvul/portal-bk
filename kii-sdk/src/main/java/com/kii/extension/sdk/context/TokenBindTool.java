package com.kii.extension.sdk.context;

public interface TokenBindTool {

	String getToken();
	
	String getBindName();
	
	void refreshToken();
	
	enum BindType{
		admin,user,thing,None,Custom;
	}

}
