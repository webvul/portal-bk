package com.kii.beehive.portal.store.entity;

import java.util.Map;

import com.kii.extension.sdk.entity.KiiEntity;

public class SupplierPushMsg extends KiiEntity{

	private String msgContent;

	private Map<String,Integer> retryNumber;

	private Map<String,Boolean> isFinish;

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Map<String, Integer> getRetryNumber() {
		return retryNumber;
	}

	public void setRetryNumber(Map<String, Integer> retryNumber) {
		this.retryNumber = retryNumber;
	}

	public Map<String, Boolean> getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(Map<String, Boolean> isFinish) {
		this.isFinish = isFinish;
	}
}
