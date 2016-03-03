package com.kii.beehive.portal.store.entity.usersync;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kii.extension.sdk.entity.KiiEntity;

public class SupplierPushMsgTask extends KiiEntity{



	private UserSyncMsg msgContent;

	private String sourceSupplier;

	private Map<String,Integer> retryRecord=new HashMap<>();

//	private Set<String> finishSet=new HashSet<>();

	private ExecuteResult result=ExecuteResult.Working;

	private Date finishTime;

	public UserSyncMsg getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(UserSyncMsg msgContent) {
		this.msgContent = msgContent;
	}

	@JsonAnyGetter
	public Map<String, Integer> getRetryRecord() {
		return retryRecord;
	}

	@JsonAnySetter
	public void setRetryRecord(String supplierID,int retryNum) {
		this.retryRecord.put(supplierID,retryNum);
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getSourceSupplier() {
		return sourceSupplier;
	}

	public void setSourceSupplier(String sourceSupplier) {
		this.sourceSupplier = sourceSupplier;
	}

	public ExecuteResult getResult() {
		return result;
	}

	public void setResult(ExecuteResult result) {
		this.result = result;
	}

	@JsonIgnore
	public int getRetryCount(String supplierID) {
		return retryRecord.get(supplierID);
	}

	@JsonIgnore
	public void countdownRetryCount(String supplierID){
		retryRecord.compute(supplierID,(id,currVal)->currVal-- );
	}
}
