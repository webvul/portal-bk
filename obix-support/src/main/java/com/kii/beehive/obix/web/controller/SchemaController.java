package com.kii.beehive.obix.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.kii.beehive.obix.service.ObixUnitIndexService;
import com.kii.beehive.obix.service.ThingSchemaService;
import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;
import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;

@RestController
@RequestMapping(path="/def/schema",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class SchemaController {



	@Autowired
	private ThingSchemaService  schemaService;


	@Autowired
	private ObixUnitIndexService unitService;


	@RequestMapping(path="/{schemaName}" )
	public ObixContain getSchemaDefine(@PathVariable("schemaName") String schemaName,WebRequest request){


		ObixThingSchema schema= schemaService.getThingSchema(schemaName);

		String fullPath = getPath(request);

		return convertSchema(schema,fullPath);
	}

	public String getPath(WebRequest request) {
		String fullPath= StringUtils.substringAfter(request.getDescription(false),"=");
		fullPath=StringUtils.substringBeforeLast(fullPath,"/");
		return fullPath;
	}


	private ObixContain convertSchema(ObixThingSchema schema, String fullPath){


		ObixContain obix=new ObixContain();

		obix.setName(schema.getName());
		obix.setDisplayName(schema.getDescription());

		obix.setObixType(ObixType.OBJ);

		if(StringUtils.isNotBlank(schema.getSuperRef())) {
			obix.setIs(fullPath+"/"+schema.getSuperRef());
		}

		schema.getFieldCollect().values().forEach(p-> {
					ObixContain point=getEmbedPoint(p, fullPath);
					if(point.getObixType()==ObixType.ENUM){
						point.setRange(fullPath+"/"+point.getHref()+"~range");
					}
					obix.addChild(point);
				}
		);

		return obix;

	}


	private ObixContain getEmbedPoint(ObixPointDetail point, String baseUrl){
		ObixContain obix=new ObixContain();

		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(baseUrl+"/"+point.getSuperRef());
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		if(point.getRange().isExist()){

			obix.setRange(baseUrl+"/"+point.getFieldName()+"~range");
		}
		obix.setUnit(unitService.getObixUnitRef(point.getUnitRef()));

		return obix;
	}


	@RequestMapping(path="/{schemaName}/{fieldName}" )
	public ObixContain getPointDefine(@PathVariable("schemaName") String schemaName,
									  @PathVariable("fieldName") String fieldName,
									  WebRequest request){


		String fullPath = getPath(request);


		ObixPointDetail point= schemaService.getPointSchema(schemaName,fieldName);

		return initPointContain(point,fullPath);
	}

	private ObixContain initPointContain(ObixPointDetail point, String fullPath){
		ObixContain obix=new ObixContain();

		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(fullPath+"/"+point.getSuperRef());
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		obix.setUnit(unitService.getObixUnitRef(point.getUnitRef()));

		if(obix.getObixType()==ObixType.ENUM){
			obix.setRange(fullPath+"/~range");
		}
		obix.setHref(fullPath);

		if(point.isWritable()){
			obix.addToIs("h:op");
			obix.addToIs("obix:writablePoint");
		}else{
			obix.addToIs("h:sensor");
			obix.addToIs("obix:point");
		}

		return obix;
	}



	@RequestMapping(path="/{schemaName}/{fieldName}/~range" )
	public ObixContain getPointRangeDefine(@PathVariable("schemaName") String schemaName, @PathVariable("fieldName") String fieldName,WebRequest request){


		EnumRange range= schemaService.getEnumRange(schemaName,fieldName);

		return convertRange(range);
	}


	private ObixContain convertRange(EnumRange range){
		ObixContain obix=new ObixContain();

		range.getValueMap().forEach((k,v)->{

			ObixContain elem=new ObixContain();
			elem.setObixType(ObixType.getInstance(range.getType()));
			elem.setName(k);
			elem.setVal(v.getVal());
			elem.setDisplayName(v.getDisplayName());

			obix.addChild(elem);

		});


		return obix;
	}
}
