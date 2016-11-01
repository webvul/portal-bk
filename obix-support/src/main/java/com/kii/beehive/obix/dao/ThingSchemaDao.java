package com.kii.beehive.obix.dao;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;

@Component
public class ThingSchemaDao {


	@Autowired
	private ObjectMapper mapper;


	@Autowired
	private ResourceLoader loader;

	@Autowired
	private IndustryTemplateDao  templateDao;


	@Autowired
	private GlobalThingSpringDao  thingDao;


	private ThingSchema getThingSchemaByName(String name){

		IndustryTemplate template=templateDao.getTemplateByName(name);

		try {
			ThingSchema schema = mapper.readValue(template.getContent(), ThingSchema.class);

			schema.setVersion(1);

			return schema;
		}catch(IOException  e){
			throw new IllegalArgumentException(e);
		}
	}

	public ThingSchema  getThingSchemaByThingVendorID(String id)  {


		GlobalThingInfo  thing=thingDao.getThingByVendorThingID(id);


		IndustryTemplate template=templateDao.getTemplateByThingID(thing.getId());

		try {
			ThingSchema schema = mapper.readValue(template.getContent(), ThingSchema.class);
			schema.setName(template.getName());
			schema.setVersion(1);

			return schema;
		}catch(IOException  e){
			throw new IllegalArgumentException(e);
		}
	}


	public ObixThingSchema getObixThingSchemaByName(String name)  {

		return new ObixThingSchema(getThingSchemaByName(name));
	}



}
