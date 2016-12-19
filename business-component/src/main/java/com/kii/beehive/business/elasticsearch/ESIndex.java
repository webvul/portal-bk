package com.kii.beehive.business.elasticsearch;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ESIndex {
	
	
	public final static ESIndex thingInfo=new ESIndex(IndexEnum.thingstatus, TypeEnum.thinginfo);
	
	
	public  final static ESIndex thingStatus=new ESIndex(IndexEnum.thingstatus, TypeEnum.thingstatus);
	
	
	private final IndexEnum index;
	
	private final TypeEnum type;
	
	
	private ESIndex(IndexEnum index) {
		this.index = index;
		this.type = null;
	}
	
	private  ESIndex(IndexEnum index, TypeEnum type) {
		this.index = index;
		this.type = type;
	}
	
	
	
	public enum IndexEnum{
		thingstatus;
	}
	
	public  enum TypeEnum{
		thinginfo,thingstatus;
	}
	
	
	@JsonProperty("_index")
	public IndexEnum getIndex() {
		return index;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ESIndex esIndex = (ESIndex) o;
		return Objects.equals(index, esIndex.index) &&
				Objects.equals(type, esIndex.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(index, type);
	}
	
	@JsonProperty("_type")
	public TypeEnum getType() {
		return type;
	}
	
	@JsonIgnore
	public String getMapperTemplateName(){
		
		return index.name()+".mapper.json";
	}
}
