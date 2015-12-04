package com.kii.beehive.portal.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
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
	private KiiUserSyncDao  userDao;

	@Autowired
	private FederatedAuthService  federatedAuthService;

	@Autowired
	private AppBindToolResolver resolver;




	private static  String DEFAULT_NAME="default_owner_id";


	@Cacheable(cacheNames="ttl_cache")
	public FederatedAuthResult getDefaultOwer(String appID){


		FederatedAuthResult  result=federatedAuthService.loginSalveApp(appID,DEFAULT_NAME,getDefaultUserPwd());

		return result;
	}

	private String getDefaultUserPwd(){

		return  DigestUtils.sha1Hex(DEFAULT_NAME+"_default_owner_beehive");

	}


	@Async
	public void initAppInfos(String userName,String pwd,String masterID){


//		String defaultOwnerName=DEFAULT_NAME;

		Map<String,AppInfo> appInfoMap=new HashMap<>();

		portalService.login(userName,pwd);
		portalService.getAppInfoList().forEach(app->appInfoMap.put(app.getAppID(),app));

		String portalAppID=appBindTool.getAppInfo("portal").getAppID();
		appInfoMap.remove(portalAppID);



		AppInfo  master=appInfoMap.remove(masterID);

		if(!masterSalveService.isMaster(master)) {
			masterSalveService.setMaster(master);
		}

		userDao.addDefaultOwner(DEFAULT_NAME,getDefaultUserPwd());

		KiiAppInfo masterAppInfo=new KiiAppInfo();
		masterAppInfo.setAppInfo(master);
		masterAppInfo.setMasterApp(true);

		appDao.addAppInfo(masterAppInfo);

		appInfoMap.values().forEach((app)->{

			KiiAppInfo appInfo=new KiiAppInfo();
			appInfo.setMasterApp(false);
			appInfo.setAppInfo(app);

			appDao.addAppInfo(appInfo);

			String currMaster=masterSalveService.checkMaster(app);
			if(!masterID.equals(currMaster)){

				masterSalveService.addSalveAppToMaster(app,master);
			}


		});

	}


//	private  void initDataWithDevPortal(String userName,String pwd){
//
//		service.login(userName, pwd);
//
//		List<AppInfo> appInfoList=service.getAppInfoList();
//		appInfoList.forEach((app) -> {
//
//			if (app.getAppID().equals(appDao.getPortalAppID())) {
//				return;
//			}
//
//			KiiAppInfo info = new KiiAppInfo();
//			info.setAppInfo(app);
//
//			appDao.addAppInfo(info);
//
//		});
//
//	}
//
//
//	private void setMasterSalve(String masterName){
//
//
//		KiiAppInfo masterInfo=appDao.getAppInfoByID(masterName);
//
//		if(masterInfo==null){
//			throw new ObjectNotFoundException();
//		}
//		boolean isMaster=masterSalveService.isMaster(masterInfo.getAppInfo());
//
//		if(!isMaster){
//
//			masterSalveService.setMaster(masterInfo.getAppInfo());
//
//		}
//		appDao.setMasterAppInfo(masterInfo.getId());
//
//		Map<String,AppInfo> appMap=appDao.getAllAppInfo();
//
//		appMap.values().stream()
//				.filter(info -> !info.getAppID().equals(appDao.getPortalAppID()))
//				.forEach(info -> {
//
//					masterSalveService.addSalveAppToMaster(masterInfo.getAppInfo(), info);
//
//					String master = masterSalveService.checkMaster(info);
//					if (master.equals(info.getAppID())) {
//						log.info("salve create success:");
//					}
//				});
//
//
//	}
//


}
