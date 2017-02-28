package com.kii.beehive.portal.store.entity.configEntry;

public class RuleEngineToken extends BeehiveConfig {
	
	public static final String RULENGINE_TOKEN = "ruleEngineToken";
	private String authToken;
	
	public RuleEngineToken() {
		
		super.setConfigName(RULENGINE_TOKEN);
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
