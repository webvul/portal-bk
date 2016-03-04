package com.kii.extension.sdk.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.CreateResponse;
import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.UpdateResponse;
import com.kii.extension.sdk.exception.StaleVersionedObjectException;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;

public abstract class AbstractDataAccess<T> {


	protected abstract Class<T>  getTypeCls();

	protected abstract BucketInfo getBucketInfo();

	private final BucketInfo bucketInfo=getBucketInfo();

	private final Class<T> typeCls=getTypeCls();


	@Autowired
	private DataService service;


	public  boolean checkExist(String id){
		return service.checkObjectExist(id,bucketInfo);
	}

	public  T  getObjectByID(String id){
		return service.getObjectByID(id, bucketInfo, typeCls);
	}

	public UpdateResponse addEntity(T  entity,String id){
		return  service.fullUpdateObject(id, entity, bucketInfo);
	}


	public CreateResponse addEntity(T  entity){
		return  service.createObject(entity, bucketInfo);
	}

	public <E extends KiiEntity> String addKiiEntity(E  entity){

		if(entity.getId()==null){
			CreateResponse resp=service.createObject(entity, bucketInfo);

			entity.setId(resp.getObjectID());

			return resp.getObjectID();

		}else {

			UpdateResponse resp= service.fullUpdateObject(entity.getId(), entity, bucketInfo);
			return entity.getId();
		}
	}


	public void removeEntity(String id){
		service.removeObject(id,bucketInfo);
	}


	public void removeEntityWithVersion(String id,int version){
		service.removeObjectWithVersion(id, String.valueOf(version), bucketInfo);
	}

	public UpdateResponse updateEntityAll(T entity,String id){

		return service.fullUpdateObject(id, entity, bucketInfo);

	}


	public UpdateResponse updateEntityAllWithVersion(T entity,String id,int version){

		return service.fullUpdateObjectWithVersion(id, entity, bucketInfo, String.valueOf(version));

	}

	public <E extends KiiEntity> UpdateResponse updateEntityAll(E entity){

		return service.fullUpdateObject(entity.getId(), entity, bucketInfo);

	}


	public <E extends KiiEntity> UpdateResponse updateEntityAllWithVersion(E entity,int version){

		return service.fullUpdateObjectWithVersion(entity.getId(), entity, bucketInfo, String.valueOf(version));

	}

	public String updateEntity(Map<String,Object> entity,String id){

		return service.updateObject(id, entity, bucketInfo);

	}


	public String updateEntityWithVersion(Map<String,Object> entity, String id, int version){

		return service.updateObjectWithVersion(id, entity, bucketInfo, String.valueOf(version));

	}

	public <T> String updateEntity(T entity,String id){

		return service.updateObjectWithEntity(id, entity, bucketInfo);

	}


	public <T> String updateEntityWithVersion(T  entity,String id,int version){

		return service.updateObjectWithVersionWithEntity(id, entity, bucketInfo, String.valueOf(version));

	}

	public List<T> query(QueryParam queryParam){

		return service.query(queryParam, typeCls, bucketInfo);

	}

	public List<T> fullQuery(QueryParam queryParam){

		List<T>  result=new ArrayList<T>();

		do {

			List<T> list=service.query(queryParam, typeCls, bucketInfo);
			result.addAll(list);

		}while(queryParam.getPaginationKey()!=null);

		return result;

	}

	public List<T> getEntitys(String[] ids){

		QueryParam query= ConditionBuilder.newCondition().In("_id",ids).getFinalCondition().build();


		return fullQuery(query);

	}

	public List<T> getAll(){

		QueryParam query= ConditionBuilder.getAll().getFinalCondition().build();


		return fullQuery(query);

	}


	public void updateWithVerify(String id,Map<String,Object> update,int retryNumber){

		for(int i=0;i<retryNumber;i++){

			KiiEntity entry= (KiiEntity) this.getObjectByID(id);

			try {
				this.updateEntityWithVersion(update, id, entry.getVersion());
				break;
			} catch (StaleVersionedObjectException e) {
				continue;
			}
		}

	}


	public  Map<String,Object> executeWithVerify(String id, Function<T,Map<String,Object>> function, int retryNumber){

		Map<String,Object> result=null;
		for(int i=0;i<retryNumber;i++){

			T entry= (T) this.getObjectByID(id);

			result=function.apply(entry);

			try {
				int  version=((KiiEntity)entry).getVersion();
				this.updateEntityWithVersion(result , id, version);
				break;
			} catch (StaleVersionedObjectException e) {
				continue;
			}
		}
		return result;

	}


}
