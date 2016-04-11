package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TestDeviceSupplierController extends WebTestTemplate {

	@Autowired
	private ObjectMapper mapper;

	private String tokenForTest = BEARER_SUPER_TOKEN;

	@Test
	public void testGetAllDeviceSuppliers() throws Exception {

		String result = this.mockMvc.perform(
				get("/devicesuppliers/all")
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

	}

	/**
	 * below content type will be tested for GET method, all of them is expected to be accepted by GET method
	 * - application/json
	 * - text/html
	 * - no content type
	 *
	 * @throws Exception
     */
	@Test
	public void testContentTypeForGET() throws Exception {

		// test content type application/json
		String result = this.mockMvc.perform(
				get("/devicesuppliers/all")
						.characterEncoding("UTF-8")
						.contentType(MediaType.APPLICATION_JSON)
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// test content type text/html
		result = this.mockMvc.perform(
				get("/devicesuppliers/all")
						.characterEncoding("UTF-8")
						.contentType(MediaType.TEXT_HTML)
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

		// test no content type
		result = this.mockMvc.perform(
				get("/devicesuppliers/all")
						.characterEncoding("UTF-8")
						.header(Constants.ACCESS_TOKEN, tokenForTest)
		)
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		System.out.println("========================================================");
		System.out.println("Response: " + result);
		System.out.println("========================================================");

	}

}
