package com.kii.extension.ruleengine.store.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingSource implements SourceElement {
	
	private SingleThing thing;
	
	@JsonUnwrapped
	public SingleThing getThing() {
		return thing;
	}
	
	public void setThing(SingleThing thing) {
		this.thing = thing;
	}
	
	@JsonUnwrapped
	private Express express=new Express();


	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}


	@Override
	public SourceElementType getType() {
		return SourceElementType.thing;
	}
	
	
	@JsonIgnore
	public BusinessDataObject getBusinessObj(){
		
		return thing.getBusinessObj();
	}
}
