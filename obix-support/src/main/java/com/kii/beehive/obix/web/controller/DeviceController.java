package com.kii.beehive.obix.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.kii.beehive.obix.service.ObixContainConvertService;
import com.kii.beehive.obix.service.ThingService;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;
import com.kii.beehive.obix.web.entity.ObixContain;

@RestController
@RequestMapping(path="/things",consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class DeviceController {



	@Autowired
	private ThingService  thingService;


	@Autowired
	private ObixContainConvertService  convertService;

	@RequestMapping(path="/{thingID}",method=RequestMethod.GET )
	public ObixContain  getThingDetail(@PathVariable("thingID") String thingID,UriComponentsBuilder builder){


		String baseUrl=builder.toUriString();

		ThingInfo  thing=thingService.getFullThingInfo(thingID);


		return convertService.getFullObix(thing,baseUrl);
	}


	@RequestMapping(path="/{thingID}/{pointName}",method=RequestMethod.GET )
	public ObixContain  getPointDetail(@PathVariable("thingID") String thingID,
									   @PathVariable("pointName") String name,
									   UriComponentsBuilder builder){


		String baseUrl=builder.toUriString();

		ThingInfo  thing=thingService.getFullThingInfo(thingID);

		PointInfo p=thing.getPointCollect().stream().filter(pp->pp.getFieldName().equals(name)).findFirst().get();

		return convertService.getFullObix(p,thing.getSchema(),baseUrl);

	}

	@RequestMapping(path="/{thingID}/{pointName}",method=RequestMethod.PUT )
	public void  setPointDetail(@PathVariable("thingID") String thingID ,
									   @PathVariable("pointName") String name,
								@RequestBody ObixContain  input){


		PointInfo point=new PointInfo();
		point.setValue(input.getVal());
		point.setFieldName(input.getName());

		thingService.setPointInfo(thingID,point);


	}



}
