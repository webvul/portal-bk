package com.kii.extension.sdk.service;


import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.CreateResponse;
import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.UpdateResponse;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataAccess<T> {


	protected abstract Class<T>  getTypeCls();

	protected abstract BucketInfo getBucketInfo();

	private final BucketInfo bucketInfo=getBucketInfo();

	private final Class<T> typeCls=getTypeCls();


	@Autowired
	private DataService service;


	protected  T  getObjectByID(String id){

		return service.getObjectByID(id,bucketInfo,typeCls);
	}

	protected UpdateResponse addEntity(T  entity,String id){
		return  service.fullUpdateObject(id, entity, bucketInfo);
	}


	protected CreateResponse addEntity(T  entity){
		return  service.createObject(entity, bucketInfo);
	}

	protected <E extends KiiEntity> String addKiiEntity(E  entity){

		if(entity.getId()==null){
			CreateResponse resp=service.createObject(entity, bucketInfo);

			return resp.getObjectID();

		}else {

			UpdateResponse resp= service.fullUpdateObject(entity.getId(), entity, bucketInfo);
			return entity.getId();
		}
	}


	protected void removeEntity(String id){
		service.removeObject(id,bucketInfo);
	}


	protected void removeEntityWithVersion(String id,int version){
		service.removeObjectWithVersion(id, String.valueOf(version), bucketInfo);
	}

	protected UpdateResponse updateEntityAll(T entity,String id){

		return service.fullUpdateObject(id, entity, bucketInfo);

	}


	protected UpdateResponse updateEntityAllWithVersion(T entity,String id,int version){

		return service.fullUpdateObjectWithVersion(id, entity, bucketInfo, String.valueOf(version));

	}

	protected <E extends KiiEntity> UpdateResponse updateEntityAll(E entity){

		return service.fullUpdateObject(entity.getId(), entity, bucketInfo);

	}


	protected <E extends KiiEntity> UpdateResponse updateEntityAllWithVersion(E entity,int version){

		return service.fullUpdateObjectWithVersion(entity.getId(), entity, bucketInfo, String.valueOf(version));

	}

	protected String updateEntity(Map<String,Object> entity,String id){

		return service.updateObject(id, entity, bucketInfo);

	}


	protected String updateEntityWithVersion(Map<String,Object> entity, String id, int version){

		return service.updateObjectWithVersion(id, entity, bucketInfo, String.valueOf(version));

	}

	protected <T> String updateEntity(T entity,String id){

		return service.updateObjectWithEntity(id, entity, bucketInfo);

	}


	protected <T> String updateEntityWithVersion(T  entity,String id,int version){

		return service.updateObjectWithVersionWithEntity(id, entity, bucketInfo, String.valueOf(version));

	}

	protected List<T> query(QueryParam queryParam){

		return service.query(queryParam, typeCls, bucketInfo);

	}

	protected List<T> fullQuery(QueryParam queryParam){

		List<T>  result=new ArrayList<T>();

		do {

			List<T> list=service.query(queryParam, typeCls, bucketInfo);
			result.addAll(list);

		}while(queryParam.getPaginationKey()!=null);

		return result;

	}

	protected List<T> getEntitys(String[] ids){

		QueryParam query= ConditionBuilder.newCondition().In("_id",ids).getFinalCondition().build();


		return fullQuery(query);

	}

	protected List<T> getAll(){

		QueryParam query= ConditionBuilder.getAll().getFinalCondition().build();


		return fullQuery(query);

	}

	protected List<T> getEntitys(String field, Object[] values) {

		return getEntitys(field, Arrays.asList(values));
	}

	protected List<T> getEntitys(String field, List<Object> values) {

		List<T> resultList = new ArrayList<T>();

		if(values == null || values.size() == 0) {
			return resultList;
		}

		// the "In" query condition only can put 200 elements at most
		int size = values.size();
		for(int i = 0; i < size; i += 200) {
			int endIndex = (i + 200) >= size? size : i + 200;
			List<Object> tempValues = values.subList(i, endIndex);

			QueryParam query= ConditionBuilder.newCondition().In(field, tempValues).getFinalCondition().build();
			List<T> tempList = fullQuery(query);
			resultList.addAll(tempList);
		}

		return resultList;
	}

	protected T getEntity(String field, String value) {

		List<T> result = this.getEntitys(field, value);

		if(result != null && result.size() >= 0) {
			return result.get(0);
		}

		return null;
	}

	protected List<T> getEntitys(String field, String value) {

		QueryParam query= ConditionBuilder.newCondition().equal(field, value).getFinalCondition().build();
		List<T> result = fullQuery(query);

		return result;
	}

}
