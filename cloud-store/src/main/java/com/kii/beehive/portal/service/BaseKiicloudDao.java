package com.kii.beehive.portal.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.InvalidEntryStatusException;
import com.kii.beehive.portal.store.entity.PortalEntity;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.None,customBindName = PortalTokenBindTool.PORTAL_OPER )
public abstract  class  BaseKiicloudDao<T extends PortalEntity> extends AbstractDataAccess<T>{
	
	
	protected QueryParam getQueryWithNonDeleteSign(QueryParam query){
		
		Condition condition=query.getBucketQuery().getClause();
		
		Condition andCond= ConditionBuilder.andCondition().addSubClause(condition).getConditionInstance();
		
		query.getBucketQuery().setClause(andCond);
		
		return query;
		
	}
	
	public List<T>  getAllEntity(){
		
		QueryParam query=ConditionBuilder.newCondition().equal("status", PortalEntity.EntityStatus.enable.name()).getFinalQueryParam();
		return super.fullQuery(query);
	}
	
	
	public T disableEntity(String id){
		
		T entity=super.getObjectByID(id);
		if(entity.getStatus()!= PortalEntity.EntityStatus.enable){
			throw new InvalidEntryStatusException(PortalEntity.class.getName(),"status",entity.getStatus().name());
			
		}
				
		updateEntity(Collections.singletonMap("status", PortalEntity.EntityStatus.disable), id);
		
		return entity;
	}
	
	public T enableEntity(String id){
		
		T entity=super.getObjectByID(id);
		if(entity.getStatus()!= PortalEntity.EntityStatus.disable){
			throw new InvalidEntryStatusException(PortalEntity.class.getName(),"status",entity.getStatus().name());
			
		}
		updateEntity(Collections.singletonMap("status", PortalEntity.EntityStatus.enable), id);
		
		return entity;
	}
	
	public T deleteEntity(String id){
		
		T entity=super.getObjectByID(id);
		if(entity.getStatus()== PortalEntity.EntityStatus.deleted){
			throw new InvalidEntryStatusException(PortalEntity.class.getName(),"status",entity.getStatus().name());
			
		}
		updateEntity(Collections.singletonMap("status", PortalEntity.EntityStatus.deleted), id);
		
		return entity;
	}
	
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
