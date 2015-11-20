package com.kii.beehive.portal.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.kii.beehive.portal.web.controller.ThingController;

public class TestThingControll extends WebTestTemplate{

	@Autowired
	private ThingController controller;

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

}
