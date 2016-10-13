//package com.kii.beehive.obix.dao;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.kii.beehive.business.service.ThingIFInAppService;
//import com.kii.beehive.obix.store.beehive.Thing;
//import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
//import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
//import com.kii.beehive.portal.jdbc.dao.ThingLocQuery;
//import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
//import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
//import com.kii.beehive.portal.service.LocationDao;
//import com.kii.extension.sdk.entity.thingif.ThingCommand;
//
//@Component
//public class ThingStatusDao {
//
//
//
//	private GlobalThingSpringDao thingDao;
//
//	private ThingLocationDao thingLocDao;
//
//	private LocationDao  locDao;
//
//
//	@Autowired
//	private IndustryTemplateDao templateDao;
//
//	@Autowired
//	private ThingIFInAppService thingIFService;
//
//
//
//	public List<Thing> getThingIDByLoc(String loc){
//
//		ThingLocQuery query=new ThingLocQuery();
//		query.setLocation(loc);
//
//		return thingLocDao.getThingsByLocation(query,0).stream().map(Thing::new).collect(Collectors.toList());
//
//
//	}
//
//	public Thing getThingByID(String thingID){
//
//
//		return new Thing(thingDao.getThingByVendorThingID(thingID));
//
//	}
//
//
//	public void setThingStatus(String thingID,String name,Object val){
//
//		GlobalThingInfo thing=thingDao.getThingByVendorThingID(thingID);
//
//		ThingCommand  cmd=new ThingCommand();
//
//
//
//		thingIFService.sendCommand(thing.getFullKiiThingID());
//
//
//	}
//
//
//}
