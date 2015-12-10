package com.kii.beehive.portal.web;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.controller.UserController;

public class TestUserController extends WebTestTemplate{

	@Autowired
	private UserController controller;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private BeehiveUserDao beehiveUserDao;


	@Test
	public void testEncode() throws Exception {

		Map<String,String> map=new HashMap<>();

		map.put("name","张三");
		map.put("value", "力士");


		String result=this.mockMvc.perform(
				post("/echo")
				.content(mapper.writeValueAsString(map))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
		.andReturn().getResponse().getContentAsString();

		Map<String,Object> newMap=mapper.readValue(result,Map.class);

		assertEquals(map.get("name"),newMap.get("name"));
	}

	@Test
	public void testThingBind() throws Exception {

		///{globalThingID}/tags/{tagName}


		String thingIDs="12345-aaa22,12345-aaa11";

		String tags="location-2F,Location-1F";

		String url="/things/"+thingIDs+"/tags/"+tags;

		this.mockMvc.perform(put(url).accept(MediaType.parseMediaType("application/json;charset=UTF-8")))

				.andExpect(status().isOk());
//				.andExpect(content().contentType("application/json"));
	}

	@Test
	public void testUserQuery() throws Exception{


		String result=this.mockMvc.perform(
				post("/users/simplequery").content("{}")
				.contentType("application/json")

		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<Map<String,Object>> map=mapper.readValue(result, List.class);


	}

	@Test
	public void testUserAdd() throws Exception {

		///{globalThingID}/tags/{tagName}


		String ctx="{\"userID\":\"21110219700930101913\",\"userName\":\"张三\",\"company\":\"IBM\",\"role\":\"2\",\"custom\":{\"age\":40,\"division\":[\"QA\",\"Operation\"]}}";

//		String encodeCtx=new String(ctx.getBytes(),"UTF-8");

		String result=this.mockMvc.perform(
				post("/users/").content(ctx)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);

		assertEquals(map.get("userID"),"21110219700930101913");
		assertEquals(map.get("userName"),"张三");



//				.andExpect(content().contentType("application/json"));
	}

	@Test
	public void testUpdateUser() throws Exception {

		// change this id for each run
		String userID = "21110219490909" + new Random(10000).nextInt();

		BeehiveUser user = new BeehiveUser();
		user.setAliUserID(userID);
		user.setUserName("user name");
		beehiveUserDao.createUser(user);

		Map<String, Object> request = new HashMap<>();
		request.put("userName", "new name");

		String ctx= mapper.writeValueAsString(request);

		String result=this.mockMvc.perform(
				patch("/users/" + userID).content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
						.header("Authorization", "Bearer d31032a0-8ebf-11e5-9560-00163e02138f")
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Map<String,Object> map=mapper.readValue(result, Map.class);

		// assert http reture
		String resultUserID = (String)map.get("userID");

		assertEquals(userID, resultUserID);

	}
}
