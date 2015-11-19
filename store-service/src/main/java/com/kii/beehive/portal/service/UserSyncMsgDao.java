package com.kii.beehive.portal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.usersync.ExecuteResult;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class UserSyncMsgDao extends AbstractDataAccess<SupplierPushMsgTask>{

	public void addUserSyncMsg(SupplierPushMsgTask msg){


		super.addKiiEntity(msg);
	}


	public List<SupplierPushMsgTask> getUnfinishMsgList(){

		QueryParam query= ConditionBuilder.newCondition()
				.equal("result", ExecuteResult.Working)
				.getFinalCondition()
				.orderBy("_created")
				.build();


		return super.fullQuery(query);

	}


	public void updateTaskStatus(Map<String,Integer> retryRecord,String id,int version){

		ExecuteResult result=ExecuteResult.Working;


		int successNum=0;
		int failNum=0;
		for(Integer val:retryRecord.values()){
			if(val==100){
				successNum++;
			}
			if(val<=0){
				failNum++;
			}
		}

		if(successNum==retryRecord.size()){
			result=ExecuteResult.Success;
		}else if(failNum+successNum==retryRecord.size()){
			 result=ExecuteResult.Finish;
		 }else{
			result=ExecuteResult.Working;
		}

		Map<String,Object> map=new HashMap<>();

		map.put("result",result);
		map.put("retryRecord",retryRecord);


		super.updateEntityWithVersion(map, id, version);
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
