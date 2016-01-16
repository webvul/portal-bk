package com.kii.beehive.portal.web.entity;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestStatusUpload {



	ObjectMapper mapper=new ObjectMapper();

	@Test
	public void testThingState() throws IOException {

		String json="{\"state\":{\"lightness\":99,\"power\":true},\"target\":\"thing:th.f83120e36100-a269-5e11-e5bb-0bc2e136\"}";



		StateUpload state=mapper.readValue(json,StateUpload.class);


		assertEquals(state.getThingID(),"th.f83120e36100-a269-5e11-e5bb-0bc2e136");

	}
}
