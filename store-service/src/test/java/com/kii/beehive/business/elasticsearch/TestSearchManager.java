package com.kii.beehive.business.elasticsearch;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by user on 15/6/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:com/kii/beehive/portal/store/testElasricsearchContext.xml"})
public class TestSearchManager {

	@Autowired
	private SearchManager manager;

	@Test
	public void testAggs() throws IOException {
		GlobalThingInfo thing = new GlobalThingInfo();
		thing.setKiiAppID("8bb9d4af");
		String kiiThingID = "thing:th.aba700e36100-37c9-6e11-ecd3-04401dbe";
		long startDate = 1467245555344L;
		long endDate = 1467355655344L;
		String operatorField = "avg";
		String intervalField = "1m";
		String[] avgFields = new String[]{"state.humidiy", "state.temprature"};
		String queryString = manager.queryBuilderForAggs(kiiThingID, startDate, endDate, intervalField, operatorField,
				avgFields);
		System.out.println(queryString);
		System.out.println(manager.extractResultForAggs(manager.search(thing, queryString)));
	}

	@Test
	public void testHistorical() throws IOException {
		GlobalThingInfo thing = new GlobalThingInfo();
		thing.setVendorThingID("100");
		thing.setKiiAppID("8bb9d4af");
		long startDate = 1462865748745L;
		long endDate = 1463484501724L;
		int from = 0;
		int size = 1;
		String queryString = manager.queryBuilderForHistorical(thing.getVendorThingID(), startDate, endDate, size, from);
		System.out.println(queryString);
		System.out.println(manager.extractResultForHistorical(manager.search(thing, queryString)));
	}
}
