package com.kii.beehive.portal.service;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class AppInfoDao extends AbstractDataAccess<KiiAppInfo> {



	TreeMap<String,String> prefixMap=new TreeMap<>();

	Map<String,AppInfo> appMap=new HashMap<>();


	@PostConstruct
	public void init(){

		List<KiiAppInfo>  appList=super.fullQuery(ConditionBuilder.getAll().getFinalCondition().build());

		for(KiiAppInfo kiiApp:appList){

			appMap.put(kiiApp.getAppName(),kiiApp.getAppInfo());

		}

	}


	public AppInfo getMatchAppInfoByThing(String vendorThingID){

		ConditionBuilder condEq=ConditionBuilder.newCondition().equal("thingIDs." + vendorThingID, true);

		ConditionBuilder condIn=ConditionBuilder.andCondition().lessAndEq("thingIDUpper", vendorThingID).greatAndEq("thingIDLower", vendorThingID);

		QueryParam query= ConditionBuilder.orCondition().addSubClause(condEq,condIn).getFinalCondition().build();

		List<AppInfo> appInfos=super.query(query);

		if(appInfos.size()==0){
			return null;
		}else{
			return appInfos.get(0);
		}
	}

	@Override
	protected Class<KiiAppInfo> getTypeCls() {
		return KiiAppInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("KiiAppInfoStore");
	}
}
