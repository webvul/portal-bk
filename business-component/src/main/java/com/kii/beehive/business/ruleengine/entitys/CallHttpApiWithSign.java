package com.kii.beehive.business.ruleengine.entitys;

public class CallHttpApiWithSign extends CallHttpApiInEngine {
	
	
	
	@Override
	public TargetType getType() {
		return TargetType.HttpApiCallWithSign;
	}
	
	private String siteName;
	
	public String getSiteName() {
		return siteName;
	}
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
}
