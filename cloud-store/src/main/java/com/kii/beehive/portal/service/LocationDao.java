package com.kii.beehive.portal.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kii.beehive.portal.store.entity.LocationInfo;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

public class LocationDao extends AbstractDataAccess<LocationInfo> {


	@Override
	protected Class<LocationInfo> getTypeCls() {
		return LocationInfo.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("locationInfo");
	}


	public void addFloorsInBuilding(String building){


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
	}
}
