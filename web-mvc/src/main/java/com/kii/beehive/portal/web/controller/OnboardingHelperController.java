package com.kii.beehive.portal.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.BeehiveAppInfoManager;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
public class OnboardingHelperController {

	@Autowired
	private TagThingManager tagThingManager;

	@Autowired
	private AppInfoDao appInfoDao;

	@Value("${beehive.kiicloud.dev-portal.username}")
	private String portalUserName;

	@Value("${beehive.kiicloud.dev-portal.password}")
	private String portalPwd;

	@Value("${beehive.kiicloud.dev-portal.masterApp}")
	private String masterAppID;

	@Autowired
	private BeehiveAppInfoManager appInfoManager;

	@Autowired
	private AppInfoManager appManager;

	@Autowired
	private ObjectMapper mapper;


	/**
	 * important:
	 * this API is supposed to be called only when initialize the environment
	 *
	 * @param paramMap
	 */
	@RequestMapping(path = "/appinit", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void initAppContext(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) {

		String userName = (String) paramMap.getOrDefault("portal.username", portalUserName);
		String pwd = (String) paramMap.getOrDefault("portal.pwd", portalPwd);

		String masterID = (String) paramMap.getOrDefault("portal.masterApp", masterAppID);


		CallbackUrlParameter param = new CallbackUrlParameter();
		param.setStateChange(CallbackNames.STATE_CHANGED);
		param.setThingCreated(CallbackNames.THING_CREATED);

		String url = request.getRequestURL().toString();
		String subUrl = url.substring(0, url.indexOf("/appinit")) + CallbackNames.CALLBACK_URL;
		param.setBaseUrl(subUrl);

		appInfoManager.initAllAppInfo(userName, pwd, masterID, param);

		return;
	}

	@RequestMapping(path = "/appRegist/{appID}", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void initAppContext(@PathVariable("appID") String appID, HttpServletRequest request) {

		CallbackUrlParameter param = new CallbackUrlParameter();
		param.setStateChange(CallbackNames.STATE_CHANGED);
		param.setThingCreated(CallbackNames.THING_CREATED);

		String url = request.getRequestURL().toString();
		String subUrl = url.substring(0, url.indexOf("/appRegist")) + CallbackNames.CALLBACK_URL;
		param.setBaseUrl(subUrl);

		appInfoManager.addAppInfo(appID, param);
		return;
	}


	/**
	 * 查询关联KiiApp信息
	 * GET /onboardinghelper/{vendorThingID}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param vendorThingID
	 */
	@RequestMapping(path = "/onboardinghelper/{vendorThingID}", method = {RequestMethod.GET}, consumes = {"*"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ModelAndView getOnboardingInfo(@PathVariable("vendorThingID") String vendorThingID) {

		List<GlobalThingInfo> thingInfos = tagThingManager.getThingsByVendorThingIds(Arrays.asList(vendorThingID));

		if (thingInfos.isEmpty()) {
			throw new PortalException("vendorThingID not found",
					"vendorThingID " + vendorThingID + " is not found", HttpStatus.NOT_FOUND);
		}
		GlobalThingInfo globalThingInfo = thingInfos.get(0);

//		if (!tagThingManager.isThingCreator(globalThingInfo) && tagThingManager.isThingOwner(globalThingInfo)) {
//			throw new BeehiveUnAuthorizedException("not creator or owner");
//		}

		KiiAppInfo appInfo = appInfoDao.getAppInfoByID(globalThingInfo.getKiiAppID());

		Map<String, Object> map = new HashMap<>();
		map.put("kiiAppID", appInfo.getAppInfo().getAppID());
		map.put("kiiAppKey", appInfo.getAppInfo().getAppKey());
		map.put("kiiSiteUrl", appInfo.getAppInfo().getSiteUrl());

		FederatedAuthResult result = appManager.getDefaultOwer(appInfo.getAppInfo().getAppID());

		map.put("ownerID", result.getUserID());
		map.put("ownerToken", result.getAppAuthToken());

		ModelAndView model = new ModelAndView();
		model.addAllObjects(map);
		model.setViewName("jsonView");

		return model;


	}


	@RequestMapping(path = "/info", method = {RequestMethod.GET})
	public Map<String, String> info(HttpServletRequest httpRequest) {
		Map<String, String> map = new HashMap<>();
		InputStream manifestStream = httpRequest.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
		try {
			Manifest manifest = new Manifest(manifestStream);
			Attributes attributes = manifest.getMainAttributes();
			String impVersion = attributes.getValue("Implementation-Version");
			String impTitle = attributes.getValue("Implementation-Title");
			String impTimestamp = attributes.getValue("Implementation-Timestamp");
			map.put("Version", impVersion);
			map.put("Title", impTitle);
			map.put("Date", impTimestamp);
		} catch (IOException ex) {
			//log.warn("Error while reading version: " + ex.getMessage());
		}
		return map;
	}

}
