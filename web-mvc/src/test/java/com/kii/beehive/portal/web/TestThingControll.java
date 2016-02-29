package com.kii.beehive.portal.web;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.portal.web.constant.Constants;

public class TestThingControll extends WebTestTemplate{

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private GlobalThingDao globalThingDao;

	@Autowired
	private TagThingRelationDao tagThingRelationDao;

	@Autowired
	private ObjectMapper mapper;

	private final static String KII_APP_ID = "0af7a7e7";

	private final static String KII_APP_ID_NEW = "c1744915";

	private final static String MASTER_KII_APP_ID = "da0b6a25";

	private Long globalThingIDForTest;

	private String[] vendorThingIDsForTest = new String[]{"someVendorThingID", "someVendorThingID-new"};

	private List<Long> globalThingIDListForTets = new ArrayList<>();

	private String[] displayNames = new String[]{"A", "B"};

	private String tokenForTest = "Bearer " + Constants.SUPER_TOKEN;

	@Before
	public void before() {
		super.before();

		for(String displayName : displayNames) {
			TagIndex tagIndex = new TagIndex();
			tagIndex.setTagType(TagType.Custom);
			tagIndex.setDisplayName(displayName);

			tagIndexDao.saveOrUpdate(tagIndex);
		}
	}

	@Test
	public void testCreatThing() throws Exception {

		// create without tag and custom field
		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		String ctx= mapper.writeValueAsString(request);

		String result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);

