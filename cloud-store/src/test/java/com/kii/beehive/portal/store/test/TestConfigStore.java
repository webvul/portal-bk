package com.kii.beehive.portal.store.test;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.service.BeehiveConfigDao;
import com.kii.beehive.portal.store.entity.es.EsDataSourceCfgEntry;

public class TestConfigStore extends  TestTemplate {


	@Autowired
	private BeehiveConfigDao dao;


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
