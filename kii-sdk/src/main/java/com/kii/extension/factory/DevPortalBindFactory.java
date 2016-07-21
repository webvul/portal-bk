package com.kii.extension.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kii.extension.sdk.service.DevPortalService;

@Configuration
public class DevPortalBindFactory {

	private String userName;

	private String pwd;

	private String devPortalUrl;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String pwd) {
		this.pwd = pwd;
	}

	public void setDevPortalUrl(String devPortalUrl) {
		this.devPortalUrl = devPortalUrl;
	}


	@Bean
	public DevPortalService getDevPortalService(){

		DevPortalService service=new DevPortalService();

		service.setDevPortalUrl(devPortalUrl);

		return service;
	}

//	@Bean
	public DevPortalBindTool getDevPortalBindTool(){

		DevPortalBindTool bindTool= new DevPortalBindTool();
		bindTool.setPassword(pwd);
		bindTool.setUserName(userName);

		return bindTool;
	}
}
