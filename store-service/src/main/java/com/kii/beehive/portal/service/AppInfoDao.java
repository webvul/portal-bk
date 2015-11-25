package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool" )
@Component
public class AppInfoDao extends AbstractDataAccess<KiiAppInfo> {



	@Autowired
	private AppBindToolResolver resolver;



	@Override
	protected Class<KiiAppInfo> getTypeCls() {
		return KiiAppInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("KiiAppInfoStore");
	}


	public String getPortalAppID(){
		return resolver.getAppInfo().getAppID();
	}
//
	@Cacheable(cacheNames="appInfo-map",key="'map'")
	public Map<String,AppInfo> getAllAppInfo() {


		List<KiiAppInfo>  list=super.getAll();

		Map<String,AppInfo> map=new HashMap<>();

		list.forEach((app)->map.put(app.getAppName(),app.getAppInfo()));

		return map;
	}

	@CacheEvict(cacheNames={"appInfo-map"},key="'map'")
	@CachePut(cacheNames={"appInfo"},key="#appInfo.appName")
	public void addAppInfo(KiiAppInfo appInfo) {

		super.addEntity(appInfo, appInfo.getAppInfo().getAppID());
	}

	@Cacheable(cacheNames={"appInfo"})
	public KiiAppInfo getAppInfoByName(String appName){
		List<KiiAppInfo>  infoList=super.query(ConditionBuilder.newCondition().equal("appName", appName).getFinalCondition().build());

		if(infoList.size()==0){
			return null;
		}else{
			return infoList.get(0);
		}

	}

	@Cacheable(cacheNames="appInfo",key="'master-app'")
	public KiiAppInfo getMasterAppInfo(){


		List<KiiAppInfo>  infoList=super.query(ConditionBuilder.newCondition().equal("masterApp", true).getFinalCondition().build());
		if(infoList.size()==0){
			return null;
		}else{
			return infoList.get(0);
		}

	}

	@CacheEvict(cacheNames="appInfo",key="'master-app'")
	public void setMasterAppInfo(String id){


		KiiAppInfo info=new KiiAppInfo();
		info.setMasterApp(true);

		super.updateEntity(info, id);

	}



}
