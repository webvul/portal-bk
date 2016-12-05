package com.kii.extension.ruleengine.store.trigger;

import java.util.HashMap;
import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class BusinessObject extends KiiEntity {

	private   String  businessObjID;

	private   Map<String,Object> data=new HashMap<>();


	public String getBusinessObjID() {
		return businessObjID;
	}

	public void setBusinessObjID(String businessObjID) {
		this.businessObjID = businessObjID;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
