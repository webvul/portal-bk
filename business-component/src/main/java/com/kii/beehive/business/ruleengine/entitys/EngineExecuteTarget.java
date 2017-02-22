package com.kii.beehive.business.ruleengine.entitys;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = CallHttpApiInEngine.class,name="HttpApiCall"),
		@JsonSubTypes.Type(value = CallHttpApiWithSign.class,name="HttpApiCallWithSign")
})
public abstract class EngineExecuteTarget {

	
	public  enum TargetType{
		
		HttpApiCall,HttpApiCallWithSign;
		
	}

	public abstract TargetType  getType();


	private String delay;


	private boolean  check;

	public boolean isDoubleCheck(){
		return check;
	};

	public void setDoubleCheck(boolean sign){
		this.check=sign;
	};

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay=delay;
	}


}
