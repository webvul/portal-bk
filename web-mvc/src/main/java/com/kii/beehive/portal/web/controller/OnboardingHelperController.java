package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.AppInfoManager;
import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.beehive.portal.web.entity.ThingInput;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/onboardinghelper",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class OnboardingHelperController {

    @Autowired
    private ThingManager thingManager;

	@Autowired
	private AppInfoDao appInfoDao;

    /**
     * 查询设备（vendorThingID）
     * GET /onboardinghelper/{vendorThingID}
     *
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param vendorThingID
     */
    @RequestMapping(path="/{vendorThingID}",method={RequestMethod.GET})
    public Map<String,Object> getOnboardingInfo(@PathVariable("vendorThingID") String vendorThingID){

        GlobalThingInfo globalThingInfo = thingManager.findThingByVendorThingID(vendorThingID);

		KiiAppInfo appInfo=appInfoDao.getAppInfoByID(globalThingInfo.getKiiAppID());

		Map<String,Object> map=new HashMap<>();
		map.put("kiiAppID",appInfo.getAppInfo().getAppID());
		map.put("kiiAppKey",appInfo.getAppInfo().getAppKey());
		map.put("kiiSiteUrl",appInfo.getAppInfo().getSiteUrl());
		map.put("ownerID",appInfo.getDefaultThingOwnerID());

		return map;

    }

}
