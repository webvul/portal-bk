package com.kii.beehive.portal.web.controller;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.helper.PermissionTreeService;
import com.kii.beehive.portal.helper.RuleSetService;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.beehive.portal.store.entity.CallbackUrlParameter;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.beehive.portal.store.entity.es.EsDataSourceCfgEntry;
import com.kii.beehive.portal.web.constant.CallbackNames;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;
import com.kii.beehive.portal.web.help.BeehiveAppInfoManager;
import com.kii.extension.sdk.entity.FederatedAuthResult;

/**
 * Beehive API - Thing API
 * <p>
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
public class UtilToolsController {

	private static final String SYS_APPINIT = "/sys/appinit";

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

	@Value("${spring.profile}")
	private String profile;

	@Autowired
	private BeehiveAppInfoManager appInfoManager;

	@Autowired
	private AppInfoManager appManager;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PermissionTreeService permissionTreeService;


	@Autowired
	private BeehiveConfigDao  configDao;


	@Autowired
	private RuleSetService    ruleSetService;


	/**
	 * important:
	 * this API is supposed to be called only when initialize the environment
	 *
	 * @param paramMap
	 */
	@RequestMapping(value =  SYS_APPINIT, method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void initAppContext(@RequestBody Map<String, Object> paramMap, HttpServletRequest request) {

		String userName = (String) paramMap.getOrDefault("portal.username", portalUserName);
		String pwd = (String) paramMap.getOrDefault("portal.pwd", portalPwd);

		String masterID = (String) paramMap.getOrDefault("portal.masterApp", masterAppID);


		CallbackUrlParameter param = getCallbackUrlParameter(request);

		appInfoManager.initAllAppInfo(userName, pwd, masterID, param);

		return;
	}

	private CallbackUrlParameter getCallbackUrlParameter(HttpServletRequest request) {
		CallbackUrlParameter param = new CallbackUrlParameter();
		param.setStateChange(CallbackNames.STATE_CHANGED);
		param.setThingCreated(CallbackNames.THING_CREATED);
		param.setCommandResponse(CallbackNames.THING_CMD_RESPONSE);

		String url = request.getRequestURL().toString();
		String subUrl=null;
		if(url.contains("Regist")){
			subUrl= url.substring(0, url.indexOf("/sys/appRegist")) + CallbackNames.CALLBACK_URL;
		}else {
			subUrl = url.substring(0, url.indexOf(SYS_APPINIT)) + CallbackNames.CALLBACK_URL;
		}
		param.setBaseUrl(subUrl);
		return param;
	}


	@RequestMapping(value = SYS_APPINIT+"/serviceExt", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void deployServiceExtensionCallback(HttpServletRequest request) {

		CallbackUrlParameter param = getCallbackUrlParameter(request);

		appInfoManager.updateServiceExtension(param);

		return;
	}


	@RequestMapping(value = "/sys/appRegist/{appID}", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void initAppContext(@PathVariable("appID") String appID, HttpServletRequest request) {

		CallbackUrlParameter param = getCallbackUrlParameter(request);

		appInfoManager.addAppInfo(appID, param);
		return;
	}


	@RequestMapping(value = "/sys/initEnv", method = {RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public void envInit(){



		EsDataSourceCfgEntry entry=new EsDataSourceCfgEntry();

		entry.setBizDataCommonCarId("source");
		entry.setBizDataGatewayIndex("4e47ffb1-0be8-4792-91f2-673be1626b57");
		entry.setBizDataGatewayIndexTypeLeave("CarOut");
		entry.setBizDataParkingSpaceIndex("74e58d2e-cb1f-4ada-b15e-49aea780f664");
		entry.setBizDataParkingSpaceIndexTypeLeave("CarOut");
		entry.setBizDataCommonEventTime("object.eventTime");

		configDao.saveConfigEntry(entry);


		ruleSetService.initRuleList();


	}


	/**
	 * 查询关联KiiApp信息
	 * GET /onboardinghelper/{vendorThingID}
	 * <p>
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param vendorThingID
	 */
	@RequestMapping(value = "/onboardinghelper/{vendorThingID}", method = {RequestMethod.GET}, consumes = {"*"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ModelAndView getOnboardingInfo(@PathVariable("vendorThingID") String vendorThingID) {

		List<GlobalThingInfo> thingInfos = tagThingManager.getThingsByVendorThingIds(Arrays.asList(vendorThingID));

		if (thingInfos.isEmpty()) {

			throw new PortalException(ErrorCode.NOT_FOUND, "type", "global thing", "objectID", vendorThingID);
		}
		GlobalThingInfo globalThingInfo = thingInfos.get(0);

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


	@RequestMapping(value = "/sys/permissionTree", method = {RequestMethod.GET}, consumes = {"*"})
	public PermissionTree getFullPermissTree() {

		return permissionTreeService.getFullPermissionTree();
	}

	@RequestMapping(value = "/info", method = {RequestMethod.GET}, consumes = {"*"})
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
			map.put("profile",profile);
		} catch (IOException ex) {
			//log.warn("Error while reading version: " + ex.getMessage());
		}
		return map;
	}

}
