package com.kii.beehive.portal.helper;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.context.AppBindTool;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.service.AppMasterSalveService;
import com.kii.extension.sdk.service.DevPortalService;

@Component
public class AppInfoService implements AppBindTool {

	private Logger log= LoggerFactory.getLogger(AppInfoService.class);

	public static final String ZERO_FILL = "000000000000000000";
	public static final String FF_FILL = "FFFFFFFFFFFFFFFFFF";

	TreeMap<String,String> prefixMap=new TreeMap<>();

	Map<String,AppInfo> appMap=new HashMap<>();

	Map<String,String>  idsMap=new HashMap<>();

	Map<String,String> nameMap=new HashMap<>();

	Map<String,String> defaultThingOwnerIDMap=new HashMap<>();

	AppInfo masterApp=null;

	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private DevPortalService  service;

	@Autowired
	private AppMasterSalveService  masterSalveService;


	@PostConstruct
	public  void init(){


		List<KiiAppInfo> appList=appInfoDao.getAllAppInfo();

		appList.forEach(this::appInfoBeenAdded);

	}


	public void setMasterSalve(String masterName){


		AppInfo masterInfo=getAppInfo(masterName);

		boolean isMaster=masterSalveService.isMaster(masterInfo);

		if(!isMaster){

			masterSalveService.setMaster(masterInfo);

		}
		appInfoDao.setMasterAppInfo(masterInfo.getAppID());


		appMap.values().stream()
				.filter(info -> !info.getAppID().equals(appInfoDao.getPortalAppID()))
				.forEach(info -> {

					masterSalveService.addSalveAppToMaster(masterInfo, info);

					String master = masterSalveService.checkMaster(info);
					if(master.equals(info.getAppID())) {
						log.info("salve create success:");
					}
				});


	}

	public void initDataWithDevPortal(String userName,String pwd){

		service.login(userName,pwd);

		List<AppInfo>  appInfoList=service.getAppInfoList();
		appInfoList.forEach((app)->{

			if(app.getAppID().equals(appInfoDao.getPortalAppID())){
				return;
			}

			KiiAppInfo info=new KiiAppInfo();
			info.setAppInfo(app);

			info.setAppName(app.getName());
			info.setId(info.getId());

			appInfoDao.addAppInfo(info);

		});

	}


	private  void appInfoBeenAdded(KiiAppInfo kiiApp) {


		String appID=kiiApp.getAppInfo().getAppID();

		appMap.put(appID, kiiApp.getAppInfo());

		nameMap.put(kiiApp.getAppName(),appID);

		defaultThingOwnerIDMap.put(appID, kiiApp.getDefaultThingOwnerID());

		if(kiiApp.getThingIDPrefix()!=null) {

			String lower= (kiiApp.getThingIDPrefix()+ ZERO_FILL).substring(0,12);
			String upper= (kiiApp.getThingIDPrefix()+ FF_FILL).substring(0,12);

			prefixMap.put(lower, appID);
			prefixMap.put(upper, appID);
		}

		for(String id:kiiApp.getRelThingIDs()){
			idsMap.put(id,appID);
		}

		if(kiiApp.getMasterApp()){
			masterApp=kiiApp.getAppInfo();
		}
	}

	private  void appInfoBeenRemoved(KiiAppInfo kiiApp) {

		String kiiAppID = kiiApp.getAppInfo().getAppID();

		appMap.remove(kiiAppID);

		defaultThingOwnerIDMap.remove(kiiAppID);

		if(kiiApp.getThingIDPrefix()!=null) {

			String lower= (kiiApp.getThingIDPrefix()+ ZERO_FILL).substring(0,12);
			String upper= (kiiApp.getThingIDPrefix()+ FF_FILL).substring(0,12);

			prefixMap.remove(lower);
			prefixMap.remove(upper);
		}

		kiiApp.getRelThingIDs().forEach(idsMap::remove);
	}



	public  void addAppInfo(KiiAppInfo appInfo){

		appInfoDao.addAppInfo(appInfo);

		synchronized (this) {
			appInfoBeenAdded(appInfo);
		}
	}


	public   void addAppInfos(List<KiiAppInfo> appInfos){


		appInfos.forEach(a -> appInfoDao.addAppInfo(a));

		synchronized (this) {
			appInfos.forEach(this::appInfoBeenAdded);
		}
	}

	public   void removeAppInfos(String appID){

		KiiAppInfo  info=appInfoDao.removeInfo(appID);

		synchronized (this) {
			appInfoBeenRemoved(info);
		}
	}


	public AppInfo getMatchAppInfoByThing(String vendorThingID){

		String appID=idsMap.get(vendorThingID);
		if(appID!=null){

			return appMap.get(appID);
		}


		String upper=prefixMap.higherKey(vendorThingID);
		String lower=prefixMap.lowerKey(vendorThingID);

		if(upper.equals(lower)) {
			return appMap.get(lower);
		}

		return null;
	}


	public  AppInfo getAppInfoByID(String appID){

		return appMap.get(appID);
	}

	@Override
	public AppInfo getDefaultAppInfo() {
		return null;
	}


	@Override
	public  AppInfo getAppInfo(String name){

		if(name.equals("master")){
			return masterApp;
		}else {

			return appMap.get(nameMap.get(name));
		}
	};

	public String getDefaultThingOwnerID(String kiiAppID) {
		return defaultThingOwnerIDMap.get(kiiAppID);
	}

}
