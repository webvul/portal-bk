package com.kii.beehive.business.helper;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import com.kii.extension.sdk.service.FederatedAuthService;

@Component
public class SlaveAppDefaultTokenBind {
	
	
	@Autowired
	private AppBindToolResolver resolver;
	
	@Autowired
	private FederatedAuthService authService;
	
	@Autowired
	private AppInfoDao appInfoDao;
	
	@Cacheable(cacheNames = CacheConfig.LONGLIVE_CACHE,key="'slave_app_default_token_'+#appID")
	public String getToken(String appID){
		
		KiiAppInfo appInfo=appInfoDao.getObjectByID(appID);
		
		return appInfo.getFederatedAuthResult().getAppAuthToken();
		
	}
	
	@CacheEvict(cacheNames = CacheConfig.LONGLIVE_CACHE,key="'slave_app_default_token_'+#appID")
	public void refreshSlaveToken(String appID){
		
		KiiAppInfo info=appInfoDao.getAppInfoByID(appID);
		
//		FederatedAuthResult  result=info.getFederatedAuthResult();
		
//		if(result==null) {
		
		FederatedAuthResult result=authService.loginSalveApp(info.getAppInfo(), DEFAULT_NAME, DEFAULT_PWD);
			
			Map<String, Object> param = new HashMap<>();
			param.put("federatedAuthResult", result);
			
			appInfoDao.updateEntity(param, appID);
			
//		}
		
		return;
	}
	
	
	public  static  String DEFAULT_NAME="default_owner_id";
	
	public  static String DEFAULT_PWD= DigestUtils.sha1Hex(DEFAULT_NAME+"_default_owner_beehive");
	
}
