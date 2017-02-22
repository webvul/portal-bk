package com.kii.beehive.portal.store.entity.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ThingSource implements SourceElement {

	
	@JsonUnwrapped
	private Express express=new Express();
	
	private SingleThing  thing;
	
	@JsonUnwrapped
	public SingleThing getThing() {
		return thing;
	}
	
	public void setThing(SingleThing thing) {
		this.thing = thing;
	}
	
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
