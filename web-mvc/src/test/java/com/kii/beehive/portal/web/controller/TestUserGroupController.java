package com.kii.beehive.portal.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUserGroupController extends WebTestTemplate {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private GroupUserRelationDao relationDao;

	private List<String> userIDListForTest = new ArrayList<>();

	private String userGroupID;

	private List<String> userGroupNameListForTest = new ArrayList<>();

	private String tokenForTest = BEARER_SUPER_TOKEN;

	@Before
	public void before() {
		super.before();

		userGroupNameListForTest.add("test_usergroupname");
		userGroupNameListForTest.add("test_usergroupname_new");
		userGroupNameListForTest.add("test_usergroupname_withoutuser");

		userIDListForTest.add("test_userid_1");
		userIDListForTest.add("test_userid_2");
		userIDListForTest.add("test_userid_3");

		for (String s : userIDListForTest) {
			BeehiveUser user = new BeehiveUser();
			user.setAliUserID(s);
			user.setUserName("username_for_" + s);

			userDao.createUser(user);
		}

	}

	private String toString(List<String> list) {

		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(",");
			buffer.append(str);
		}
		buffer.deleteCharAt(0);

		return buffer.toString();
	}

	@Test
	public void testAddUsersToUserGroup() throws Exception {
		this.testCreateUserGroup();

		// add users to user group
		String userID = "211102";

		String result = this.mockMvc.perform(
				post("/usergroup/" + userGroupID + "/user/" + userID)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		System.out.println("Response: " + result);

		// assert
		GroupUserRelation groupUserRelation = relationDao.findByUserIDAndUserGroupID(userID, Long.parseLong(userGroupID));

		assertNotNull(groupUserRelation);

	}

	@Test
	public void testRemoveUsersFromUserGroup() throws Exception {

		String userID = "211102";

		// create user group
		this.testAddUsersToUserGroup();

		// remove users from user group
		String userIDLeft = userIDListForTest.remove(0);
		String userIDs = this.toString(userIDListForTest);

		String result = this.mockMvc.perform(
				delete("/usergroup/" + userGroupID + "/user/" + userID)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isUnauthorized())
				.andReturn().getResponse().getContentAsString();

		System.out.println("Response: " + result);


	}

	@Test
	public void testCreateUserGroup() throws Exception {

		Map<String, Object> request = new HashMap<>();
		request.put("userGroupName", "test_usergroupname");
		request.put("description", "some description");

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/usergroup/").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		// assert http return
		userGroupID = (String) map.get("userGroupID");
		assertNotNull(userGroupID);

	}

	@Test
	public void testUpdateUserGroup() throws Exception {

		// create user group info
		testCreateUserGroup();

		// update user group info
		Map<String, Object> request = new HashMap<>();
		request.put("userGroupID", userGroupID);
		request.put("userGroupName", "test_usergroupname_new");
		request.put("description", "some description.new");

		String ctx = mapper.writeValueAsString(request);

		String result = this.mockMvc.perform(
				post("/usergroup/").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String, Object> map = mapper.readValue(result, Map.class);

		// assert http reture
		UserGroup ug = userGroupDao.findByID(userGroupID);
		assertEquals("test_usergroupname_new", ug.getName());
		assertEquals("some description.new", ug.getDescription());

	}


	@Test
	public void testQueryUserGroup() throws Exception {
		// create user group info
		testUpdateUserGroup();

		// query
		String result = this.mockMvc.perform(
				get("/usergroup/list")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertTrue(list.size() > 0);
	}


	@Test
	public void testQueryUserGroupAll() throws Exception {

		// create user group info
		testUpdateUserGroup();

		// query
		String result = this.mockMvc.perform(
				get("/usergroup/all")
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String, Object>> list = mapper.readValue(result, List.class);

		System.out.println("Response: " + list);

		// assert
		assertTrue(list.size() > 0);
	}

	@Test
	public void testDeleteUserGroup() throws Exception {

		// create user group info
		testCreateUserGroup();

		String result = this.mockMvc.perform(
				delete("/usergroup/" + userGroupID)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();


		UserGroup ug = userGroupDao.findByID(userGroupID);
		assertNull(ug);
	}

}
