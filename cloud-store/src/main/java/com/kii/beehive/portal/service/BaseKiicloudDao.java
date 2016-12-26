package com.kii.beehive.portal.service;

import java.util.Map;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.extension.sdk.service.AbstractDataAccess;

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
