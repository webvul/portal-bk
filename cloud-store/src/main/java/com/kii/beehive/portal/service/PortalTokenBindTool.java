package com.kii.beehive.portal.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.context.TokenBindTool;


@Component
public class PortalTokenBindTool implements TokenBindTool {
	
	public static final String PORTAL_OPER="portalOper";
	
	@Autowired
	private PortalOperateUserService  portalOper;
	
	@PostConstruct
	public void init(){
		
		portalOper.init();
	}
	
	
	@Override
	public String getToken() {
		return portalOper.getToken();
	}
	
	@Override
	public String getBindName() {
		return PORTAL_OPER;
	}
	
	@Override
	public void refreshToken() {
		
		portalOper.init();
		
	}
	
	
}
