package com.kii.beehive.portal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.kii.beehive.portal.store.entity.LocationType;

public class SubLocInfo {

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



	public  List<String> getSeq(String levelPrefix){

		if(StringUtils.isEmpty(levelPrefix)){
			levelPrefix="";
		}else if(LocationType.getTypeByLocation(levelPrefix)==LocationType.partition){
			levelPrefix+="-";
		}

		final String globalPrefix=levelPrefix;

		List<String> result=new ArrayList<>();

		if(!getArray().isEmpty()){

			return getArray().stream().map((s)->globalPrefix+s).collect(Collectors.toList());
		}


		if( (getFrom()==null) || (getTo() == null)){

			return result;
		}

		Object from=getFrom();


		String prefix=getPrefix();
		if(StringUtils.isBlank(prefix)){
			prefix="";
		}

		String fillZero=StringUtils.repeat('0',2);


		if(from instanceof  String ){
			String fromStr=(String)from;
			String toStr=(String)getTo();
			if(StringUtils.isBlank(fromStr)||StringUtils.isBlank(toStr)){
				return result;
			}

			char  start=fromStr.charAt(0);
			char end=toStr.charAt(0);

			for(int i=(int)start;i<=(int)end;i++){
				String val=String.valueOf((char)i);
//				String completeSub=StringUtils.substring(fillZero+val,-2);

				result.add(levelPrefix+prefix+val);
			}

			return result;
		}else if(from instanceof Integer){

			int fromInt= (Integer)getFrom();
			int toInt=(Integer)getTo();



			for(int i=fromInt;i<=toInt;i++){
				String sub=String.valueOf(i);

				String completeSub=StringUtils.substring(fillZero+sub,-2);

				result.add(levelPrefix+prefix+completeSub);
			}

			return result;
		}else{
			return result;
		}

	}

}
