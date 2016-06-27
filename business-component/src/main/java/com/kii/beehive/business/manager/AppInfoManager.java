package com.kii.beehive.business.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveParameterDao;
import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.context.AppBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import com.kii.extension.sdk.service.AppMasterSalveService;
import com.kii.extension.sdk.service.DevPortalService;
import com.kii.extension.sdk.service.FederatedAuthService;

@Component
public class AppInfoManager {


	private Logger log= LoggerFactory.getLogger(AppInfoManager.class);

	@Autowired
	private AppInfoDao appDao;


	@Autowired
	@Qualifier("propAppBindTool")
	private AppBindTool appBindTool;

	@Autowired
	private AppMasterSalveService masterSalveService;


	@Autowired
	private DevPortalService portalService;

	@Autowired
	private KiiUserService userDao;

	@Autowired
	private FederatedAuthService  federatedAuthService;

	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private BeehiveParameterDao paramDao;

	private static  String DEFAULT_NAME="default_owner_id";

	private static String DEFAULT_PWD=DigestUtils.sha1Hex(DEFAULT_NAME+"_default_owner_beehive");


	public boolean verifyAppToken(String appID,String token){

		KiiAppInfo info=appDao.getAppInfoByID(appID);

		if(info==null){
			return false;
		}
		resolver.setToken(token);
		return paramDao.verify(info.getAppInfo(),token);

	}

	@Cacheable(cacheNames="ttl_cache")
	public FederatedAuthResult getDefaultOwer(String appID){

		KiiAppInfo info=appDao.getAppInfoByID(appID);

		FederatedAuthResult  result=info.getFederatedAuthResult();

		if(result==null) {

			result=federatedAuthService.loginSalveApp(info.getAppInfo(), DEFAULT_NAME, DEFAULT_PWD);

			Map<String, Object> param = new HashMap<>();
			param.put("federatedAuthResult", result);

			appDao.updateEntity(param, appID);

		}

		return result;
	}


	private String getDefaultUserPwd(){

		return  DigestUtils.sha1Hex(DEFAULT_NAME+"_default_owner_beehive");

	}


	public AppInfo  addAppInfo(String appID,String userName,String pwd,String masterID){

		portalService.login(userName,pwd);

		AppInfo appInfo=portalService.getAppInfoByID(appID);

		AppInfo master=portalService.getAppInfoByID(masterID);

		setSlaveApp(master,appInfo);

		return appInfo;

	}
	/**
	 * important:
	 * this API is supposed to be called only when initialize the environment
	 *
	 * refer to below for the details of federated authentication process:
	 *  - https://wiki.kii.com/display/Products/Federated+authentication
	 *
	 */
	public void initAppInfos(String userName,String pwd,String masterID){


		Map<String,AppInfo> appInfoMap=new HashMap<>();

		// login dev portal
		portalService.login(userName,pwd);

		portalService.getAppInfoList().forEach(app->appInfoMap.put(app.getAppID(),app));

		String portalAppID=appBindTool.getAppInfo("portal").getAppID();
		appInfoMap.remove(portalAppID);

		AppInfo  master=appInfoMap.remove(masterID);

		if(!masterSalveService.isMaster(master)) {
			masterSalveService.setMaster(master);
		}

		KiiAppInfo masterAppInfo=new KiiAppInfo();
		masterAppInfo.setAppInfo(master);
		masterAppInfo.setMasterApp(true);

		appDao.setMasterAppInfo(masterAppInfo);

		userDao.addDefaultOwner(DEFAULT_NAME,DEFAULT_PWD);

		appInfoMap.values().forEach((app)->{

			setSlaveApp(master, app);

		});

		log.info("initAppInfos end");
	}

	/**
	 * configure slave app for federated authentication,
	 * and save the app info into bucket "KiiAppInfoStore" of portal app
	 *
	 * @param master
	 * @param app
     */
	private void setSlaveApp(AppInfo master, AppInfo app) {
		KiiAppInfo appInfo=new KiiAppInfo();
		appInfo.setMasterApp(false);
		appInfo.setAppInfo(app);
		appInfo.setId(app.getAppID());

		String currMaster=masterSalveService.checkMaster(app);

		if(!master.getAppID().equals(currMaster)){

			AppMasterSalveService.ClientInfo  clientInfo=masterSalveService.addSalveApp(master,app);
			masterSalveService.registInSalve(clientInfo,master,app);
		}


		FederatedAuthResult result = null;
		final int retryCount = 6;
		for (int i = 0; i < retryCount; i++) {

			try {
				result=federatedAuthService.loginSalveApp(app,DEFAULT_NAME,DEFAULT_PWD);
				break;
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage(), e);

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					log.error(e1.getMessage(), e1);
				}

				if(i == retryCount - 1) {
					throw e;
				}
			}
		}

		appInfo.setFederatedAuthResult(result);
		appDao.addAppInfo(appInfo);
	}


	public String getMasterAppID() {
		return appDao.getMasterAppInfo().getId();
	}
}
