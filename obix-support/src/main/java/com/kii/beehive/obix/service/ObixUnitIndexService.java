package com.kii.beehive.obix.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ObixUnitIndexService {


	public String getObixUnitRef(String unit){
		if(StringUtils.isBlank(unit)){
			return null;
		}
		return "obix:units/"+convert(unit);
	}

	private String convert(String unit){
		switch(unit){

			case "˚C":return "Celsius";
			default:return null;

		}
	}


}
