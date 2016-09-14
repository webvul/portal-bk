package com.kii.beehive.obix.dao;


import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.store.beehive.ActionInput;
import com.kii.beehive.obix.store.beehive.PointDetail;
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

			return schema;
		}catch(IOException e){
			throw new IllegalArgumentException(e);
		}
	}


	public ObixThingSchema getObixThingSchemaByName(String name){

		return convert(getThingSchemaByName(name));
	}




	private ObixThingSchema convert(ThingSchema th)  {

		ObixThingSchema  schema=new ObixThingSchema();

		th.getStatesSchema().getProperties().forEach((k,v)->{

			ObixPointDetail point=new ObixPointDetail(k,v);

			point.setExistCur(true);

			schema.addField(point);
		});

		th.getActions().forEach((k,v)->{

			ActionInput in=v.getIn();

			Map<String,PointDetail> pointMap=in.getProperties();

			if(pointMap.size()>1||pointMap.size()==0){
				return;
			}
			Map.Entry<String,PointDetail> entry=pointMap.entrySet().iterator().next();

			PointDetail detail=entry.getValue();
			String fieldName=entry.getKey();

			ObixPointDetail point=schema.getFieldCollect().get(fieldName);

			if(point==null){
				point=new ObixPointDetail(fieldName,detail);
			}

			point.setWritable(true);

			schema.addField(point);
		});


		return schema;
	}


}
