package com.kii.beehive.portal.store.entity.usersync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kii.extension.sdk.entity.KiiEntity;

public class SupplierPushMsgTask extends KiiEntity{

	private String msgContent;

	private String sourceSupplier;

	private Map<String,Integer> retryRecord=new HashMap<>();

//	private Set<String> finishSet=new HashSet<>();

	private ExecuteResult result=ExecuteResult.Working;

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Map<String, Integer> getRetryRecord() {
		return retryRecord;
	}

	public void setRetryRecord(Map<String, Integer> retryRecord) {
		this.retryRecord = retryRecord;
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
}
