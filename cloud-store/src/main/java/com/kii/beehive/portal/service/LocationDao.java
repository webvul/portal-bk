package com.kii.beehive.portal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.LocationInfo;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.FieldType;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;


@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class LocationDao extends AbstractDataAccess<LocationInfo> {


	@Override
	protected Class<LocationInfo> getTypeCls() {
		return LocationInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("locationInfo");
	}


	public void generTopLocation(SubLocInfo  locInfo){

		getSeq(locInfo).forEach(loc->{

			LocationInfo info=new LocationInfo();
			info.setLocation(loc);

			info.setParent(".");

			info.setLevel(LocationInfo.LocationType.building);

			info.setDisplayName(loc);

			super.addEntity(info,loc);

		});

	}


	public void generSubLevelInUpper(String upper,SubLocInfo  subLoc,LocationInfo.LocationType type){

		deleteByUpperLevel(upper);

		Map<String,String> subLevel=new HashMap<>();

		getSeq(subLoc).forEach(loc->{


			LocationInfo info=new LocationInfo();
			info.setLocation(loc);

			info.setParent(upper);

			info.setLevel(type);

			info.setDisplayName(loc);

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
		QueryParam query= ConditionBuilder.newCondition().prefixLike("parent",upperLevel).getFinalQueryParam();

		super.fullQuery(query).forEach((rec)->{
			super.removeEntity(rec.getId());
		});
	}


	private List<String> getSeq(SubLocInfo seqInfo){

		List<String> result=new ArrayList<>();

		if(!seqInfo.array.isEmpty()){
			return seqInfo.array;
		}


		if( (seqInfo.from==null) || (seqInfo.to == null)){

			return result;
		}

		Object from=seqInfo.from;



		if(from instanceof  String ){
			String fromStr=(String)from;
			String toStr=(String)seqInfo.to;
			if(StringUtils.isBlank(fromStr)||StringUtils.isBlank(toStr)){
				return result;
			}

			char  start=fromStr.charAt(0);
			char end=toStr.charAt(0);

			for(int i=(int)start;i<=(int)end;i++){
				result.add(String.valueOf((char)i));
			}

			return result;
		}else if(from instanceof Integer){

			int fromInt= (Integer)seqInfo.from;
			int toInt=(Integer)seqInfo.to;



			for(int i=(int)fromInt;i<=(int)toInt;i++){
				result.add(String.valueOf((char)i));
			}

			return result;
		}else{
			return result;
		}

	}

	public static class SubLocInfo{

		private String prefix;

		private String suffix;

		private Object from ;

		private Object  to;

		private List<String> array=new ArrayList<>();

		public Object getFrom() {
			return from;
		}

		public void setFrom(Object from) {
			this.from = from;
		}

		public Object getTo() {
			return to;
		}

		public void setTo(Object to) {
			this.to = to;
		}

		public List<String> getArray() {
			return array;
		}

		public void setArray(List<String> array) {
			this.array = array;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

	}
}
