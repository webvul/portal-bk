package com.kii.beehive.portal.web;


import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.OutputUser;

public class JsonTest {


	private ObjectMapper mapper=new ObjectMapper();

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
