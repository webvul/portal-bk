package com.kii.beehive.obix.dao;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class LocationDao {

	//01-02-A03

	public List<String> getTopLocation(){

		List<String>  list=new ArrayList<>();

		for(int i=1;i<7;i++){

			list.add("0"+i);
		}

		return list;
	}

	public List<String> getChildLoc(String loc){


		if(loc.length()==2){
			List<String> list=new ArrayList<>();

			for(int i=1;i<5;i++){
				list.add(loc+"-0"+i);
			}
			return list;

		}else if(loc.length()==5){
			List<String> list=new ArrayList<>();

			for(int i=0;i<5;i++){
				list.add(loc+"-"+ (char)('A'+i)+'0'+i);
			}
			return list;

		}else{
			return new ArrayList<>();
		}
	}

	public String getParentLoc(String loc){
		if(loc.length()==2){

			return "";

		}else if(loc.length()==5){
			return StringUtils.substring(loc,0,2);

		}else if(loc.length()==9){
			return StringUtils.substring(loc,0,5);
		}else{
			return "";
		}

	}

}
