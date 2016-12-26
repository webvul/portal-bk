package com.kii.extension.ruleengine.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName = "portal", appBindSource = "propAppBindTool")
public class BusinessObjDao extends AbstractDataAccess<BusinessDataObject> {


	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("triggerBusinessObj");
	}
	
	
	public void addBusinessObj(BusinessDataObject obj){
		
		super.addEntity(obj,obj.getFullID());
	}
	
	public List<BusinessDataObject> getAllBusinessObjs(){
		
		
		List<String>  types=new ArrayList<>();
		types.add(BusinessObjType.Business.name());
		types.add(BusinessObjType.User.name());
		types.add(BusinessObjType.TriggerGroup.name());
		
		QueryParam query= ConditionBuilder.newCondition().In("businessType",types).getFinalQueryParam();
		
		return super.fullQuery(query);
	}
	
	public void saveExtensionValue(String name, Map<String,Object> values){
		
		BusinessDataObject obj=new BusinessDataObject(name,"ext",BusinessObjType.Global);
		
		obj.setData(values);
		
		super.addEntity(obj,obj.getFullID());
	}
	
	
	public Map<String,Object> loadExtensionValue(String name){
	
		String fullID=BusinessObjType.Global.getFullID(name,"ext");
		
		return super.getObjectByID(fullID).getData();
	}
	
	
	public List<BusinessDataObject> loadAllExtension(){
		
		QueryParam query= ConditionBuilder.newCondition().equal("businessType",BusinessObjType.Global.name()).getFinalQueryParam();
		
		return super.fullQuery(query);
	}
	
	public void updateExtensionValue(String name, String key, Object data) {
		BusinessDataObject obj=new BusinessDataObject(name,"ext",BusinessObjType.Global);
		
		obj.setData(Collections.singletonMap(key,data));
		
		super.updateEntityAll(obj,obj.getFullID());
		
	}
}
