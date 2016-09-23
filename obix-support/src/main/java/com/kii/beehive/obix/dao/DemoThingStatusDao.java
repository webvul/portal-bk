package com.kii.beehive.obix.dao;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.obix.store.beehive.Thing;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
public class DemoThingStatusDao {


	private Map<String,Thing>  thingMap=new HashMap<>();


	@PostConstruct
	public void init(){

		for(int i=0;i<20;i++){
			Thing status = generThingStatus(i);
			thingMap.put(status.getThingID(),status);
		}

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
		thing.setLocation("0103W");

		return thing;
	}

	public void setThingStatus(String thingID,String name,Object val){


		ThingStatus  status=thingMap.get(thingID).getStatus();

		status.setField(name,val);

//		thingMap.put(thingID,status);
	}

}
