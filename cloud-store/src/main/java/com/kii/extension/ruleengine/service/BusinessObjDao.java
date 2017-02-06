package com.kii.extension.ruleengine.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.PortalTokenBindTool;
import com.kii.extension.ruleengine.store.trigger.BusinessDataObject;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
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
	
}
