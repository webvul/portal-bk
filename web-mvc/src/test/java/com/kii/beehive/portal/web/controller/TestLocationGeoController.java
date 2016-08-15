package com.kii.beehive.portal.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.kii.beehive.portal.exception.DuplicateException;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingGeoDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingGeo;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.entity.LocationGeoRestBean;
import com.kii.beehive.portal.web.entity.ThingRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Created by USER on 8/1/16.
 */
public class TestLocationGeoController extends WebTestTemplate {

	@Autowired
	private LocationGeoController locationGeoController;

	@Autowired
	private ThingController thingController;

	@Autowired
	private ThingGeoDao thingGeoDao;

	@Autowired
	private GlobalThingSpringDao globalThingSpringDao;

	private List<ThingGeo> testPOIData;

	@Before
	public void before() {

		testPOIData = new ArrayList<>();

		ThingGeo thingGeo;

		// on building "BuildingIDTest1", floor 8
		thingGeo = this.constructThingGeo(null,             null, 119.02845066671632, 31.27824347382616, 8,
				"BuildingIDTest1", "AliThingIDForTest1");
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest1", floor 8
		thingGeo = this.constructThingGeo(0l, "0807W-F02-15-301", 119.02845066671632, 31.27824347382616, 8,
				"BuildingIDTest1",                 null);
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest1", floor 8
		thingGeo = this.constructThingGeo(1l, "0807W-F02-15-302", 119.02845066671632, 31.27824347382616, 8,
				"BuildingIDTest1", "AliThingIDForTest2");
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest1", floor 8
		thingGeo = this.constructThingGeo(2l, "0807W-F02-15-303", 121.02845066671632, 31.27824347382616, 8,
				"BuildingIDTest1", "AliThingIDForTest3");
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest1", floor 8
		thingGeo = this.constructThingGeo(3l, "0807W-F02-15-304", 121.02845066671632, 29.27824347382616, 8,
				"BuildingIDTest1", "AliThingIDForTest4");
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest1", floor 8
		thingGeo = this.constructThingGeo(4l, "0807W-F02-15-305", 119.02845066671632, 29.27824347382616, 8,
				"BuildingIDTest1", "AliThingIDForTest5");
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest1", on floor 7
		thingGeo = this.constructThingGeo(5l, "0806W-F02-15-301", 119.02845066671632, 29.27824347382616, 7,
				"BuildingIDTest1", "AliThingIDForTest6");
		testPOIData.add(thingGeo);

		// on building "BuildingIDTest2", on floor 8
		thingGeo = this.constructThingGeo(6l, "0907W-F02-15-301", 119.02845066671632, 29.27824347382616, 8,
				"BuildingIDTest2", "AliThingIDForTest7");
		testPOIData.add(thingGeo);

	}

	private ThingGeo constructThingGeo(Long globalThingID, String vendorThingID, Double lng, Double lat, Integer
			floor, String buildingID, String aliThingID) {

		ThingGeo thingGeo = new ThingGeo();
		thingGeo.setGlobalThingID(globalThingID);
		thingGeo.setVendorThingID(vendorThingID);
		thingGeo.setLng(lng);
		thingGeo.setLat(lat);
		thingGeo.setFloor(floor);
		thingGeo.setBuildingID(buildingID);
		thingGeo.setAliThingID(aliThingID);

		return thingGeo;
	}

	private void createLocationGeo() {

		for (int i = 0; i < testPOIData.size(); i++) {

			ThingGeo thingGeo = testPOIData.get(i);
			LocationGeoRestBean restBean = new LocationGeoRestBean(thingGeo);

			ResponseEntity responseEntity = this.locationGeoController.saveLocationGeo(restBean);
			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

			Map<String, Long> map = (Map<String, Long>)responseEntity.getBody();
			Long id = map.get("id");
			assertTrue(id != null);

			ThingGeo temp = thingGeoDao.findByID(id);
			assertEquals(id, temp.getId());

			thingGeo.setId(id);
		}

	}

	private void deleteLocationGeo() {

		if(testPOIData == null) {
			return;
		}

		for (int i = 0; i < testPOIData.size(); i++) {

			ThingGeo thingGeo = testPOIData.get(i);

			Long id = thingGeo.getId();
			if(id == null) {
				continue;
			}

			ResponseEntity responseEntity = this.locationGeoController.deleteLocationGeo(id);
			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

			ThingGeo temp = thingGeoDao.findByID(id);
			assertNull(temp);

		}

	}

