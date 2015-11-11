package com.kii.extension.sdk.entity;

public enum SiteType {

	US("api"),SG("api-sg"),JP("api-jp"),CN3("api-cn3"),CN("api-cn2"),BH01A("api-development-beehivecn3.internal");

	private String url;

	private String site;

	SiteType(String site){

		this.site=site;
		url="https://"+site+".kii.com";
	}

	public String getSiteUrl(){
		return url;
	}


	public String getSite() {
		return site;
	}
}


