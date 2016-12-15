package com.kii.beehive.business.service;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;

@Component
public class IndustryTemplateService {

	@Autowired
	private IndustryTemplateDao dao;


	@Autowired
	private ObjectMapper mapper;


	@CacheEvict(cacheNames = CacheConfig.INDUSTRY_TEMPLATE)
	@CachePut(cacheNames = CacheConfig.LONGLIVE_CACHE,key="'industryTemplate-'+#template.name")
	public void insert(IndustryTemplate template){

		dao.insert(template);

	}

	@CacheEvict(cacheNames = CacheConfig.INDUSTRY_TEMPLATE)
	@CachePut(cacheNames = CacheConfig.LONGLIVE_CACHE,key="'industryTemplate-'+#template.name")
	public void updateEntityAllByID(IndustryTemplate template){

		dao.updateEntityAllByID(template);

	}


	@Cacheable(cacheNames = CacheConfig.INDUSTRY_TEMPLATE )
	public ThingSchema getTemplateByThingID(long thingID){


		IndustryTemplate template=dao.getTemplateByThingID(thingID);

		try {
			ThingSchema schema = mapper.readValue(template.getContent(), ThingSchema.class);
			schema.setName(template.getName());
			schema.setVersion(1);

			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}

	}


	@Cacheable(cacheNames = CacheConfig.INDUSTRY_TEMPLATE )
	public ThingSchema getTemplateByKiiThingID(String kiiThingID){


		IndustryTemplate template=dao.getTemplateByKiiThingID(kiiThingID);

		if(template==null){
			return new ThingSchema();
		}
		try {
			ThingSchema schema = mapper.readValue(template.getContent(), ThingSchema.class);
			schema.setName(template.getName());
			schema.setVersion(1);

			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}

	}


	@Cacheable(cacheNames = CacheConfig.INDUSTRY_TEMPLATE )
	public ThingSchema getTemplateByVendorThingID(String vendorThingID){


		IndustryTemplate template=dao.getTemplateByVendorThingID(vendorThingID);

		try {
			ThingSchema schema = mapper.readValue(template.getContent(), ThingSchema.class);
			schema.setName(template.getName());
			schema.setVersion(1);

			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}

	}

	@Cacheable(cacheNames = CacheConfig.LONGLIVE_CACHE,key="'industryTemplate-'+#name" )
	public ThingSchema getThingSchemaByName(String name){

		IndustryTemplate template=dao.getTemplateByName(name);

		try {
			ThingSchema schema = mapper.readValue(template.getContent(), ThingSchema.class);

			schema.setVersion(1);

			return schema;
		}catch(IOException  e){
			throw new IllegalArgumentException(e);
		}
	}



}
