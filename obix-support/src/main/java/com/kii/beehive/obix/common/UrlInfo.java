package com.kii.beehive.obix.common;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;

public class UrlInfo {


	private final String host;

	private final String rootUrl;

	private final String path;

	private final String fullUrl;

	public UrlInfo(URI url){

		fullUrl=url.toASCIIString();

		host=url.getScheme()+"//"+url.getHost()+":"+url.getPort();

		rootUrl= StringUtils.substringBefore(url.getPath(),"/def")+"/def";

		path=StringUtils.substringAfter(url.getPath(),"/def");

	}


	public String addToRootUrl(String subUrl) {
		if(subUrl.startsWith("/")){
			return rootUrl+subUrl;
		}else {
			return rootUrl + "/" + subUrl;
		}
	}

	public String getFullUrl(){
		return fullUrl;
	}

	public String addToFullPath(String subUrl) {
		if(subUrl.startsWith("/")){
			return rootUrl+path+subUrl;
		}else {
			return rootUrl+path+ "/" + subUrl;
		}
	}}