		// assert http reture
		globalThingIDForTest = Long.valueOf((int)map.get("globalThingID"));
		assertNotNull(globalThingIDForTest);
		assertTrue(globalThingIDForTest > 0);

	}

	@Test
	public void testCreatThingWithoutLocation() throws Exception {

		// create without tag and custom field
		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");

		String ctx= mapper.writeValueAsString(request);

		String result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);

		// assert http reture
		globalThingIDForTest = Long.valueOf((int)map.get("globalThingID"));
		assertNotNull(globalThingIDForTest);
		assertTrue(globalThingIDForTest > 0);

		// query thing
		result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map=mapper.readValue(result, Map.class);

		System.out.println(map);

		assertEquals(globalThingIDForTest, Long.valueOf((Integer)map.get("globalThingID")));
		assertEquals(TagThingManager.DEFAULT_LOCATION, map.get("location"));

	}

	@Test
	public void testCreatThingWithTagAndCustomFields() throws Exception {

		// create without tag and custom field
		Map<String, Object> request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		// set status
		Map<String, Object> status = new HashMap<>();
		status.put("brightness", "80");
		status.put("temperature", "90");
		request.put("status", status);

		// set custom
		Map<String, Object> custom = new HashMap<>();
		custom.put("license", "123qwerty");
		custom.put("produceDate", "20001230");
		request.put("custom", custom);

		String ctx= mapper.writeValueAsString(request);

		String result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);

		// assert http return
		globalThingIDForTest = Long.valueOf((int)map.get("globalThingID"));
		assertNotNull(globalThingIDForTest);
		assertTrue(globalThingIDForTest > 0);

		GlobalThingInfo thingInfo = globalThingDao.getThingByVendorThingID(vendorThingIDsForTest[0]);
		assertTrue(thingInfo != null);

		// test update
		request = new HashMap<>();
		request.put("globalThingID", globalThingIDForTest);
		request.put("vendorThingID", vendorThingIDsForTest[1]);
		request.put("kiiAppID", KII_APP_ID_NEW);
		request.put("type", "some_type_new");
		request.put("location", "some_location_new");

		// set status
		status = new HashMap<>();
		status.put("brightness", "90");
		status.put("color", "#123456");
		request.put("status", status);

		// set custom
		custom = new HashMap<>();
		custom.put("time-zone", "GMT+8");
		custom.put("produceDate", "20991230");
		request.put("custom", custom);

		ctx= mapper.writeValueAsString(request);

		result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map=mapper.readValue(result, Map.class);

		// assert http reture
		Long globalThingIDForTest = Long.valueOf((int)map.get("globalThingID"));
		assertEquals(this.globalThingIDForTest, globalThingIDForTest);

		// assert
		thingInfo = globalThingDao.getThingByVendorThingID(vendorThingIDsForTest[0]);
		assertTrue(thingInfo == null);

		thingInfo = globalThingDao.getThingByVendorThingID(vendorThingIDsForTest[1]);
		assertTrue(thingInfo != null);

	}

	@Test
	public void testCreatThingException() throws Exception {

		// no vendorThingID
		Map<String, Object> request = new HashMap<>();
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		String ctx= mapper.writeValueAsString(request);

		String result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

		// invalid vendorThingID
		request = new HashMap<>();
		request.put("vendorThingID", "qwe.rty");
		request.put("kiiAppID", KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		ctx= mapper.writeValueAsString(request);

		result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

		// no kiiAppID
		request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("type", "some type");
		request.put("location", "some location");

		ctx= mapper.writeValueAsString(request);

		result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();

		// invalid kiiAppID
		request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", "some_non_existing_kii_app_id");
		request.put("type", "some type");
		request.put("location", "some location");

		ctx= mapper.writeValueAsString(request);

		result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

		// master kiiAppID
		request = new HashMap<>();
		request.put("vendorThingID", vendorThingIDsForTest[0]);
		request.put("kiiAppID", MASTER_KII_APP_ID);
		request.put("type", "some type");
		request.put("location", "some location");

		ctx= mapper.writeValueAsString(request);

		result=this.mockMvc.perform(
				post("/things").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();



	}

	@Test
	public void testGetThingByGlobalID() throws Exception {

		this.testCreatThingWithTagAndCustomFields();

		String result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);

		System.out.println(map);

		// assert http return
		assertEquals(this.globalThingIDForTest, Long.valueOf((int)map.get("globalThingID")));
		assertEquals(vendorThingIDsForTest[1], map.get("vendorThingID"));
		assertEquals(KII_APP_ID_NEW, map.get("kiiAppID"));
		assertEquals("some_type_new", map.get("type"));
		assertEquals("some_location_new", map.get("location"));

		// assert status
		Map<String, Object> status = (Map<String, Object>)map.get("status");
		assertEquals(2, status.keySet().size());
		assertEquals("90", status.get("brightness"));
		assertEquals("#123456", status.get("color"));

		// assert custom
		Map<String, Object> custom = (Map<String, Object>)map.get("custom");
		assertEquals(2, custom.keySet().size());
		assertEquals("GMT+8", custom.get("time-zone"));
		assertEquals("20991230", custom.get("produceDate"));

	}

	@Test
	public void testGetThingByGlobalIDException() throws Exception {

		String result=this.mockMvc.perform(
				get("/things/" + "some_non_existing_globalthingid")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

	}

	@Test
	public void testRemoveThing() throws Exception {

		this.testCreatThing();

		GlobalThingInfo thingInfo = globalThingDao.findByID(globalThingIDForTest);
		assertNotNull(thingInfo);

		// delete thing
		String result=this.mockMvc.perform(
				delete("/things/" + globalThingIDForTest)
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		thingInfo = globalThingDao.findByID(globalThingIDForTest);
		assertNull(thingInfo);

	}

	@Test
	public void testRemoveThingException() throws Exception {

		// delete thing
		// global thing id 123456789 is not supposed to exist
		String result=this.mockMvc.perform(
				delete("/things/" + "123456789")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();

	}

	@Test
	public void testOneThingBindMultiTags() throws Exception {

		this.testCreatThing();

		// assert no tag
		String result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);
		List<String> tagList = (List<String>)map.get("tags");

		assertTrue(tagList == null || tagList.isEmpty());

		// bind tags to thing
		String displayName=displayNames[0] + "," + displayNames[1];

		String url="/things/"+globalThingIDForTest+"/tags/custom/"+displayName;

		this.mockMvc.perform(put(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert tags existing
		result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map=mapper.readValue(result, Map.class);
		tagList = (List<String>)map.get("tags");

		assertEquals(2, tagList.size());
		assertTrue(tagList.contains(displayNames[0]));
		assertTrue(tagList.contains(displayNames[1]));

		// bind non existing tag to thing
		displayName="non_existing_tag";

		url="/things/"+globalThingIDForTest+"/tags/custom/"+displayName;

		this.mockMvc.perform(put(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert the non existing tag not bound to thing
		result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map=mapper.readValue(result, Map.class);
		tagList = (List<String>)map.get("tags");

		assertEquals(2, tagList.size());
		assertTrue(tagList.contains(displayNames[0]));
		assertTrue(tagList.contains(displayNames[1]));

	}

	@Test
	public void testOneThingUnBindMultiTags() throws Exception {

		this.testOneThingBindMultiTags();

		// unbind tags from thing
		String displayName=displayNames[0];

		String url="/things/"+globalThingIDForTest+"/tags/custom/"+displayName;

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert tags existing
		String result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map=mapper.readValue(result, Map.class);
		List<String> tagList = (List<String>)map.get("tags");

		assertEquals(1, tagList.size());
		assertTrue(tagList.contains(displayNames[1]));

		// unbind non existing tag from thing
		displayName="non_existing_tag," + displayNames[0];

		url="/things/"+globalThingIDForTest+"/tags/custom/"+displayName;

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert tags existing
		result=this.mockMvc.perform(
				get("/things/" + globalThingIDForTest)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		map=mapper.readValue(result, Map.class);
		tagList = (List<String>)map.get("tags");

		assertEquals(1, tagList.size());
		assertTrue(tagList.contains(displayNames[1]));

	}

	private List<Long> createThings(String[] vendorThingIDList) throws Exception {

		List<Long> globalThingIDList = new ArrayList<>();

		for(String vendorThingID : vendorThingIDList) {

			Map<String, Object> request = new HashMap<>();
			request.put("vendorThingID", vendorThingID);
			request.put("kiiAppID", KII_APP_ID);
			request.put("type", "some type");
			request.put("location", "some location");

			String ctx= mapper.writeValueAsString(request);

			String result=this.mockMvc.perform(
					post("/things").content(ctx)
							.contentType(MediaType.APPLICATION_JSON)
							.characterEncoding("UTF-8")
							.header(Constants.ACCESS_TOKEN, tokenForTest)
			)
					.andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();

			Map<String,Object> map=mapper.readValue(result, Map.class);

			// assert http reture
			long globalThingID = Long.valueOf((int)map.get("globalThingID"));
			assertNotNull(globalThingID);
			assertTrue(globalThingID > 0);

			globalThingIDList.add(globalThingID);
		}

		return globalThingIDList;
	}

	@Test
	public void testMultiThingsBindOneTag() throws Exception {

		globalThingIDListForTets = this.createThings(vendorThingIDsForTest);

		StringBuffer globalThingIDs = new StringBuffer();
		for(Long globalThingID : globalThingIDListForTets) {
			globalThingIDs.append(",").append(globalThingID);
		}
		globalThingIDs.deleteCharAt(0);

		// bind tag to things

		String url="/things/"+globalThingIDs.toString()+"/tags/custom/"+displayNames[0];

		this.mockMvc.perform(put(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert things existing
		String result=this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list=mapper.readValue(result, List.class);
		assertEquals(2, list.size());

		for(Map<String, Object> map : list) {
			Long globalThingID = Long.valueOf((int)map.get("globalThingID"));
			assertTrue(globalThingIDListForTets.contains(globalThingID));
		}

		// bind tag to non existing thing
		globalThingIDs.append(",9999");

		url="/things/"+globalThingIDs.toString()+"/tags/custom/"+displayNames[0];

		this.mockMvc.perform(put(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk());

		// assert the non existing tag not bound to thing
		result=this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list=mapper.readValue(result, List.class);
		assertEquals(2, list.size());

		for(Map<String, Object> map : list) {
			Long globalThingID = Long.valueOf((int)map.get("globalThingID"));
			assertTrue(globalThingIDListForTets.contains(globalThingID));
		}

	}

	@Test
	public void testMultiThingsUnBindOneTag() throws Exception {

		this.testMultiThingsBindOneTag();

		// unbind thing from tag
		String url="/things/"+globalThingIDListForTets.get(0)+"/tags/custom/"+displayNames[0];

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert thing existing
		String result=this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list=mapper.readValue(result, List.class);
		assertEquals(1, list.size());

		Map<String, Object> map = list.get(0);
		Long globalThingID = Long.valueOf((int)map.get("globalThingID"));
		assertEquals(globalThingIDListForTets.get(1), globalThingID);


		// unbind non existing tag from thing
		StringBuffer globalThingIDs = new StringBuffer();
		globalThingIDs.append(globalThingIDListForTets.get(0)).append(",").append("9999");
		url="/things/"+globalThingIDs.toString()+"/tags/custom/"+displayNames[0];

		this.mockMvc.perform(delete(url).content("{}")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.header(Constants.ACCESS_TOKEN, tokenForTest)
		)

				.andExpect(status().isOk());

		// assert thing existing
		result=this.mockMvc.perform(
				get("/things/search?tagType=Custom&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list=mapper.readValue(result, List.class);
		assertEquals(1, list.size());

		map = list.get(0);
		globalThingID = Long.valueOf((int)map.get("globalThingID"));
		assertEquals(globalThingIDListForTets.get(1), globalThingID);

	}

	@Test
	public void testGetThingsByTagExpress() throws Exception {

		// create thing
		String[] vendorThingIDs = new String[]{"vendorThingIDForTest1", "vendorThingIDForTest2", "vendorThingIDForTest3"};
		long[] globalThingIDs = new long[3];

		for(int i = 0; i<vendorThingIDs.length;i++) {
			GlobalThingInfo thingInfo = new GlobalThingInfo();
			thingInfo.setVendorThingID(vendorThingIDs[i]);
			thingInfo.setKiiAppID(KII_APP_ID);
			globalThingIDs[i] = globalThingDao.saveOrUpdate(thingInfo);
		}

		// create tag
		String[] displayNames = new String[]{"displayNameForCustom", "displayNameForLocation"};

		TagIndex tagIndex = new TagIndex();
		tagIndex.setTagType(TagType.Custom);
		tagIndex.setDisplayName(displayNames[0]);
		long tagID1 = tagIndexDao.saveOrUpdate(tagIndex);

		tagIndex = new TagIndex();
		tagIndex.setTagType(TagType.Location);
		tagIndex.setDisplayName(displayNames[1]);
		long tagID2 = tagIndexDao.saveOrUpdate(tagIndex);

		// create relation
		TagThingRelation relation = new TagThingRelation();
		relation.setTagID(tagID1);
		relation.setThingID(globalThingIDs[0]);
		tagThingRelationDao.saveOrUpdate(relation);

		relation = new TagThingRelation();
		relation.setTagID(tagID1);
		relation.setThingID(globalThingIDs[1]);
		tagThingRelationDao.saveOrUpdate(relation);

		relation = new TagThingRelation();
		relation.setTagID(tagID2);
		relation.setThingID(globalThingIDs[2]);
		tagThingRelationDao.saveOrUpdate(relation);


		// search custom tag
		String result=this.mockMvc.perform(
				get("/things/search?" + "tagType=" + TagType.Custom + "&displayName=" + displayNames[0])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		assertEquals(2, list.size());

		for(Map<String, Object> map : list) {
			System.out.println("response: " + map);
			assertTrue((int)map.get("globalThingID") == globalThingIDs[0] || (int)map.get("globalThingID") == globalThingIDs[1]);
			assertTrue(map.get("vendorThingID").equals(vendorThingIDs[0]) || map.get("vendorThingID").equals(vendorThingIDs[1]));
		}

		// search location tag
		result=this.mockMvc.perform(
				get("/things/search?" + "tagType=" + TagType.Location + "&displayName=" + displayNames[1])
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		assertEquals(1, list.size());

		for(Map<String, Object> map : list) {
			System.out.println("response: " + map);
			assertTrue((int)map.get("globalThingID") == globalThingIDs[2]);
			assertTrue(map.get("vendorThingID").equals(vendorThingIDs[2]));
		}

		// search all things
		result=this.mockMvc.perform(
				get("/things/search")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		assertEquals(3, list.size());

		for(Map<String, Object> map : list) {
			System.out.println("response: " + map);
			assertTrue((int)map.get("globalThingID") == globalThingIDs[0] || (int)map.get("globalThingID") == globalThingIDs[1] || (int)map.get("globalThingID") == globalThingIDs[2]);
			assertTrue(map.get("vendorThingID").equals(vendorThingIDs[0]) || map.get("vendorThingID").equals(vendorThingIDs[1]) || map.get("vendorThingID").equals(vendorThingIDs[2]));
		}

	}

	@Test
	public void testGetThingsByType() throws Exception {

		Long[] thingGroup1 = this.creatThingsForTest(3, "vendorThingIDForTest", KII_APP_ID, "LED");
		Long[] thingGroup2 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, "camera");
		Long[] thingGroup3 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, null);

		// query
		String result=this.mockMvc.perform(
				get("/things/types/LED")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(3, list.size());

		List<Long> globalThingIDList = Arrays.asList(thingGroup1);
		for(Map<String, Object> map : list) {
			Long globalThingID = ((Integer)map.get("globalThingID")).longValue();
			globalThingIDList.contains(globalThingID);
		}

		// no result
		result=this.mockMvc.perform(
				get("/things/types/some_non_existing_type")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(0, list.size());

	}

	@Test
	public void testGetAllType() throws Exception {

		// no result
		String result=this.mockMvc.perform(
				get("/things/types")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(0, list.size());

		Long[] thingGroup1 = this.creatThingsForTest(3, "vendorThingIDForTest", KII_APP_ID, "LED");
		Long[] thingGroup2 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, "camera");
		Long[] thingGroup3 = this.creatThingsForTest(1, "vendorThingIDForTest", KII_APP_ID, null);

		// query
		result=this.mockMvc.perform(
				get("/things/types")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertEquals(3, list.size());

		for(Map<String, Object> map : list) {
			String type = (String)map.get("type");
			if("LED".equals(type)) {
				assertEquals(3, map.get("count"));
			} else if("camera".equals(type)) {
				assertEquals(1, map.get("count"));
			} else if(null == type) {
				assertEquals(1, map.get("count"));
			}
		}

	}

	private Long[] creatThingsForTest(int creatCount, String prefixVendorThingID, String kiiAppID, String type) {
		// create thing

		Random random = new Random();

		Long[] globalThingIDs = new Long[creatCount];

		for(int i = 0; i<creatCount;i++) {
			GlobalThingInfo thingInfo = new GlobalThingInfo();
			thingInfo.setVendorThingID(prefixVendorThingID + random.nextInt(10000));
			thingInfo.setKiiAppID(kiiAppID);
			thingInfo.setType(type);
			globalThingIDs[i] = globalThingDao.saveOrUpdate(thingInfo);
		}

		return globalThingIDs;
	}

}
