package com.kii.beehive.business.ruleengine.entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.Express;

public class SingleObjEngineSource implements EngineSourceElement {

	
	@JsonUnwrapped
	private Express express=new Express();
	
	private SingleObject businessObj;
	
	@JsonUnwrapped
	public SingleObject getBusinessObj() {
		return businessObj;
	}
	
	public void setBusinessObj(SingleObject businessObj) {
		this.businessObj = businessObj;
	}
	
	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}


	@Override
	public SourceElementType getType() {
		return SourceElementType.object;
	}
	
	@JsonIgnore
	public BusinessDataObject getObjInstance(){
		
		return businessObj.getBusinessObj();
	}
}