	@Test
	public void testCreateLocationGeo() {

		this.createLocationGeo();

	}

	@Test
	public void testSaveLocationGeo() {

		// test create locationGeo
		ThingGeo thingGeo = new ThingGeo();
		thingGeo.setGlobalThingID(99l);
		thingGeo.setVendorThingID("someVendorThingID");
		thingGeo.setLng(90d);
		thingGeo.setLat(91d);
		thingGeo.setFloor(10);
		thingGeo.setBuildingID("someBuildingID");
		thingGeo.setAliThingID("someAliThingID");
		thingGeo.setDescription("someDescription");
		thingGeo.setGeoType(1);

		LocationGeoRestBean restBean = new LocationGeoRestBean(thingGeo);

		this.locationGeoController.saveLocationGeo(restBean);

		ResponseEntity responseEntity = this.locationGeoController.getLocationGeoByBuildingID("someBuildingID");

		List<LocationGeoRestBean> response = (List<LocationGeoRestBean>)responseEntity.getBody();

		assertEquals(1, response.size());

		ThingGeo result = response.get(0).getThingGeo();

		// assert
		Long id = result.getId();
		assertTrue(id > 0);
		// no corresponding record in global_thing, so globalThingID in thing_geo will be set as null
		assertNull(result.getGlobalThingID());
		assertEquals(thingGeo.getVendorThingID(), result.getVendorThingID());
		assertEquals(thingGeo.getLng(), result.getLng());
		assertEquals(thingGeo.getLat(), result.getLat());
		assertEquals(thingGeo.getFloor(), result.getFloor());
		assertEquals(thingGeo.getBuildingID(), result.getBuildingID());
		assertEquals(thingGeo.getAliThingID(), result.getAliThingID());
		assertEquals(thingGeo.getDescription(), result.getDescription());
		assertEquals(thingGeo.getGeoType(), result.getGeoType());


		// test update locationGeo

		ThingGeo updateThingGeo = new ThingGeo();
		updateThingGeo.setId(id);
		updateThingGeo.setGlobalThingID(100l);
		updateThingGeo.setVendorThingID("anotherVendoThingID");
		updateThingGeo.setLng(100d);
		updateThingGeo.setDescription("anotherDescription");
		updateThingGeo.setGeoType(0);

		restBean = new LocationGeoRestBean(updateThingGeo);

		this.locationGeoController.saveLocationGeo(restBean);

		responseEntity = this.locationGeoController.getLocationGeoByBuildingID("someBuildingID");

		response = (List<LocationGeoRestBean>)responseEntity.getBody();

		assertEquals(1, response.size());

		result = response.get(0).getThingGeo();

		// assert
		assertEquals(id, result.getId());
		// no corresponding record in global_thing, so globalThingID in thing_geo will be set as null
		assertNull(result.getGlobalThingID());
		assertEquals(updateThingGeo.getVendorThingID(), result.getVendorThingID());
		assertEquals(updateThingGeo.getLng(), result.getLng());
		assertEquals(thingGeo.getLat(), result.getLat());
		assertEquals(thingGeo.getFloor(), result.getFloor());
		assertEquals(thingGeo.getBuildingID(), result.getBuildingID());
		assertEquals(thingGeo.getAliThingID(), result.getAliThingID());
		assertEquals(updateThingGeo.getDescription(), result.getDescription());
		assertEquals(updateThingGeo.getGeoType(), result.getGeoType());
	}

