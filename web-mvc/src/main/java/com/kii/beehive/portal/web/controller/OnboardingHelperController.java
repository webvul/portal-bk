package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.manager.AppInfoManager;
import com.kii.beehive.portal.service.ThingTagService;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.beehive.portal.web.help.PortalException;
import com.kii.extension.sdk.entity.FederatedAuthResult;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
public class OnboardingHelperController {

    @Autowired
    private ThingTagService thingTagService;

	@Autowired
	private AppInfoDao appInfoDao;

	@Value("${beehive.kiicloud.dev-portal.username}")
	private String portalUserName;

	@Value("${beehive.kiicloud.dev-portal.password}")
	private String portalPwd;

	@Value("${beehive.kiicloud.dev-portal.masterApp}")
	private String masterAppID;



	@Autowired
	private AppInfoManager  appManager;

	// TODO is this method used for bucket KiiAppInfo initialization? each partner may have his own app
	@RequestMapping(path="/appinit",method={RequestMethod.POST},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void initAppContext(@RequestBody Map<String,Object>  paramMap){

		String userName= (String) paramMap.getOrDefault("portal.username",portalUserName);
		String pwd= (String) paramMap.getOrDefault("portal.pwd",portalPwd);

		String masterID= (String) paramMap.getOrDefault("portal.masterApp",masterAppID);

		appManager.initAppInfos(userName,pwd,masterID);

		return;

	}


    /**
     * 查询设备（vendorThingID）
     * GET /onboardinghelper/{vendorThingID}
     *
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param vendorThingID
     */
    @RequestMapping(path="/onboardinghelper/{vendorThingID}",method={RequestMethod.GET},consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Map<String,Object> getOnboardingInfo(@PathVariable("vendorThingID") String vendorThingID){

        GlobalThingInfo globalThingInfo = thingTagService.findThingByVendorThingID(vendorThingID);

		if(globalThingInfo == null) {
			throw new PortalException("vendorThingID not found", "vendorThingID " + vendorThingID + " is not found", HttpStatus.NOT_FOUND);
		}

		KiiAppInfo appInfo=appInfoDao.getAppInfoByID(globalThingInfo.getKiiAppID());

		Map<String,Object> map=new HashMap<>();
		map.put("kiiAppID",appInfo.getAppInfo().getAppID());
		map.put("kiiAppKey",appInfo.getAppInfo().getAppKey());
		map.put("kiiSiteUrl",appInfo.getAppInfo().getSiteUrl());

		FederatedAuthResult  result=appManager.getDefaultOwer(appInfo.getAppInfo().getAppID());

		map.put("ownerID",result.getUserID());
		map.put("ownerToken",result.getAppAuthToken());

		return map;

    }

}
