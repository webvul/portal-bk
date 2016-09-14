package com.kii.beehive.obix.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.kii.beehive.obix.common.UrlInfo;
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
	public ObixContain getSchemaDefine(@PathVariable("schemaName") String schemaName,ServerHttpRequest request){


		ObixThingSchema schema= schemaService.getThingSchema(schemaName);


		return convertSchema(schema,new UrlInfo(request.getURI()));
	}



	private ObixContain convertSchema(ObixThingSchema schema, UrlInfo url){


		ObixContain obix=new ObixContain();

		obix.setName(schema.getFullSchemaName());
		obix.setDisplayName(schema.getDescription());

		obix.setObixType(ObixType.OBJ);

		obix.setHref(url.getFullUrl());

		if(StringUtils.isNotBlank(schema.getSuperRef())) {
			obix.setIs(url.addToRootUrl(schema.getSuperRef()));
		}

		schema.getFieldCollect().values().forEach(p-> {
					ObixContain point=getEmbedPoint(p, url);
					if(point.getObixType()==ObixType.ENUM){
						point.setRange(url.addToFullPath(point.getHref()+"~range"));
					}
					obix.addChild(point);
				}
		);

		return obix;

	}


	private ObixContain getEmbedPoint(ObixPointDetail point, UrlInfo baseUrl){
		ObixContain obix=new ObixContain();

		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(baseUrl.addToRootUrl(point.getSuperRef()));
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		if(point.getRange().isExist()){

			obix.setRange(baseUrl+"/"+point.getFieldName()+"/~range");
		}
		obix.setUnit(unitService.getObixUnitRef(point.getUnitRef()));

		return obix;
	}


	@RequestMapping(path="/{schemaName}/{fieldName}" )
	public ObixContain getPointDefine(@PathVariable("schemaName") String schemaName,
									  @PathVariable("fieldName") String fieldName,
									  UriComponentsBuilder builder){


		ObixPointDetail point= schemaService.getPointSchema(schemaName,fieldName);

		return initPointContain(point,builder);
	}

	private ObixContain initPointContain(ObixPointDetail point, UriComponentsBuilder builder){
		ObixContain obix=new ObixContain();

		obix.setName(point.getFieldName());
		obix.setDisplayName(point.getDescription());

		obix.setHref(point.getFieldName()+"/");

		if(StringUtils.isNotBlank(point.getSuperRef())) {
			obix.setIs(builder.path(point.getSuperRef()).toUriString());
		}
		obix.setObixType(ObixType.getInstance(point.getType()));
		obix.setWritable(point.isWritable());

		if(obix.getObixType()==ObixType.INT  || obix.getObixType()==ObixType.REAL) {
			obix.setMax(point.getMaxValue());
			obix.setMin(point.getMinValue());
		}

		obix.setUnit(unitService.getObixUnitRef(point.getUnitRef()));

		if(obix.getObixType()==ObixType.ENUM){
			obix.setRange(builder.path("~range").toUriString());
		}
		obix.setHref(builder.toUriString());

		return obix;
	}



	@RequestMapping(path="/{schemaName}/{fieldName}/~range" )
	public ObixContain getPointRangeDefine(@PathVariable("schemaName") String schemaName, @PathVariable("fieldName") String fieldName,UriComponentsBuilder builder){


		EnumRange range= schemaService.getEnumRange(schemaName,fieldName);

		return convertRange(range,builder);
	}


	private ObixContain convertRange(EnumRange range,UriComponentsBuilder builder){
		ObixContain obix=new ObixContain();

		range.getValueMap().forEach((k,v)->{

			ObixContain elem=new ObixContain();
			elem.setObixType(ObixType.getInstance(range.getType()));
			elem.setName(k);
			elem.setDisplayName(v.getDisplayName());

			obix.addChild(elem);

		});

		obix.setHref(builder.toUriString());

		return obix;
	}
}