	@Test
	public void testCreateLocationGeoException() {

		// create POI points
		this.createLocationGeo();

		// test invalid input, no lng and lat
		ThingGeo thingGeo = new ThingGeo();
		thingGeo.setGlobalThingID(10l);
		thingGeo.setVendorThingID("1007W-F02-15-301");
		thingGeo.setAliThingID("AliThingIDForTest10");
		thingGeo.setBuildingID("BuildingIDTest9");

		try {
			this.locationGeoController.saveLocationGeo(new LocationGeoRestBean(thingGeo));
			fail();
		} catch (PortalException e) {
			e.printStackTrace();
			assertEquals(ErrorCode.REQUIRED_FIELDS_MISSING.getName(), e.getErrorCode());
		}

		// test invalid input, no globalThingID and vendorThingID and aliThingID
		thingGeo = new ThingGeo();
		thingGeo.setLng(119.02845066671632);
		thingGeo.setLat(29.27824347382616);
		thingGeo.setBuildingID("BuildingIDTest9");

		try {
			this.locationGeoController.saveLocationGeo(new LocationGeoRestBean(thingGeo));
			fail();
		} catch (PortalException e) {
			e.printStackTrace();
			assertEquals(ErrorCode.REQUIRED_FIELDS_MISSING.getName(), e.getErrorCode());
		}

		// test duplicated globalThing and vendorThingID
		thingGeo = this.testPOIData.get(2);
		thingGeo.setId(null);
		thingGeo.setAliThingID("newAliThingID");
		try {
			this.locationGeoController.saveLocationGeo(new LocationGeoRestBean(thingGeo));
			fail();
		} catch (DuplicateException e) {
			e.printStackTrace();
		}

		// test duplicated aliThingID
		thingGeo = this.testPOIData.get(3);
		thingGeo.setId(null);
		thingGeo.setGlobalThingID(11l);
		thingGeo.setVendorThingID("newVendorThingID");
		try {
			this.locationGeoController.saveLocationGeo(new LocationGeoRestBean(thingGeo));
			fail();
		} catch (DuplicateException e) {
			e.printStackTrace();
		}


	}

	@Test
	public void testDeleteLocationGeo() {

		// create POI points
		this.createLocationGeo();

		this.deleteLocationGeo();

		// delete the non existing id 100
		ResponseEntity responseEntity = this.locationGeoController.deleteLocationGeo(100l);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	}

	@Test
	public void testGetLocationGeoByFloor() {

		// create POI points
		this.createLocationGeo();

		ResponseEntity responseEntity = this.locationGeoController.getLocationGeoByFloor("BuildingIDTest1", 8);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<LocationGeoRestBean> response = (List<LocationGeoRestBean>)responseEntity.getBody();

		assertEquals(6, response.size());

		response.forEach(e -> {
			ThingGeo thingGeo = e.getThingGeo();
			assertEquals("BuildingIDTest1", thingGeo.getBuildingID());
			assertEquals(8, (int)thingGeo.getFloor());
		});

	}


	@Test
	public void testGetLocationGeoByBuildingID() {

		// create POI points
		this.createLocationGeo();

		ResponseEntity responseEntity = this.locationGeoController.getLocationGeoByBuildingID("BuildingIDTest1");

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<LocationGeoRestBean> response = (List<LocationGeoRestBean>)responseEntity.getBody();

		assertEquals(7, response.size());

		response.forEach(e -> {
			ThingGeo thingGeo = e.getThingGeo();
			assertEquals("BuildingIDTest1", thingGeo.getBuildingID());
		});
	}


	@Test
	public void testSearchCircle() {

		// create POI points
		this.createLocationGeo();

		Map<String, Object> searchMap = new HashMap<>();
		searchMap.put("floor", 8);
		searchMap.put("buildingID", "BuildingIDTest1");
		searchMap.put("centricLng", 121.02845066671632);
		searchMap.put("centricLat", 31.27824347382616);
		searchMap.put("radius", 200000);

		ResponseEntity responseEntity = this.locationGeoController.searchCircle(searchMap);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<LocationGeoRestBean> response = (List<LocationGeoRestBean>)responseEntity.getBody();

		response.forEach(e -> System.out.println(e));

		assertEquals(4, response.size());

		response.forEach(e -> {
			Long id = e.getThingGeo().getGlobalThingID();
			assertTrue(id == null || id == 0 || id == 1 || id ==2);
		});

	}


