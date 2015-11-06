package com.kii.beehive.portal.service;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class AppInfoDao extends AbstractDataAccess<KiiAppInfo> {


	public static final String ZERO_FILL = "000000000000000000";
	public static final String FF_FILL = "FFFFFFFFFFFFFFFFFF";
	TreeMap<String,String> prefixMap=new TreeMap<>();

	Map<String,AppInfo> appMap=new HashMap<>();

	Map<String,String>  idsMap=new HashMap<>();

	AppInfo defaultApp;


	@PostConstruct
	public void init(){


		List<KiiAppInfo>  appList=super.fullQuery(ConditionBuilder.getAll().getFinalCondition().build());


		for(KiiAppInfo kiiApp:appList){

			if(kiiApp.isDefaultApp()){
				defaultApp=kiiApp.getAppInfo();
			}
			appInfoBeenAdded(kiiApp);

		}

		if(defaultApp==null&&appList.size()>0){
			defaultApp=appList.get(0).getAppInfo();
		}

	}

	private  void appInfoBeenAdded(KiiAppInfo kiiApp) {
		String appID=kiiApp.getAppInfo().getAppID();

		appMap.put(appID, kiiApp.getAppInfo());

		if(kiiApp.getThingIDPrefix()!=null) {

			String lower= (kiiApp.getThingIDPrefix()+ ZERO_FILL).substring(0,12);
			String upper= (kiiApp.getThingIDPrefix()+ FF_FILL).substring(0,12);

			prefixMap.put(lower, appID);
			prefixMap.put(upper, appID);
		}

		for(String id:kiiApp.getRelThingIDs()){
			idsMap.put(id,appID);
		}
	}

	private  void appInfoBeenRemoved(KiiAppInfo kiiApp) {

		appMap.remove(kiiApp.getAppInfo().getAppID());

		if(kiiApp.getThingIDPrefix()!=null) {

			String lower= (kiiApp.getThingIDPrefix()+ ZERO_FILL).substring(0,12);
			String upper= (kiiApp.getThingIDPrefix()+ FF_FILL).substring(0,12);

			prefixMap.remove(lower);
			prefixMap.remove(upper);
		}

		kiiApp.getRelThingIDs().forEach(idsMap::remove);
	}

	public  void addAppInfo(KiiAppInfo appInfo){

		super.addEntity(appInfo, appInfo.getAppInfo().getAppID());

		synchronized (this) {
			appInfoBeenAdded(appInfo);
		}
	}


	public   void addAppInfos(List<KiiAppInfo> appInfos){


		appInfos.forEach(a -> addEntity(a, a.getAppInfo().getAppID()));

		synchronized (this) {
			appInfos.forEach(this::appInfoBeenAdded);
		}
	}

	public   void removeAppInfos(String appID){


		KiiAppInfo appInfo=this.getObjectByID(appID);

		this.removeEntity(appID);

		synchronized (this) {
			appInfoBeenRemoved(appInfo);
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

		return defaultApp;
	}

	public  AppInfo getAppInfo(String appID){

		return appMap.get(appID);
	};


	@Override
	protected Class<KiiAppInfo> getTypeCls() {
		return KiiAppInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("KiiAppInfoStore");
	}
}
