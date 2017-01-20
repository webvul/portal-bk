package com.kii.extension.sdk.context;

public class NoneTokenBindTool implements TokenBindTool {
	@Override
	public String getToken() {
		return null;
	}
	
	@Override
	public String getBindName() {
		return BindType.None.name();
	}
	
	@Override
	public void refreshToken() {
		
	}
}
