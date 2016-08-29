package com.kii.beehive.obix.web.controller;

import javax.annotation.PostConstruct;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.PointDataType;
import com.kii.beehive.obix.store.PointDetail;
import com.kii.beehive.obix.store.RangeElement;
import com.kii.beehive.obix.store.ThingSchema;

@RestController
@RequestMapping(path="/def",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DefineController {


	ThingSchema  schema=new ThingSchema();


	EnumRange powRange=new EnumRange();

	@PostConstruct
	public void init(){

		schema.setName("DemoLight");
		schema.setDescription("a demo light device schema");
		schema.setFramewireVersion("1.0.1a");
		schema.addTag("lightsGroup");
		schema.addTag("foo");


		PointDetail  bri=new PointDetail();
		bri.setFieldName("bri");
		bri.setMinValue(0);
		bri.setMaxValue(100);
		bri.addTag("lights");
		bri.addTag("cmd");
		bri.setType(PointDataType.Int);
		bri.setUnitRef("percent");
		bri.setWritable(true);

		schema.addField(bri);

		PointDetail pow=new PointDetail();
		pow.setFieldName("power");
		pow.setType(PointDataType.Enum);
		pow.setEnumRangeRef("powRange");
		pow.addTag("lights");
		pow.addTag("cmd");
		pow.setWritable(true);

		schema.addField(pow);

		PointDetail  sensor=new PointDetail();
		sensor.setFieldName("lightSensor");
		sensor.setType(PointDataType.Int);
		sensor.setUnitRef("lux");
		sensor.setExistCur(true);
		sensor.addTag("lightLevel");
		sensor.addTag("sensor");

		schema.addField(sensor);

		RangeElement  on=new RangeElement();
		on.setName("on");
		on.setDisplayName("power on");
		on.setVal(1);

		RangeElement off=new RangeElement();
		off.setName("off");
		off.setDisplayName("Power Off");
		off.setVal(0);

		powRange.addElement(on);
		powRange.addElement(off);

		powRange.setType(PointDataType.Int);
		powRange.setName("lightPower");



	}


	@RequestMapping(path="/{schemaName}" )
	public ThingSchema getSchemaDefine(@PathVariable("schemaName") String schemaName){


		return schema;
	}


	@RequestMapping(path="/{schemaName}/{fieldName}/~range" )
	public EnumRange getPointRangeDefine(@PathVariable("schemaName") String schemaName,@PathVariable("fieldName") String fieldName){


		return powRange;
	}

	@RequestMapping(path="/{schemaName}/{fieldName}" )
	public PointDetail getPointDefine(@PathVariable("schemaName") String schemaName,
									  @PathVariable("fieldName") String fieldName){


		PointDetail point=schema.getFieldCollect().get(fieldName);

		return point;
	}




}
