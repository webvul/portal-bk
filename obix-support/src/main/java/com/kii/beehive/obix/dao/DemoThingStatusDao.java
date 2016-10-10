package com.kii.beehive.obix.dao;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.obix.store.beehive.Thing;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class DemoThingStatusDao {


	private Map<String,Thing>  thingMap=new HashMap<>();

	private Map<String,List<String>> locThingMap=new HashMap<>();



	@PostConstruct
	public void init(){

		for(int i=0;i<20;i++){
			Thing status = generThingStatus(i);
			thingMap.put(status.getThingID(),status);
		}

	}

	public List<String> getThingIDByLoc(String loc){
		List<String> list= locThingMap.get(loc);

		if(list==null){
			return new ArrayList<>();
		}

		return list;
	}

	public Thing getThingByID(String thingID){


		return thingMap.get(thingID);

	}



	private Thing generThingStatus(int idx) {
		ThingStatus  status=new ThingStatus();

		status.setField("Temp", RandomUtils.nextFloat(10,30));

		status.setField("Valve",idx%2);

		status.setField("Speed",idx%3);

		status.setField("Mode",idx%3);

		status.setField("Power",1);

		Thing thing=new Thing();

		thing.setThingID("thing"+idx);
		thing.setStatus(status);
		thing.setSchema("aircondition");
		String loc="01-0"+idx%3;
		thing.setLocation(loc);

		locThingMap.computeIfAbsent(loc,(l)->{
			return new ArrayList<>();
		}).add(thing.getThingID());

		return thing;
	}



	public void setThingStatus(String thingID,String name,Object val){


		ThingStatus  status=thingMap.get(thingID).getStatus();

		status.setField(name,val);

	}
	

}