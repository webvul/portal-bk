package com.kii.beehive.portal.service;

import static com.kii.beehive.portal.service.PortalTokenBindTool.PORTAL_OPER;

import java.util.Map;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = PORTAL_OPER)
public abstract  class  BaseKiicloudDao<T extends PortalEntity> extends AbstractDataAccess<T>{
	
	
	protected  T fillInsertEntity(T entity){
		
		entity.setCreateBy(AuthInfoStore.getUserIDStr());
		entity.setModifyBy(AuthInfoStore.getUserIDStr());
		return  entity;
	}
	
	
	protected  T fillUpdateEntity(T entity){
		
		entity.setModifyBy(AuthInfoStore.getUserIDStr());
		
		return entity;
	}
	
	protected Map<String,Object> fillUpdateMap(Map<String,Object> map){
		map.put("modifyBy", AuthInfoStore.getUserID());
		
		return map;
	}
}
