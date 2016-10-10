package com.kii.beehive.obix.web.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.kii.beehive.obix.service.LocationService;
import com.kii.beehive.obix.service.ObixContainConvertService;
import com.kii.beehive.obix.store.LocationInfo;
import com.kii.beehive.obix.web.entity.ObixContain;
import com.kii.beehive.obix.web.entity.ObixType;

@RestController
@RequestMapping(path="/site",method= RequestMethod.GET,consumes = {MediaType.ALL_VALUE},produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class LocTagController {


	@Autowired
	private LocationService  locService;


	@Autowired
	private ObixContainConvertService convertService;



	@RequestMapping(path="/**")
	public ObixContain getRootLoc(WebRequest request){

		String url= StringUtils.substringAfter(request.getDescription(false),"=");

		String baseUrl=StringUtils.substringBefore(url,"/site/");

		String locStr=StringUtils.substringAfter(url,"/site/");

		String[]  list=StringUtils.split(locStr,"/");

		if(list.length>4){
			return new ObixContain();
		}



		if(list.length==0){
			ObixContain  obj=new ObixContain();
			obj.setObixType(ObixType.OBJ);
			obj.setHref(url);
			obj.setName(locStr);
			obj.setName("ROOT");

			locService.getRootLoc().forEach(l->{

				ObixContain  loc=convertService.getEmbeddedObix(l.getLocation(),baseUrl);
				loc.setName(l.getLocation());
				loc.setObixType(ObixType.REF);
				obj.addChild(loc);

			});
			return obj;
		}

		locStr=StringUtils.replace(locStr,"/","-");

		LocationInfo currLoc=locService.getLocationInfo(locStr);

		ObixContain  loc=convertService.getFullObix(currLoc,baseUrl);
		loc.setName(locStr);
		loc.setObixType(ObixType.OBJ);
		return loc;
	}




}