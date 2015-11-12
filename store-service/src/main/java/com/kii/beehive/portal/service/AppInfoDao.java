package com.kii.beehive.portal.service;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class AppInfoDao extends AbstractDataAccess<KiiAppInfo> {





	@Override
	protected Class<KiiAppInfo> getTypeCls() {
		return KiiAppInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("KiiAppInfoStore");
	}
	
	public List<KiiAppInfo> getAllAppInfo() {
		return super.getAll();
	}

	public void addAppInfo(KiiAppInfo appInfo) {

		super.addEntity(appInfo, appInfo.getAppInfo().getAppID());
	}

	public KiiAppInfo removeInfo(String id){

		KiiAppInfo info=super.getObjectByID(id);

		super.removeEntity(id);

		return info;
	}

	public KiiAppInfo getMasterAppInfo(){


		List<KiiAppInfo>  infoList=super.query(ConditionBuilder.newCondition().equal("master", true).getFinalCondition().build());
		if(infoList.size()==0){
			return null;
		}else{
			return infoList.get(0);
		}

	}

	public void setMasterAppInfo(String id){


		KiiAppInfo info=new KiiAppInfo();
		info.setIsMasterApp(true);

		super.updateEntity(info,id);

	}
	

}