	@Test
	public void testSearchPolygon() {

		// create POI points
		this.createLocationGeo();

		Map<String, Object> searchMap = new HashMap<>();
		searchMap.put("floor", 8);
		searchMap.put("buildingID", "BuildingIDTest1");

		List<Map<String, Double>> scope = new ArrayList<>();
		searchMap.put("scope", scope);

		Map<String, Double> point = new HashMap<>();
		point.put("lng", 119d);
		point.put("lat", 31d);
		scope.add(point);

		point = new HashMap<>();
		point.put("lng", 121d);
		point.put("lat", 31d);
		scope.add(point);

		point = new HashMap<>();
		point.put("lng", 122d);
		point.put("lat", 29d);
		scope.add(point);

		point = new HashMap<>();
		point.put("lng", 119d);
		point.put("lat", 29d);
		scope.add(point);

		ResponseEntity responseEntity = this.locationGeoController.searchPolygon(searchMap);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<LocationGeoRestBean> response = (List<LocationGeoRestBean>)responseEntity.getBody();

		response.forEach(e -> System.out.println(e));

		assertEquals(2, response.size());

		response.forEach(e -> {
			String temp = e.getThingGeo().getVendorThingID();
			assertTrue("0807W-F02-15-304".equals(temp) || "0807W-F02-15-305".equals(temp));
		});

	}

	private GlobalThingInfo createGlobalThing(String vendorThingID) {
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID(vendorThingID);
		thingInfo.setKiiAppID("c1744915");
		long id = this.globalThingSpringDao.saveOrUpdate(thingInfo);
		thingInfo.setId(id);
		return thingInfo;
	}

	/**
	 * the sync target of location geo is all the POI points on building "BuildingIDTest1" and floor 8,
	 * as there are only below thing info in table global_thing, only the globalThingID in table thing_geo of below
	 * first 3 will be updated
	 * - "0807W-F02-15-301"
	 * - "0807W-F02-15-302"
	 * - "0807W-F02-15-303"
	 * - "0907W-F02-15-301" (not on floor 8)
	 */
	@Test
	public void testSyncLocationGeoByBuildingIDAndFloor() {

		// create POI points
		this.createLocationGeo();

		// create thing
		Long globalThingID1 = this.createGlobalThing("0807W-F02-15-301").getId();
		Long globalThingID2 = this.createGlobalThing("0807W-F02-15-302").getId();
		Long globalThingID3 = this.createGlobalThing("0807W-F02-15-303").getId();
		Long globalThingID4 = this.createGlobalThing("0907W-F02-15-301").getId();

		ResponseEntity responseEntity = this.locationGeoController.syncLocationGeoByFloor("BuildingIDTest1", 8);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<LocationGeoRestBean> response = (List<LocationGeoRestBean>)responseEntity.getBody();

		// assert
		assertEquals(6, response.size());

		response.forEach(e -> {
			ThingGeo thingGeo = e.getThingGeo();
			assertEquals("BuildingIDTest1", thingGeo.getBuildingID());
			assertEquals(8, (int)thingGeo.getFloor());

			// assert each thing geo
			if("AliThingIDForTest1".equals(thingGeo.getAliThingID())) {
				assertNull(thingGeo.getGlobalThingID());
				assertNull(thingGeo.getVendorThingID());
			}

			if("0807W-F02-15-301".equals(thingGeo.getVendorThingID())) {
				assertEquals(globalThingID1, thingGeo.getGlobalThingID());
			}

			if("0807W-F02-15-302".equals(thingGeo.getVendorThingID())) {
				assertEquals(globalThingID2, thingGeo.getGlobalThingID());
			}

			if("0807W-F02-15-303".equals(thingGeo.getVendorThingID())) {
				assertEquals(globalThingID3, thingGeo.getGlobalThingID());
			}

			if("0807W-F02-15-304".equals(thingGeo.getVendorThingID())) {
				assertNull(thingGeo.getGlobalThingID());
			}

			if("0807W-F02-15-305".equals(thingGeo.getVendorThingID())) {
				assertNull(thingGeo.getGlobalThingID());
			}

		});

		List<ThingGeo> list = thingGeoDao.findByBuildingIDAndFloor("BuildingIDTest1", 7);
		assertTrue(list.size() == 1);

		ThingGeo thingGeo = list.get(0);
		// no corresponding record in global_thing, so globalThingID in thing_geo will be set as null
		assertNull(thingGeo.getGlobalThingID());
		assertEquals("0806W-F02-15-301", thingGeo.getVendorThingID());

		list = thingGeoDao.findByBuildingIDAndFloor("BuildingIDTest2", 8);
		assertTrue(list.size() == 1);

		thingGeo = list.get(0);
		// no corresponding record in global_thing, so globalThingID in thing_geo will be set as null
		assertNull(thingGeo.getGlobalThingID());
		assertEquals("0907W-F02-15-301", thingGeo.getVendorThingID());


	}

