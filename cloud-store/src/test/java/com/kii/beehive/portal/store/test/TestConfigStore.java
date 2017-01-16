package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.beehive.portal.service.UserRuleDao;
import com.kii.beehive.portal.store.entity.KiiAppInfo;
import com.kii.beehive.portal.store.entity.es.EsDataSourceCfgEntry;
import com.kii.extension.sdk.entity.AppInfo;

public class TestConfigStore extends  TestTemplate {


	@Autowired
	private BeehiveConfigDao dao;

	@Autowired
	private AppInfoDao storeDao;

	@Autowired
	private UserRuleDao ruleDao;
	
	


	@Test
	public void setNewApp(){

		KiiAppInfo appInfo=new KiiAppInfo();

		AppInfo app=new AppInfo();
		app.setAppID("192b49ce");
		appInfo.setAppInfo(app);


		storeDao.addAppInfo(appInfo);

	}

	@Test
	public void addConfig(){
		
		EsDataSourceCfgEntry entry=new EsDataSourceCfgEntry();

		entry.setBizDataCommonCarId("source");
		entry.setBizDataGatewayIndex("4e47ffb1-0be8-4792-91f2-673be1626b57");
		entry.setBizDataGatewayIndexTypeLeave("CarOut");
		entry.setBizDataParkingSpaceIndex("74e58d2e-cb1f-4ada-b15e-49aea780f664");
		entry.setBizDataParkingSpaceIndexTypeLeave("CarOut");
		entry.setBizDataCommonEventTime("object.eventTime");


		dao.saveConfigEntry(entry);

		entry=dao.getEsConfig();

		assertEquals(entry.getBizDataCommonCarId(),"source");


	}
}
