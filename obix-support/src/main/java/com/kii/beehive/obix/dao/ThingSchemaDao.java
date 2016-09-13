package com.kii.beehive.obix.dao;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.store.PointDataType;
import com.kii.beehive.obix.store.RangeElement;
import com.kii.beehive.obix.store.beehive.ActionInput;
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


	private Map<String,EnumRange> rangeMap=new HashMap<>();


	private ObixThingSchema convert(ThingSchema th)  {

		ObixThingSchema  schema=new ObixThingSchema();

		th.getStatesSchema().getProperties().forEach((k,v)->{

			ObixPointDetail point=new ObixPointDetail();
			point.setFieldName(k);
			point.setMaxValue(v.getMaximum());
			point.setMinValue(v.getMinimum());
			point.setDescription(v.getDisplayNameCN());
			point.setType(PointDataType.getInstance(v.getType()));

			EnumRange range=getRange(v.getEnumMap());
			rangeMap.put(k,range);

			point.setUnitRef(v.getUnit());

			point.setExistCur(true);

			schema.addField(point);
		});

		th.getActions().forEach((k,v)->{

			ObixPointDetail point=schema.getFieldCollect().get(k);

			ActionInput in=v.getIn();


			point.setFieldName(k);
			point.setMaxValue(v.getMaximum());
			point.setMinValue(v.getMinimum());
			point.setDescription(v.getDisplayNameCN());
			point.setType(PointDataType.getInstance(v.getType()));

			EnumRange range=getRange(v.getEnumMap());
			rangeMap.put(k,range);

			point.setUnitRef(v.getUnit());

			point.setExistCur(true);

			schema.addField(point);
		});


		return schema;
	}

	private  EnumRange getRange(Map<String,Object> enumMap){

		EnumRange range=new EnumRange();

		enumMap.forEach((k,v)->{

			RangeElement  elem=new RangeElement();
			elem.setVal(v);
			elem.setDisplayName(k);
			elem.setName(k);

			range.addElement(elem);

		});

		return range;
	}
}
