package com.kii.beehive.obix.test;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.industrytemplate.ThingSchema;


public class TestSchema extends TestTemplate{



	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private ResourceLoader  loader;


	@Test
	public void testSchema() throws IOException {

		String json= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/obix/demodata/aircondition.schema.json").getInputStream(), Charsets.UTF_8);


		ThingSchema schema=mapper.readValue(json,ThingSchema.class);


		System.out.println(schema.getStatesSchema().getProperties());



	}
}
