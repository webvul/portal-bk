package com.kii.beehive.portal.web;


import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.UserRestBean;

public class JsonTest {


	private ObjectMapper mapper=new ObjectMapper();

	@Test
	public void testUserConvert() throws IOException {


		Map<String,Object> input=new HashMap<>();
		input.put("userName","ac");
		Map<String,Object> custom=new HashMap<>();

		custom.put("No","123");

		input.put("custom",custom);

		String json=mapper.writeValueAsString(input);

		UserRestBean user=mapper.readValue(json,UserRestBean.class);

		assertEquals("123",user.getCustomFields().getValueByKey("No"));

		BeehiveUser bUser=user.getBeehiveUser();

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
