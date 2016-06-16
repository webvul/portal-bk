package com.kii.beehive.business.elasticsearch;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by user on 15/6/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:com/kii/beehive/portal/store/testElasricsearchContext.xml"})
public class TestSearchManager {

	@Autowired
	private SearchManager manager;

	@Test
	public void testSearch() {
		GlobalThingInfo thing = new GlobalThingInfo();
		thing.setVendorThingID("100");
		thing.setKiiAppID("f3a8dd68");
		long startDate = 1462865748745L;
		long endDate = 1463484501724L;
		String operatorField = "avg";
		String intervalField = "1m";
		String[] avgFields = new String[]{"humidiy", "temprature"};
		String queryString = manager.queryBuilder(thing.getVendorThingID(), startDate, endDate, intervalField, operatorField,
				avgFields);
		System.out.println(queryString);
		System.out.println(manager.search(thing, queryString));
	}
}
