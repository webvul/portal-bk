package com.kii.beehive.portal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.LocationInfo;
import com.kii.beehive.portal.store.entity.LocationType;
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

		deleteByUpperLevel(".");

		getSeq(locInfo).forEach(loc->{

			LocationInfo info=new LocationInfo();
			info.setLocation(loc);

			info.setParent(".");

			info.setLevel(LocationType.building);

			info.setDisplayName(loc);

			super.addEntity(info,loc);

		});

	}


	public void generSubLevelInUpper(String upper,SubLocInfo  subLoc){

		deleteByUpperLevel(upper);

		Map<String,String> subLevel=new HashMap<>();

		LocationType type= LocationType.getTypeByLocation(upper);


		getSeq(subLoc,upper).forEach(loc->{

			LocationInfo info=new LocationInfo();
			info.setLocation(loc);

			info.setParent(upper);

			info.setLevel(LocationType.getNextLevel(type));

			info.setDisplayName(loc);

			if(info.getLevel()==LocationType.area){
				String area=LocationType.area.getLevelSeq(loc);
				String areaType=StringUtils.substring(area,0,1);
				info.setAreaType(LocationInfo.AreaType.valueOf(areaType));
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
		QueryParam query= ConditionBuilder.newCondition().prefixLike("parent",upperLevel).getFinalQueryParam();

		super.fullQuery(query).forEach((rec)->{
			super.removeEntity(rec.getId());
		});
	}
	public  List<String> getSeq(SubLocInfo seqInfo){
		return getSeq(seqInfo,null);
	}



	public  List<String> getSeq(SubLocInfo seqInfo,String levelPrefix){

		if(StringUtils.isEmpty(levelPrefix)){
			levelPrefix="";
		}else if(LocationType.getTypeByLocation(levelPrefix)==LocationType.partition){
			levelPrefix+="-";
		}

		final String globalPrefix=levelPrefix;

		List<String> result=new ArrayList<>();

		if(!seqInfo.array.isEmpty()){

			return seqInfo.array.stream().map((s)->globalPrefix+s).collect(Collectors.toList());
		}


		if( (seqInfo.from==null) || (seqInfo.to == null)){

			return result;
		}

		Object from=seqInfo.from;


		String prefix=seqInfo.prefix;
		if(StringUtils.isBlank(prefix)){
			prefix="";
		}

		String fillZero=StringUtils.repeat('0',2);


		if(from instanceof  String ){
			String fromStr=(String)from;
			String toStr=(String)seqInfo.to;
			if(StringUtils.isBlank(fromStr)||StringUtils.isBlank(toStr)){
				return result;
			}

			char  start=fromStr.charAt(0);
			char end=toStr.charAt(0);

			for(int i=(int)start;i<=(int)end;i++){
				result.add(levelPrefix+prefix+String.valueOf((char)i));
			}

			return result;
		}else if(from instanceof Integer){

			int fromInt= (Integer)seqInfo.from;
			int toInt=(Integer)seqInfo.to;



			for(int i=(int)fromInt;i<=(int)toInt;i++){
				String sub=String.valueOf(i);

				String completeSub=StringUtils.substring(fillZero+sub,-2);

				result.add(levelPrefix+prefix+completeSub);
			}

			return result;
		}else{
			return result;
		}

	}

	public static class SubLocInfo{

		private String prefix;


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

		public void setArrayInfo(String... infos){
			this.array= Arrays.asList(infos);
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

	}
}
