//package com.kii.beehive.portal.web;
//
//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertTrue;
//import static junit.framework.TestCase.fail;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.kii.beehive.business.service.KiiUserService;
//import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
//import com.kii.beehive.portal.service.ArchiveBeehiveUserDao;
//import com.kii.beehive.portal.service.PortalSyncUserDao;
//import com.kii.beehive.portal.store.entity.PortalSyncUser;
//import com.kii.beehive.portal.web.constant.Constants;
//import com.kii.beehive.portal.web.controller.UserSyncController;
//import com.kii.extension.sdk.exception.UserAlreadyExistsException;
//
//
//public class TestUserController extends WebTestTemplate{
//
//	private  static final String AUTH_HEADER = Constants.ACCESS_TOKEN;
//
//	@Autowired
//	private UserSyncController controller;
//
//	@Autowired
//	private ObjectMapper mapper;
//
//	@Autowired
//	private PortalSyncUserDao beehiveUserDao;
//
//	@Autowired
//	private KiiUserService kiiUserService;
//
//	@Autowired
//	private ArchiveBeehiveUserDao archiveBeehiveUserDao;
//
//
//	private String userIDForTest;
//
//	private String kiiUserIDForTest;
//
//	private String userGroupIDForTest;
//
//	private String userGroupNameForTest;
//
//	private String superTokenForTest = BEARER_SUPER_TOKEN;
//	private String tokenForTest = BEARER_DEVICE_SUPPLIER_ID;
//
//	@Before
//	public void before() {
//		super.before();
//
//		System.out.println("*******************************************************************");
//		System.out.println("*********************** Junit Before Method Start *****************");
//		System.out.println("*******************************************************************");
//
//		userIDForTest = "some_userid_for_test";
//		userGroupNameForTest = "some_usergroup_name_for_test";
//
//		clear();
//
//		System.out.println("*******************************************************************");
//		System.out.println("*********************** Junit Before Method End *******************");
//		System.out.println("*******************************************************************");
//	}
//
//	/**
//	 * important:
//	 * when Kii user in master app is not synchronised with beehive user in portal app, need to manually remove it
//	 */
//	@After
//	public void clear() {
//		System.out.println("*******************************************************************");
//		System.out.println("*********************** Junit After Method Start ******************");
//		System.out.println("*******************************************************************");
//
//		// remove from archive beehive user
//		PortalSyncUser user = new PortalSyncUser();
//		user.setAliUserID(userIDForTest);
//		try {
//			user = archiveBeehiveUserDao.queryInArchive(user);
//
//			archiveBeehiveUserDao.removeArchive(userIDForTest);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//
//		// remove from master app
//		try {
//			// if user not existing in archive beehive user, try to get from beehive user
//			if(user == null) {
//				user = beehiveUserDao.getUserByID(userIDForTest);
//			}
//
//			System.out.println("Clear() KiiUserID:" + user.getKiiUserID());
//			kiiUserService.removeBeehiveUser(user);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//
//		// remove from beehive user
//		try {
//			beehiveUserDao.deleteUser(userIDForTest);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//
//
//
//		System.out.println("*******************************************************************");
//		System.out.println("*********************** Junit After Method End ********************");
//		System.out.println("*******************************************************************");
//	}
//
//	@Test
//	public void testEncode() throws Exception {
//
//		Map<String,String> map=new HashMap<>();
//
//		map.put("name","张三");
//		map.put("value", "力士");
//
//
//		String result=this.mockMvc.perform(
//				post("/echo")
//				.content(mapper.writeValueAsString(map))
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.header(AUTH_HEADER, superTokenForTest))
//				.andExpect(status().isOk())
//		.andReturn().getResponse().getContentAsString();
//
//		Map<String,Object> newMap=mapper.readValue(result,Map.class);
//
//		assertEquals(map.get("name"),newMap.get("name"));
//	}
//
//	@Test
//	public void testUserAdd() throws Exception {
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("userID", userIDForTest);
//		request.put("userName", "张三");
//		request.put("company", "IBM");
//		request.put("role", "2");
//		request.put("phone", "some.phone.number");
//		request.put("mail", "some.mail");
//
//		Map<String, Object> custom = new HashMap<>();
//		custom.put("age", 40);
//		custom.put("division", new String[]{"QA", "Operation"});
//		request.put("custom", custom);
//
//		String ctx = mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				post("/users/").content(ctx)
//				.contentType(MediaType.APPLICATION_JSON)
//				.characterEncoding("UTF-8")
//				.header(AUTH_HEADER, tokenForTest)
//				)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		Map<String,Object> map=mapper.readValue(result, Map.class);
//
//		assertEquals(map.get("userID"), userIDForTest);
//		assertEquals(map.get("userName"),"张三");
//
//		// check whether Kii user is created in master app
//		BeehiveJdbcUser user = new BeehiveJdbcUser();
//		user.setUserName(userIDForTest);
//		user.setId(101l);
//		try {
//			kiiUserService.addBeehiveUser(user,user.getDefaultPassword());
//			fail();
//		} catch (Throwable e) {
//			assertEquals(UserAlreadyExistsException.class, e.getClass());
//		}
//
//	}
//
//	@Test
//	public void testUserAddException() throws Exception {
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("userID", userIDForTest);
//		request.put("userName", "张三");
//		request.put("company", "IBM");
//		request.put("role", "2");
//		request.put("phone", "some.phone.number");
//		request.put("mail", "some.mail");
//
//		Map<String, Object> custom = new HashMap<>();
//		custom.put("age", 40);
//		custom.put("division", new String[]{"QA", "Operation"});
//		request.put("custom", custom);
//
//		String ctx = mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				post("/users/").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		Map<String,Object> map=mapper.readValue(result, Map.class);
//
//		assertEquals(map.get("userID"), userIDForTest);
//		assertEquals(map.get("userName"),"张三");
//
//		// test 409
//		result=this.mockMvc.perform(
//				post("/users/").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isConflict())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		// test 400
//		// no userName/userID/role
//		result=this.mockMvc.perform(
//				post("/users/").content("{}")
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isBadRequest())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		// test 400
//		// no userName
//		request = new HashMap<>();
//		request.put("userID", userIDForTest);
//		request.put("role", "2");
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isBadRequest())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		// test 400
//		// no userID
//		request = new HashMap<>();
//		request.put("userName", "张三");
//		request.put("role", "2");
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isBadRequest())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		// test 400
//		// no role
//		request = new HashMap<>();
//		request.put("userID", userIDForTest);
//		request.put("userName", "张三");
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isBadRequest())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//	}
//
//
//	@Test
//	public void testUpdateUser() throws Exception {
//
//		this.testUserAdd();
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("userName", "张三.new");
//		request.put("company", "IBM.new");
//		request.put("role", "3");
//		request.put("phone", "some.phone.number.new");
//		request.put("mail", "some.mail.new");
//
//		Map<String, Object> custom = new HashMap<>();
//		custom.put("age", 45);
//		custom.put("division", new String[]{"QA"});
//		custom.put("new-field", "some_new_field_value");
//		request.put("custom", custom);
//
//		String ctx= mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				patch("/users/" + userIDForTest).content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		Map<String,Object> map=mapper.readValue(result, Map.class);
//
//		// assert http return
//		String resultUserID = (String)map.get("userID");
//
//		assertEquals(userIDForTest, resultUserID);
//
//	}
//
//	@Test
//	public void testUpdateUser2() throws Exception {
//
//		this.testUserAdd();
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("userName", "张三.new");
//		request.put("company", "IBM.new");
//		request.put("userID", "another_user_id");
//
//		String ctx= mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				patch("/users/" + userIDForTest).content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		Map<String,Object> map=mapper.readValue(result, Map.class);
//
//		// assert http return
//		String resultUserID = (String)map.get("userID");
//
//		assertEquals(userIDForTest, resultUserID);
//
//		// query
//		result=this.mockMvc.perform(
//				get("/users/" + userIDForTest).content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		map=mapper.readValue(result, Map.class);
//		resultUserID = (String)map.get("userID");
//
//		System.out.println("Response: " + result);
//
//		assertEquals(userIDForTest, resultUserID);
//
//	}
//
//	@Test
//	public void testUpdateUserException() throws Exception {
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("userName", "张三.new");
//		request.put("company", "IBM.new");
//		request.put("role", "3");
//		request.put("phone", "some.phone.number.new");
//		request.put("mail", "some.mail.new");
//
//		Map<String, Object> custom = new HashMap<>();
//		custom.put("age", 45);
//		custom.put("division", new String[]{"QA"});
//		custom.put("new-field", "some_new_field_value");
//		request.put("custom", custom);
//
//		String ctx= mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				patch("/users/" + "some_non_existing_userid").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isNotFound())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//	}
//
//	@Test
//	public void test3rdPartyUpdateUser() throws Exception {
//
//		this.testUserAdd();
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("gender", "male");
//		request.put("age", 50);
//		request.put("address", "address is secret");
//
//		String ctx= mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				patch("/users/" + userIDForTest + "/custom").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		Map<String,Object> map=mapper.readValue(result, Map.class);
//
//		// assert http return
//		String resultUserID = (String)map.get("userID");
//
//		assertEquals(userIDForTest, resultUserID);
//
//		// query
//		request = new HashMap<>();
//		request.put("userID", userIDForTest);
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		List<Map<String, Object>> mapList=mapper.readValue(result, List.class);
//		assertEquals(1, mapList.size());
//
//		map = mapList.get(0);
//		Map<String, Object> custom = (Map<String, Object>)map.get("custom");
//
//		assertEquals(4, custom.keySet().size());
//
//		assertEquals("male", custom.get("gender"));
//		assertEquals(50, custom.get("age"));
//		assertEquals("address is secret", custom.get("address"));
//
//		List<String> division = (List<String>)custom.get("division");
//		assertEquals("QA", division.get(0));
//		assertEquals("Operation", division.get(1));
//	}
//
//	@Test
//	public void test3rdPartyUpdateUserException() throws Exception {
//
//		this.testUserAdd();
//
//		Map<String, Object> request = new HashMap<>();
//		request.put("gender", "male");
//		request.put("age", 50);
//		request.put("address", "address is secret");
//
//		String ctx= mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				patch("/users/" + "some_non_existing_userid" + "/custom").content(ctx)
//						.contentType(MediaType.APPLICATION_JSON)
//						.characterEncoding("UTF-8")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isNotFound())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//	}
//
//	@Test
//	public void testDeleteUser() throws Exception {
//
//		this.testUserAdd();
//
//		// query
//		Map<String, Object> request = new HashMap<>();
//		request.put("userID", userIDForTest);
//
//		String ctx = mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		List<Map<String, Object>> mapList=mapper.readValue(result, List.class);
//		assertEquals(1, mapList.size());
//
//		// delete
//		result=this.mockMvc.perform(
//				delete("/users/" + userIDForTest)
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		// query
//		request = new HashMap<>();
//		request.put("userID", userIDForTest);
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		mapList=mapper.readValue(result, List.class);
//		assertEquals(0, mapList.size());
//
//	}
//
//	@Test
//	public void testDeleteUserException() throws Exception {
//
//		this.testUserAdd();
//
//		// query
//		Map<String, Object> request = new HashMap<>();
//		request.put("userID", userIDForTest);
//
//		String ctx = mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		List<Map<String, Object>> mapList=mapper.readValue(result, List.class);
//		assertEquals(1, mapList.size());
//
//		// delete
//		result=this.mockMvc.perform(
//				delete("/users/" + "some_non_existing_userid")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isNotFound())
//				.andReturn().getResponse().getContentAsString();
//
//		System.out.println();
//		System.out.println("Http Response: " + result);
//		System.out.println();
//
//		// query
//		request = new HashMap<>();
//		request.put("userID", userIDForTest);
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		mapList=mapper.readValue(result, List.class);
//		assertEquals(1, mapList.size());
//
//	}
//
//
//	@Test
//	public void testUserQuery() throws Exception{
//
//		this.testUpdateUser();
//
//		// query by userID
//		Map<String, Object> request = new HashMap<>();
//		request.put("userID", userIDForTest);
//
//		String ctx = mapper.writeValueAsString(request);
//
//		String result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		List<Map<String,Object>> response = mapper.readValue(result, List.class);
//		assertEquals(1, response.size());
//
//		this.assertUserInfoForTestUserQuery(response.get(0));
//
//		// query by userName and phone
//		request = new HashMap<>();
//		request.put("userName", "张三.new");
//		request.put("phone", "some.phone.number.new");
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		response = mapper.readValue(result, List.class);
//		assertEquals(1, response.size());
//
//		this.assertUserInfoForTestUserQuery(response.get(0));
//
//		// query by userName and custom field
//		request = new HashMap<>();
//		request.put("userName", "张三.new");
//		request.put("custom.age", 45);
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		response = mapper.readValue(result, List.class);
//		assertEquals(1, response.size());
//
//		this.assertUserInfoForTestUserQuery(response.get(0));
//
//		// query and get no result
//		request = new HashMap<>();
//		request.put("role", "non-exist-role");
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		response = mapper.readValue(result, List.class);
//		assertTrue(response.size() == 0);
//
//		// query and get multiple result
//		request = new HashMap<>();
//		request.put("role", "2");
//
//		ctx = mapper.writeValueAsString(request);
//
//		result=this.mockMvc.perform(
//				post("/users/simplequery").content(ctx)
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		response = mapper.readValue(result, List.class);
//		assertTrue(response.size() > 1);
//
//
//	}
//
//	/**
//	 * assert the beehive user info from testUpdateUser();
//	 * @param response
//	 */
//	private void assertUserInfoForTestUserQuery(Map<String, Object> response) {
//
//		assertEquals(response.get("userID"), userIDForTest);
//		assertEquals(response.get("userName"), "张三.new");
//		assertEquals(response.get("company"), "IBM.new");
//		assertEquals(response.get("role"), "3");
//		assertEquals(response.get("phone"), "some.phone.number.new");
//		assertEquals(response.get("mail"), "some.mail.new");
//
//		Map<String, Object> custom = (Map<String, Object>)response.get("custom");
//		assertEquals(custom.get("age"), 45);
//
//		List<String> division = (List<String>)custom.get("division");
//		assertEquals(1, division.size());
//		assertEquals(division.get(0), "QA");
//
//		assertEquals("some_new_field_value", custom.get("new-field"));
//
//	}
//
//	@Test
//	public void testUserQueryAll() throws Exception{
//
//
//		String result=this.mockMvc.perform(
//				post("/users/simplequery").content("{}")
//						.contentType("application/json")
//						.header(AUTH_HEADER, tokenForTest)
//		)
//				.andExpect(status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		List<Map<String,Object>> map=mapper.readValue(result, List.class);
//
//
//	}
//}
