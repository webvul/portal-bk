package com.kii.extension.sdk.entity;

public enum SiteType {

	US(""),SG("sg"),JP("jp"),CN3("cn3"),CN("cn2"),BH01A("development-beehivecn3.internal"),BH02A("beehivecn4");

	private String url;

	private String site;

	SiteType(String site){

		this.site=site;
		if(site.equals("")){
			url="https://api.kii.com";
		}else {
			url = "https://api-" + site + ".kii.com";
		}
		return;
	}

	public String getSiteUrl(){
		return url;
	}


	public String getSite() {
		return site;
	}
}


