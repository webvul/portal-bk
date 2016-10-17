package com.kii.beehive.obix.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.obix.dao.ThingSchemaDao;
import com.kii.beehive.obix.store.PointInfo;
import com.kii.beehive.obix.store.ThingInfo;
import com.kii.beehive.obix.store.beehive.Thing;
import com.kii.beehive.obix.store.beehive.ThingSchema;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocQuery;
import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.LocationDao;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.ThingCommand;


@Component
public class ThingStatusService {


	@Autowired
	private GlobalThingSpringDao thingDao;

	@Autowired
	private ThingLocationDao thingLocDao;

	@Autowired
	private LocationDao locDao;

	@Autowired
	private ThingSchemaDao schemaDao;

	@Autowired
	private ThingIFInAppService thingIFService;


	@Autowired
	private ThingLocationRelDao locRelationDao;

	@Autowired
	private AppInfoManager appInfoManager;



	public List<ThingInfo>  getThingInfoByLoc(String loc){

		return getThingByLoc(loc).stream().map(this::getFullThingInfo).collect(Collectors.toList());

	}



	public List<PointInfo>  getPointInfoByLoc(String loc){

		return getThingInfoByLoc(loc).stream().flatMap(th->th.getPointCollect().stream()).collect(Collectors.toList());

	}


	private ThingInfo getFullThingInfo(Thing thing){


		ThingSchema  schema=schemaDao.getThingSchemaByThingVendorID(thing.getThingID());

		ThingInfo info=new ThingInfo(schema,thing.getStatus());

		List<String> locs=locRelationDao.getRelation(thing.getDBId());
		if(!locs.isEmpty()){
			info.setLocation(locs.get(0));
		}else{
			info.setLocation("");
		}
		info.setName(thing.getThingID());

		info.setPointCollect(info.getSchema().getFieldCollect().entrySet().stream().map(entry->{

			String name=entry.getKey();

			return new PointInfo(entry.getValue(),name,thing.getStatus().getField(name),thing.getThingID());

		}).collect(Collectors.toSet()));

//		info.setLocation(thing.getLocation());

		return info;

	}


	public List<Thing> getThingByLoc(String loc){

		ThingLocQuery query=new ThingLocQuery();
		query.setLocation(loc);

		return thingLocDao.getThingsByLocation(query,0).stream().map(Thing::new).collect(Collectors.toList());


	}

	public ThingInfo getThingByID(String thingID){


		return getFullThingInfo(new Thing(thingDao.getThingByVendorThingID(thingID)));

	}


	public void setThingStatus(String thingID,String name,Object val){

		GlobalThingInfo thing=thingDao.getThingByVendorThingID(thingID);

		ThingCommand cmd=new ThingCommand();

		ThingSchema schema=schemaDao.getThingSchemaByThingVendorID(thingID);

		String actionDef=schema.getActions().entrySet().stream().filter((a)->a.getValue().getIn().getProperties().containsKey(name)).findFirst().get().getKey();

		Action action=new Action();
		action.setField(name,val);
		cmd.addAction(actionDef,action);
		cmd.setSchema(schema.getName());
		cmd.setSchemaVersion(schema.getVersion());
		cmd.setTitle(schema.getActions().get(actionDef).getIn().getTitle());
		cmd.setUserID(appInfoManager.getDefaultOwer(thing.getKiiAppID()).getUserID());

		thingIFService.sendCommand(cmd,thing.getFullKiiThingID());

	}


}