	/**
	 * below cases will be tested:
	 * 1. create POI info in thing_geo with vendorThingID,
	 * 		a. if vendorThingID not existing in global_thing, globalThingID in thing_geo will be null
	 * 		b. if vendorThingID existing in global_thing, globalThingID in thing_geo will be synchronized
	 * 2. create POI info in thing_geo without vendorThingID, both vendorThingID and globalThingID in thing_geo will
	 * be null
	 * 3. create thing info in global_thing, if vendorThingID existing in thing_geo, globalThingID in thing_geo will
	 * be synchronized
	 *
	 */
	@Test
	public void testSyncGlobalThingIDByVendorThingID() {

		// 1. create POI info in thing_geo with vendorThingID,
		//		a. if vendorThingID not existing in global_thing, globalThingID in thing_geo will be null

		String vendorThingIDForTest = "0807W-F02-03-118";

		ThingGeo thingGeo = this.constructThingGeo(null, vendorThingIDForTest, 0d, 0d, 8, "someBuildingID",
				"someAliThingID");

		LocationGeoRestBean restBean = new LocationGeoRestBean(thingGeo);
		this.locationGeoController.saveLocationGeo(restBean);

		ThingGeo temp = thingGeoDao.findBySingleField(ThingGeo.VENDOR_THING_ID, vendorThingIDForTest).get(0);
		assertNull(temp.getGlobalThingID());

		thingGeoDao.hardDeleteByID(temp.getId());


		// 1. create POI info in thing_geo with vendorThingID,
		//		b. if vendorThingID existing in global_thing, globalThingID in thing_geo will be synchronized

		GlobalThingInfo thingInfo = this.createGlobalThing(vendorThingIDForTest);

		restBean = new LocationGeoRestBean(thingGeo);
		this.locationGeoController.saveLocationGeo(restBean);

		temp = thingGeoDao.findBySingleField(ThingGeo.VENDOR_THING_ID, vendorThingIDForTest).get(0);
		assertEquals(thingInfo.getId(), temp.getGlobalThingID());

		thingGeoDao.hardDeleteByID(temp.getId());
		globalThingSpringDao.hardDeleteByID(thingInfo.getId());


		// 2. create POI info in thing_geo without vendorThingID, both vendorThingID and globalThingID in thing_geo
		// be null
		thingGeo = this.constructThingGeo(null, null, 0d, 0d, 8, "someBuildingID", "someAliThingID");
		restBean = new LocationGeoRestBean(thingGeo);
		this.locationGeoController.saveLocationGeo(restBean);

		temp = thingGeoDao.findBySingleField(ThingGeo.ALI_THING_ID, "someAliThingID").get(0);
		assertNull(temp.getGlobalThingID());
		assertNull(temp.getVendorThingID());

		thingGeoDao.hardDeleteByID(temp.getId());


		// 3. create thing info in global_thing, if vendorThingID existing in thing_geo, globalThingID in thing_geo
		// will be synchronized
		thingGeo = this.constructThingGeo(null, vendorThingIDForTest, 0d, 0d, 8, "someBuildingID",
				"someAliThingID");

		restBean = new LocationGeoRestBean(thingGeo);
		this.locationGeoController.saveLocationGeo(restBean);

		temp = thingGeoDao.findBySingleField(ThingGeo.VENDOR_THING_ID, vendorThingIDForTest).get(0);
		assertNull(temp.getGlobalThingID());

		thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID(vendorThingIDForTest);
		thingInfo.setKiiAppID("c1744915");
		thingInfo.setSchemaName("schemaName");
		thingInfo.setSchemaVersion("schemaVersion");
		ThingRestBean thingRestBean = new ThingRestBean(thingInfo);
		Map<String, Long> response = this.thingController.createThing(thingRestBean);

		temp = thingGeoDao.findBySingleField(ThingGeo.VENDOR_THING_ID, vendorThingIDForTest).get(0);
		assertEquals(response.get("globalThingID"), temp.getGlobalThingID());


	}



}
