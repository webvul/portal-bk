package com.kii.beehive.portal.web;


import static junit.framework.TestCase.assertEquals;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.store.entity.PortalSyncUser;
import com.kii.beehive.portal.store.entity.UserCustomData;
import com.kii.beehive.portal.web.entity.SyncUserRestBean;

public class JsonTest {


//	private ObjectMapper mapper=new ObjectMapper();

	@Test
	public void testCustomData() throws IOException {


		String json="{\"data\":[\"abcd\",\"abc\"]}";

//		assertTrue(mapper.canSerialize(CustomData.class));


		CustomData data=mapper.readValue(json,CustomData.class);

		System.out.println(data);


		UserCustomData  userData=new UserCustomData();
		userData.addData("test",data);

		String jsonUser=mapper.writeValueAsString(userData);

		userData=mapper.readValue(jsonUser,UserCustomData.class);

		System.out.println(userData.getDataMap().get("test").getData().get("data"));

	}

	private ObjectMapper mapper=new ObjectMapper();

	@PostConstruct
	public void init() {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Test
	public void testUserConvert() throws IOException {


		Map<String,Object> input=new HashMap<>();
		input.put("userName","ac");
		Map<String,Object> custom=new HashMap<>();

		custom.put("No","123");

		input.put("custom",custom);

		String json=mapper.writeValueAsString(input);

		SyncUserRestBean user=mapper.readValue(json,SyncUserRestBean.class);

		assertEquals("123",user.getCustomFields().getValueByKey("No"));

		PortalSyncUser bUser=user.getBeehiveUser();

		assertEquals("123",bUser.getCustomFields().getValueByKey("No"));

//
//		user.setKiiLoginName("abc");
////		user.setCustomField("no","123");
//
//		BeehiveUser bUser=user.getBeehiveUser();
//
//		String json=mapper.writeValueAsString(bUser);
//
//		Map<String,Object> map=mapper.readValue(json,Map.class);
//
//		assertEquals("123",map.get("custom-no"));

	}

//	@Test
//	public void test() throws IOException {
//		String outJson="{\"party3rdID\":\"name:9\"}";
//
//		OutputUser user=mapper.readValue(outJson,OutputUser.class);
//
////		assertEquals(user.getParty3rdID(),"name:9");
//
//		BeehiveUser newUser=user.getBeehiveUser();
//
////		assertEquals(newUser.getParty3rdID(),"name:9");
//
//
//	}
}
