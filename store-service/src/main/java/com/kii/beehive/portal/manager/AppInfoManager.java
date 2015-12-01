package com.kii.beehive.portal.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.KiiUserSyncDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.context.AppBindTool;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.FederatedAuthResult;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
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


	private String DEFAULT_NAME="default_owner_id";


	public void initAppInfos(String userName,String pwd,String masterID){


		String defaultOwnerName=DEFAULT_NAME;

		Map<String,AppInfo> appInfoMap=new HashMap<>();

		portalService.login(userName,pwd);
		portalService.getAppInfoList().forEach(app->appInfoMap.put(app.getAppID(),app));

		String portalAppID=appBindTool.getAppInfo("portal").getAppID();
		appInfoMap.remove(portalAppID);

		AppInfo  master=appInfoMap.remove(masterID);

		if(!masterSalveService.isMaster(master)) {
			masterSalveService.setMaster(master);
		}
		String defaultOwnerPwd= DigestUtils.sha1Hex(defaultOwnerName+"_default_owner_beehive");

		String id=userDao.addDefaultOwner(defaultOwnerName,defaultOwnerPwd);

		KiiAppInfo masterAppInfo=new KiiAppInfo();
		masterAppInfo.setAppInfo(master);
		masterAppInfo.setMasterApp(true);
		masterAppInfo.setDefaultThingOwnerID(id);

		appDao.addAppInfo(masterAppInfo);

		appInfoMap.values().forEach((app)->{

			String currMaster=masterSalveService.checkMaster(app);
			if(!masterID.equals(currMaster)){

				masterSalveService.addSalveAppToMaster(app,master);
			}

			FederatedAuthResult  result=federatedAuthService.loginSalveApp(app.getAppID(),defaultOwnerName,defaultOwnerPwd);

			String slaveUserID=result.getUserID();

			KiiAppInfo appInfo=new KiiAppInfo();
			appInfo.setDefaultThingOwnerID(slaveUserID);
			appInfo.setMasterApp(false);

			appDao.addAppInfo(appInfo);

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
