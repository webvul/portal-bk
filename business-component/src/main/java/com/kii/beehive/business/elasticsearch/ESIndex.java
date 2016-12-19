package com.kii.beehive.business.elasticsearch;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ESIndex {
	
	private final ESService.IndexEnum index;
	
	private final ESService.TypeEnum type;
	
	
	public ESIndex(ESService.IndexEnum index) {
		this.index = index;
		this.type = null;
	}
	
	public ESIndex(ESService.IndexEnum index, ESService.TypeEnum type) {
		this.index = index;
		this.type = type;
	}
	
	
	@JsonProperty("_index")
	public ESService.IndexEnum getIndex() {
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
	public ESService.TypeEnum getType() {
		return type;
	}
	
	@JsonIgnore
	public String getMapperTemplateName(){
		
		return index.name()+".mapper.json";
	}
}
