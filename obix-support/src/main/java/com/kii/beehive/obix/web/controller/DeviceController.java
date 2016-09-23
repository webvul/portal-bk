package com.kii.beehive.obix.web.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.kii.beehive.obix.service.ObixUnitIndexService;
import com.kii.beehive.obix.service.ThingService;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;
import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;

@RestController
@RequestMapping(path="/things",consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DeviceController {



	@Autowired
	private ThingService  thingService;


	@Autowired
	private ObixUnitIndexService unitService;

	@RequestMapping(path="/{thingID}",method=RequestMethod.GET )
	public ObixContain  getThingDetail(@PathVariable("thingID") String thingID,UriComponentsBuilder builder){


		String baseUrl=builder.toUriString();

		ThingInfo  thing=thingService.getFullThingInfo(thingID);



		ObixContain obix= convertThingSchema(thing.getSchema(),baseUrl);

		obix.setHref(baseUrl+"/things/"+thing.getName());
		obix.setDisplay(thing.getName());
		obix.setName(thing.getName());

		obix.addToIs("h:equip");
		obix.addToIs(baseUrl+"def/contract/commEquip");
		obix.addToIs(baseUrl+"def/contract/siteRef");


		obix.addToIs("h:ahu");

		thing.getPointCollect().forEach((p)->{


			ObixContain obixP=initEmbeddedPointContain(p,thing.getSchema().getName(),baseUrl);

			obix.addChild(obixP);

		});

		ObixContain  obixL=convertEmbeddedLocation(thing.getLocation(),baseUrl);
		obix.addChild(obixL);

		return obix;
	}


	private ObixContain convertThingSchema(ObixThingSchema schema, String  baseUrl){


		ObixContain obix=new ObixContain();

		obix.setName(schema.getFullSchemaName());
		obix.setDisplayName(schema.getDescription());

		obix.setObixType(ObixType.OBJ);

		String defUrl=baseUrl+"/def/schema/";
		obix.setHref(defUrl+obix.getName());

		if(StringUtils.isNotBlank(schema.getSuperRef())) {
			obix.setIs(baseUrl+schema.getSuperRef());
		}

		return obix;

	}

	private ObixContain initEmbeddedPointContain(PointInfo p,String thingSchemaName, String baseUrl){


		ObixPointDetail  point=p.getSchema();

		ObixContain obix=new ObixContain();

		obix.setObixType(ObixType.REF);

		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(baseUrl+"def/contract"+point.getSuperRef());
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		obix.setUnit(unitService.getObixUnitRef(point.getUnitRef()));

		if(obix.getObixType()==ObixType.ENUM){
			obix.setRange(baseUrl+"def/schema/"+thingSchemaName+"/"+p.getFieldName()+"/~range");
		}
		obix.setHref(p.getFieldName()+"/");
		obix.setDisplay(p.getFieldName());
		obix.setName(p.getFieldName());
		obix.setVal(p.getValue());
		obix.addToIs(baseUrl+"def/"+thingSchemaName+"/"+p.getFieldName());
		obix.addToIs(baseUrl+"def/contract/commPoint");

		if(p.getSchema().isWritable()) {
			obix.addToIs("obix:writablePoint");
		}else{
			obix.addToIs("obix:point");
		}

		return obix;
	}



	private ObixContain convertEmbeddedLocation(String  loc,String baseUrl){

		ObixContain obix=new ObixContain();

		obix.setObixType(ObixType.REF);
		obix.setHref(baseUrl+"/locations/"+loc);
		obix.setDisplay(loc);
		obix.setName("siteRef");
		obix.setIs(baseUrl+"/def/contract/location");

		return obix;
	}



	@RequestMapping(path="/{thingID}/{pointName}",method=RequestMethod.GET )
	public ObixContain  getPointDetail(@PathVariable("thingID") String thingID,
									   @PathVariable("pointName") String name,
									   UriComponentsBuilder builder){


		String baseUrl=builder.toUriString();

		ThingInfo  thing=thingService.getFullThingInfo(thingID);

		PointInfo p=thing.getPointCollect().stream().filter(pp->pp.getFieldName().equals(name)).findFirst().get();


		ObixContain obixP=initEmbeddedPointContain(p, thing.getSchema().getName(),baseUrl);

		obixP.setObixType(ObixType.OBJ);

		obixP.setHref(baseUrl+"/things/"+thingID+"/"+name);

		obixP.setDisplay(p.getFieldName());
		obixP.setName(p.getFieldName());
		obixP.setVal(p.getValue());
		obixP.addToIs(baseUrl+"def/contract/siteRef" );
		obixP.addToIs(baseUrl+"def/"+thing.getSchema().getName()+"/"+p.getFieldName());
		obixP.addToIs(baseUrl+"def/contract/commPoint");


		if(p.getSchema().isWritable()) {
			obixP.addToIs("obix:writablePoint");
		}else{
			obixP.addToIs("obix:point");
		}

		ObixContain  loc=convertEmbeddedLocation(p.getLocation(),baseUrl);

		loc.setName("siteRef");

		obixP.addChild(loc);

		ObixContain obixT= convertThingSchema(thing.getSchema(),baseUrl);

		obixT.setName("equipRef");
		obixP.addChild(obixT);

		return obixP;

	}

	@RequestMapping(path="/{thingID}/{pointName}",method=RequestMethod.PUT )
	public void  setPointDetail(@PathVariable("thingID") String thingID ,
									   @PathVariable("pointName") String name,
								@RequestBody ObixContain  input){



	}



}
