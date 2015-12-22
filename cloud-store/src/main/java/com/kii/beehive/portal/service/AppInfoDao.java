package com.kii.beehive.portal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool" )
@Component
public class AppInfoDao extends AbstractDataAccess<KiiAppInfo> {


	public static final String APP_LIST_CACHE="'app_list'";

	public static final String MASTER_CACHE="'master'";


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

	@CacheEvict(cacheNames = CacheConfig.LONGLIVE_CACHE,key=APP_LIST_CACHE)
	@CachePut(cacheNames={CacheConfig.LONGLIVE_CACHE},key="#appInfo.id")
	public void addAppInfo(KiiAppInfo appInfo) {

		super.addEntity(appInfo, appInfo.getAppInfo().getAppID());
	}

	@Cacheable(cacheNames={CacheConfig.LONGLIVE_CACHE})
	public KiiAppInfo getAppInfoByID(String id){
		try {
			return super.getObjectByID(id);
		}catch(ObjectNotFoundException e){
			return null;
		}
	}


	@Cacheable(cacheNames={CacheConfig.LONGLIVE_CACHE},key=APP_LIST_CACHE)
	public List<AppInfo> getSalveAppList(){

		QueryParam query=ConditionBuilder.notCondition().equal("masterApp",true).getFinalQueryParam();

		List<KiiAppInfo> infoList=super.query(query);

		return infoList.stream().map((kiiApp)->kiiApp.getAppInfo()).collect(Collectors.toList());
	}

	@Cacheable(cacheNames= CacheConfig.LONGLIVE_CACHE,key=MASTER_CACHE)
	public KiiAppInfo getMasterAppInfo(){


		List<KiiAppInfo>  infoList=super.query(ConditionBuilder.newCondition().equal("masterApp", true).getFinalCondition().build());
		if(infoList.size()==0){
			return null;
		}else{
			return infoList.get(0);
		}

	}

	@CacheEvict(cacheNames=CacheConfig.LONGLIVE_CACHE,key=MASTER_CACHE)
	public void setMasterAppInfo(String id){


		KiiAppInfo info=new KiiAppInfo();
		info.setMasterApp(true);

		super.updateEntity(info, id);

	}



}