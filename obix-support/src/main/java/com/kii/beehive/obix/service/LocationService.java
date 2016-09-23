package com.kii.beehive.obix.service;


import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.obix.store.LocationInfo;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;

@Component
public class LocationService {


	LocationInfo   rootLoc=new LocationInfo();

	Map<String,ThingInfo> thingMap=new HashMap<>();


	public LocationInfo getRootLoc(){

		return rootLoc;
	}

	public LocationInfo getLoc(String loc){
		return rootLoc;
	}

	public ThingInfo getThingInfo(String thingName){
		return thingMap.get(thingName);
	}


	public PointInfo getPointInfo(String thingID ,String pointID){

		ThingInfo th=thingMap.get(thingID);

		Set<PointInfo>  points=thingMap.get(thingID).getPointCollect();

		PointInfo p= points.stream().filter(pointID::equals).findAny().get();

//		p.setThingSchema(th.getSchema());

		return p;
	}


	@PostConstruct
	public void init(){

		fillSubLocation("",1,1);

	}

	private void fillSubLocation(String upperLoc,int level,int num){

		Set<LocationInfo> subLocs=new HashSet<>();

		if(level<3){
			for(int i=0;i<num;i++){

				LocationInfo  info=new LocationInfo();
				info.setDisplayName("loc"+upperLoc+"0"+(i+1));
				info.setLocation(upperLoc+"0"+(i+1));
				info.setLevel(level);

				fillSubLocation(info.getLocation(),level+1,num+2);

				if(level==2){
					info.setThingCollect(getThingInfoList("thing",info.getLocation()));
				}
				subLocs.add(info);
			}

		}else{

			for(int i=(int)'A';i<(int)'E';i++){


				LocationInfo  info=new LocationInfo();
				info.setDisplayName("loc"+upperLoc+"-"+(i+1));
				info.setLocation(upperLoc+"-"+(i+1));
				info.setLevel(level);

				subLocs.add(info);

			}

		}

		rootLoc.fillSubLocations(subLocs);
	}

	private Set<ThingInfo> getThingInfoList(String prefix, String loc){

		Set<ThingInfo>  list=new HashSet<>();
		for(int i=0;i<10;i++) {
			ThingInfo thing = new ThingInfo();
			thing.setLocation(loc+"0"+i);
			thing.setName(prefix + i);
//			thing.setSchema("demo");

			Set<PointInfo> pointSet=new HashSet<>();

			PointInfo p1=new PointInfo();
			p1.setFieldName("bri");
			p1.setValue(RandomUtils.nextInt(0,100));
			p1.setLocation(loc+"-A");
			pointSet.add(p1);

			PointInfo p2=new PointInfo();
			p2.setFieldName("power");
			p2.setValue(RandomUtils.nextInt(0,2)>1);
			p2.setLocation(loc+"-A");
			pointSet.add(p2);

			PointInfo p3=new PointInfo();
			p3.setFieldName("lightSensor");
			p3.setValue(RandomUtils.nextFloat(10,100));
			p3.setLocation(loc+"-B");
			pointSet.add(p3);

			thing.setPointCollect(pointSet);

			Set<String> locCol=new HashSet<>();
			locCol.add(loc+"-A");
			locCol.add(loc+"-B");
			locCol.add(loc+"-C");

//			thing.setLocationCollect(locCol);

			Set<String> customTags=new HashSet<>();
			customTags.add("foo");
			customTags.add("bar");
			thing.setCustomTags(customTags);


			thingMap.put(thing.getName(),thing);
			list.add(thing);

		}

		return list;
	}

}
