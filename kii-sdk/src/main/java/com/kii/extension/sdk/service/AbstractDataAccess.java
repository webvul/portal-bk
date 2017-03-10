package com.kii.extension.sdk.service;


import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.CreateResponse;
import com.kii.extension.sdk.entity.KiiEntity;
import com.kii.extension.sdk.entity.UpdateResponse;
import com.kii.extension.sdk.exception.ObjectNotFoundException;
import com.kii.extension.sdk.exception.StaleVersionedObjectException;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;

public abstract class AbstractDataAccess<T> {
	
	
	private final BucketInfo bucketInfo = getBucketInfo();
	;
	private final Class<T> typeCls = getTypeCls();
	@Autowired
	private DataService service;
	
	protected Class<T> getTypeCls() {

		ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
		return  (Class<T>) type.getActualTypeArguments()[0];
		
	}

	protected abstract BucketInfo getBucketInfo();

	protected BucketInfo getBucketInfoInstance() {
		return bucketInfo;
	}

	public  boolean checkExist(String id){

		try {
			return service.checkObjectExist(id, bucketInfo);
		}catch(ObjectNotFoundException e){
			return false;
		}
	}

	
	protected  T fillInsertEntity(T entity){
	
		return  entity;
	}
	
	
	protected  T fillUpdateEntity(T entity){
	
		return entity;
	}
	
	protected Map<String,Object> fillUpdateMap(Map<String,Object> entity){
		return entity;
	}
	
	public  T  getObjectByID(String id){
		return service.getObjectByID(id, bucketInfo, typeCls);
	}

	public UpdateResponse addEntity(T  entity,String id){
		
		
		return  service.fullUpdateObject(id, fillInsertEntity(entity), bucketInfo);
	}


	public CreateResponse addEntity(T  entity){
		return  service.createObject(fillInsertEntity(entity), bucketInfo);
	}

	public <E extends KiiEntity> String addKiiEntity(E  entity){

		if(entity.getId()==null){
			CreateResponse resp=service.createObject(fillInsertEntity((T) entity), bucketInfo);

			entity.setId(resp.getObjectID());

			return resp.getObjectID();

		}else {

			UpdateResponse resp= service.fullUpdateObject(entity.getId(), fillUpdateEntity((T) entity), bucketInfo);
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

		return service.fullUpdateObject(id, fillUpdateEntity(entity), bucketInfo);

	}


	public UpdateResponse updateEntityAllWithVersion(T entity,String id,int version){

		return service.fullUpdateObjectWithVersion(id, fillUpdateEntity(entity), bucketInfo, String.valueOf(version));

	}

	public <E extends KiiEntity> UpdateResponse updateEntityAll(E entity){

		return service.fullUpdateObject(entity.getId(), fillUpdateEntity((T) entity), bucketInfo);

	}


	public <E extends KiiEntity> UpdateResponse updateEntityAllWithVersion(E entity,int version){

		return service.fullUpdateObjectWithVersion(entity.getId(), fillUpdateEntity((T)entity), bucketInfo, String.valueOf(version));

	}

	public String updateEntity(Map<String,Object> entity,String id){

		return service.updateObject(id, fillUpdateMap(entity), bucketInfo);

	}


	public String updateEntityWithVersion(Map<String,Object> entity, String id, int version){

		return service.updateObjectWithVersion(id, fillUpdateMap(entity), bucketInfo, String.valueOf(version));

	}

	public  String updateEntity(T entity,String id){

		return service.updateObjectWithEntity(id, fillUpdateEntity(entity), bucketInfo);

	}


	public  String updateEntityWithVersion(T  entity,String id,int version){

		return service.updateObjectWithVersionWithEntity(id, fillUpdateEntity(entity), bucketInfo, String.valueOf(version));

	}

	public List<T> query(QueryParam queryParam){

		return service.query(queryParam, typeCls, bucketInfo);

	}


	public void iterateEntitys(QueryParam queryParam, Consumer<T>  callback){


		do {

			List<T> list=service.query(queryParam, typeCls, bucketInfo);
			list.forEach(callback);

		}while(queryParam.getPaginationKey()!=null);


	}
	public List<T> fullQuery(QueryParam queryParam){

		List<T>  result=new ArrayList<T>();

		do {

			List<T> list=service.query(queryParam, typeCls, bucketInfo);
			result.addAll(list);

		}while(queryParam.getPaginationKey()!=null);

		return result;

	}
	
	public List<T> pagerQuery(QueryParam queryParam,KiiBucketPager pager){
		
		if(pager==null){
			return fullQuery(queryParam);
		}
		
		List<T>  result=new ArrayList<T>();
		int sum=pager.getSum();
		do {
			
			List<T> list=service.query(queryParam, typeCls, bucketInfo);
			result.addAll(list);
			
		}while(queryParam.getPaginationKey()!=null&&result.size()<sum);
		
		return result.subList(pager.getStart(),sum);
		
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
	
	
	public static class KiiBucketPager{
		
		private static Pattern pagerPat = Pattern.compile("^((\\d+)[\\/\\_]?)?(\\d+)$");
		
		
		private int start=0;
		
		private int size=0;
		
		
		public static KiiBucketPager getInstance(String sign){
			
			if(StringUtils.isBlank(sign)){
				return null;
			}
			
			Matcher matcher=pagerPat.matcher(sign);
			
			if(matcher.find()){
				
				KiiBucketPager pager=new KiiBucketPager();
				String a = matcher.group(2);
				String b = matcher.group(3);
				
				pager.size=Integer.parseInt(b);
				
				if(StringUtils.isNotBlank(a)){
					pager.start=Integer.parseInt(a);
				}
				
				return pager;
			}else{
				return null;
			}
		}
		
		public int getStart() {
			return start;
		}
		
		public void setStart(int start) {
			this.start = start;
		}
		
		public int getSize() {
			return size;
		}
		
		public void setSize(int size) {
			this.size = size;
		}
		
		public int getSum() {
			return start+size;
		}
	}
	
	
}
