package com.kii.beehive.portal.web;

import static junit.framework.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.beehive.portal.web.controller.ThingController;

public class TestThingControll extends WebTestTemplate{

	@Autowired
	private ThingController controller;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private ObjectMapper mapper;

	private String[] tagIDs = new String[]{"A", "B"};

	@Before
	public void before() {
		super.before();

		for(String id : tagIDs) {
			TagIndex tagIndex = new TagIndex();
			tagIndex.setTagType(TagType.Custom);
			tagIndex.setDisplayName("A");
			tagIndex.fillID();

			tagIndexDao.addTagIndex(tagIndex);
		}
	}

	@After
	public void after() {
		for (String id : tagIDs) {
			try {
				tagIndexDao.removeTagByID("Custom-" + id);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
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
	public void testGetThingsByTagExpress() throws Exception {

		String ctx= mapper.writeValueAsString(null);

		String result=this.mockMvc.perform(
				get("/things/tag/Custom-A,Custom-B/operation/or").content(ctx)
						.contentType(MediaType.APPLICATION_JSON)
						.characterEncoding("UTF-8")
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Object map=mapper.readValue(result, Object.class);

		System.out.println("response: " + map);

		List mapArray = (List)map;

		assertTrue(mapArray.isEmpty());


	}

}
