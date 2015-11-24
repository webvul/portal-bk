package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.annotation.BindAppByName;
import com.kii.beehive.portal.store.entity.usersync.ExecuteResult;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal")
@Component
public class UserSyncMsgDao extends AbstractDataAccess<SupplierPushMsgTask>{

	public  static final Integer RETRY_NUM=5;


	public void addUserSyncMsg(SupplierPushMsgTask msg){


		String id=super.addKiiEntity(msg);
		msg.setId(id);

	}


	public List<SupplierPushMsgTask> getUnfinishMsgList(){

		QueryParam query= ConditionBuilder.newCondition()
				.equal("result", ExecuteResult.Working)
				.getFinalCondition()
				.orderBy("_created")
				.build();


		return super.fullQuery(query);

	}

	public void successSupplier(String supplierID,int retry,String id){

		Map<String,Object> map=new HashMap<>();

		map.put(supplierID,100+(RETRY_NUM-retry));

		super.updateEntity(map, id);

	}


	public void recordRetrySupplier(String supplierID,int retry,String id){

		Map<String,Object> map=new HashMap<>();

		map.put(supplierID,retry);

		super.updateEntity(map, id);

	}


	public void updateTaskStatus(String id,boolean sign){

		SupplierPushMsgTask task=super.getObjectByID(id);

		Map<String,Integer> retryRecord=task.getRetryRecord();


		Map<String,Object> map=new HashMap<>();


		int successNum=0;
		int failNum=0;

		for(Map.Entry<String,Integer> entry:retryRecord.entrySet()){

			int val=entry.getValue();
			if(val>=100){
				successNum++;
				continue;
			}
			if(val<=0){
				failNum++;
				continue;
			}

			if(!sign){
				val--;
				map.put(entry.getKey(),val);
				if(val<=0){
					failNum++;
				}
			}

		}

		ExecuteResult result=null;
		if(successNum==retryRecord.size()){
			result=ExecuteResult.Success;
		}else if(failNum+successNum==retryRecord.size()){
			 result=ExecuteResult.Finish;
		 }else{
			result=ExecuteResult.Working;
		}

		map.put("result",result);

		super.updateEntityWithVersion(map, id, task.getVersion());
	}

	@Override
	protected Class<SupplierPushMsgTask> getTypeCls() {
		return SupplierPushMsgTask.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("userSyncMsgQueue");
	}
}
