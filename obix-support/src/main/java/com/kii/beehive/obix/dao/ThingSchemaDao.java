package com.kii.beehive.obix.dao;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.store.beehive.ThingSchema;

@Component
public class ThingSchemaDao {


	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private ResourceLoader loader;


	public ThingSchema  getThingSchemaByName(String name)  {

		try {
			String json = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/obix/demodata/" + name + ".schema.json").getInputStream(), Charsets.UTF_8);


			ThingSchema schema = mapper.readValue(json, ThingSchema.class);

			schema.setName(name);
			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}


	public ObixThingSchema getObixThingSchemaByName(String name){

		return new ObixThingSchema(getThingSchemaByName(name));
	}



}
