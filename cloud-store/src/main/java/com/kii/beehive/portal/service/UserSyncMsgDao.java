package com.kii.beehive.portal.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.usersync.ExecuteResult;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.context.TokenBindTool;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool",tokenBind = TokenBindTool.BindType.Custom,customBindName = PortalTokenBindTool.PORTAL_OPER )
@Component
public class UserSyncMsgDao extends AbstractDataAccess<SupplierPushMsgTask>{

	public  static final Integer RETRY_NUM=5;


	public void addUserSyncMsg(SupplierPushMsgTask msg){


		String id=super.addKiiEntity(msg);
		msg.setId(id);

	}


	public List<SupplierPushMsgTask> getUnfinishMsgList(){

		QueryParam query= ConditionBuilder.newCondition()
				.equal("task", ExecuteResult.Working)
				.getFinalCondition()
				.orderBy("_created")
				.build();


		return super.fullQuery(query);

	}

	public void successSupplier(String supplierID,int retry,String id){

		Map<String,Object> map=new HashMap<>();

		map.put(supplierID,100+(RETRY_NUM-retry));
		map.put("finishTime",new Date());

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

		int successNum=0;
		int failNum=0;

		Map<String,Object> map=new HashMap<>();

		for(Map.Entry<String,Integer> entry:retryRecord.entrySet()){

			int val=entry.getValue();
			if(val>=100){
				successNum++;
				continue;
			}
			//Fix source supplier bug,still don't why .
			if(entry.getKey().equals(task.getSourceSupplier())){
				successNum++;
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


		if(successNum==retryRecord.size()){
			map.put("task",ExecuteResult.Success);
			map.put("finishTime",new Date());

		}else if(failNum+successNum==retryRecord.size()){
			map.put("task",ExecuteResult.Finish);
			map.put("finishTime",new Date());

		}else{
			map.put("task",ExecuteResult.Working);
		}


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
