package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.store.entity.LocationInfo;
import com.kii.beehive.portal.store.entity.LocationTree;
import com.kii.beehive.portal.store.entity.LocationType;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.FieldType;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@Component
@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
public class LocationDao extends AbstractDataAccess<LocationInfo> {


	@Override
	protected Class<LocationInfo> getTypeCls() {
		return LocationInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("locationInfo");
	}



	public List<LocationInfo>  getLowLocation(String location){

		QueryParam query=ConditionBuilder.newCondition().equal("parent",location).getFinalQueryParam();

		return super.fullQuery(query);
	}

	Comparator<LocationInfo> locationInfoComparator = (o1, o2) -> {
		if(o1.getLevel() == o2.getLevel()) {
			return o1.getId().compareTo(o2.getId());
		}else{
			return o1.getLevel().compareTo(o2.getLevel());
		}
	};


	public List<LocationInfo> getAllLowLocation(String location){
		QueryParam query=ConditionBuilder.newCondition().prefixLike("parent",location).getFinalQueryParam();

		List<LocationInfo> list= super.fullQuery(query);


		Collections.sort(list, locationInfoComparator);

		return list;

	}

	public List<LocationInfo> getPathToLoc(String location){


		QueryParam query=ConditionBuilder.newCondition().In("_id",LocationType.getLevelInList(location)).getFinalQueryParam();

		List<LocationInfo> list= super.fullQuery(query);

		Collections.sort(list, locationInfoComparator);

		return list;

	}


	public List<LocationInfo> getTopLocation() {

		QueryParam query= ConditionBuilder.newCondition().prefixLike("parent",".").getFinalQueryParam();

		List<LocationInfo> list= super.fullQuery(query);

		Collections.sort(list, locationInfoComparator);

		return list;
	}
	
	
	@CacheEvict(cacheNames= CacheConfig.PERSISTENCE_CACHE,key="'location_tree'")
	public void generTopLocation(SubLocInfo  locInfo){


		locInfo.getSeq(null).forEach(loc->{

			LocationInfo info=new LocationInfo();
			info.setLocation(loc);

			info.setParent(".");

			info.setLevel(LocationType.building);

			info.setDisplayName(loc);

			super.addEntity(info,loc);

		});

	}
	
//	@CacheEvict(cacheNames= CacheConfig.PERSISTENCE_CACHE,key="'location_tree'")
	public void generSubLevelInUpper(String upper,SubLocInfo  subLoc){

		deleteByUpperLevel(upper);

		Map<String,String> subLevel=new HashMap<>();

		LocationType type= LocationType.getTypeByLocation(upper);


		subLoc.getSeq(upper).forEach(loc->{

			LocationInfo info=new LocationInfo();
			info.setLocation(loc);

			info.setParent(upper);

			info.setLevel(LocationType.getNextLevel(type));

			info.setDisplayName(loc);


			if(info.getLevel()==LocationType.area){
				String area=LocationType.area.getLevelSeq(loc);
//				String areaType=StringUtils.substring(area,-3,-2);

				info.setAreaType(LocationInfo.AreaType.getInstance(area));
			}

			subLevel.put(loc,loc);

			super.addEntity(info,loc);

		});

		super.updateEntity(Collections.singletonMap("subLocations",subLevel),upper);

	}

	public void setDisplayName(String location,String displayName){

		super.updateEntity(Collections.singletonMap("displayName",displayName),location);

		QueryParam query= ConditionBuilder.newCondition().fieldExist("subLocations."+location, FieldType.STRING).getFinalQueryParam();

		super.query(query).forEach((loc)->{

			Map<String,String> map=loc.getSubLocations();
			map.put(location,displayName);

			super.updateEntity(Collections.singletonMap("subLocations",map),loc.getId());

		});


	}

	private void deleteByUpperLevel(String upperLevel){
		QueryParam query= ConditionBuilder.newCondition().prefixLike("parent",upperLevel)
				.getFinalQueryParam();

		super.fullQuery(query).forEach((rec)->{
			super.removeEntity(rec.getId());
		});
	}

//	@Cacheable(cacheNames= CacheConfig.PERSISTENCE_CACHE,key="location_tree")
	public LocationTree getFullLocationTree(){

		QueryParam query=ConditionBuilder.getAll().getFinalCondition().orderBy("location").build();

		LocationTree tree=new LocationTree();
		super.iterateEntitys(query,(loc -> {

			try {
				LocationType.getTypeByLocation(loc.getLocation());
			}catch(IllegalArgumentException e){
				return;
			}

			tree.addSubLocation(loc);
		}));

		return tree;

	}

}
