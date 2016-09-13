package com.kii.beehive.obix.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.obix.service.ThingSchemaService;
import com.kii.beehive.obix.store.EnumRange;
import com.kii.beehive.obix.store.ObixPointDetail;
import com.kii.beehive.obix.store.ObixThingSchema;

@RestController
@RequestMapping(path="/def",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DefineController {



	@Autowired
	private ThingSchemaService  schemaService;


	@RequestMapping(path="/{schemaName}" )
	public ObixThingSchema getSchemaDefine(@PathVariable("schemaName") String schemaName){


		return schemaService.getThingSchema(schemaName);
	}


	@RequestMapping(path="/{schemaName}/{fieldName}/~range" )
	public EnumRange getPointRangeDefine(@PathVariable("schemaName") String schemaName,@PathVariable("fieldName") String fieldName){


		return schemaService.getEnumRange(schemaName,fieldName);
	}

	@RequestMapping(path="/{schemaName}/{fieldName}" )
	public ObixPointDetail getPointDefine(@PathVariable("schemaName") String schemaName,
										  @PathVariable("fieldName") String fieldName){


		return schemaService.getPointSchema(schemaName,fieldName);
	}




}
