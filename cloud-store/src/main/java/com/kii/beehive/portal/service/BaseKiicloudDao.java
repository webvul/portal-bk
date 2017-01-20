package com.kii.beehive.portal.service;

import java.util.Map;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.None,customBindName = PortalTokenBindTool.PORTAL_OPER )
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
