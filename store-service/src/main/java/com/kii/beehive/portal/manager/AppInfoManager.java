package com.kii.beehive.portal.manager;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.service.AppMasterSalveService;
import com.kii.extension.sdk.service.DevPortalService;

@Component
public class AppInfoManager {


	private Logger log= LoggerFactory.getLogger(AppInfoManager.class);

	@Autowired
	private AppInfoDao appDao;



	@Autowired
	private DevPortalService service;

	@Autowired
	private AppMasterSalveService masterSalveService;



	public void initAppInfos(String userName,String pwd,String master){

		initDataWithDevPortal(userName, pwd);


		setMasterSalve(master);


	}


	private void initDataWithDevPortal(String userName,String pwd){

		service.login(userName, pwd);

		List<AppInfo> appInfoList=service.getAppInfoList();
		appInfoList.forEach((app) -> {

			if (app.getAppID().equals(appDao.getPortalAppID())) {
				return;
			}

			KiiAppInfo info = new KiiAppInfo();
			info.setAppInfo(app);

			info.setAppName(app.getName());
			info.setId(info.getId());

			appDao.addAppInfo(info);

		});

	}


	private void setMasterSalve(String masterName){


		KiiAppInfo masterInfo=appDao.getAppInfoByName(masterName);

		if(masterInfo==null){
			throw new ObjectNotFoundException();
		}
		boolean isMaster=masterSalveService.isMaster(masterInfo.getAppInfo());

		if(!isMaster){

			masterSalveService.setMaster(masterInfo.getAppInfo());

		}
		appDao.setMasterAppInfo(masterInfo.getId());

		Map<String,AppInfo> appMap=appDao.getAllAppInfo();

		appMap.values().stream()
				.filter(info -> !info.getAppID().equals(appDao.getPortalAppID()))
				.forEach(info -> {

					masterSalveService.addSalveAppToMaster(masterInfo.getAppInfo(), info);

					String master = masterSalveService.checkMaster(info);
					if (master.equals(info.getAppID())) {
						log.info("salve create success:");
					}
				});


	}


}
