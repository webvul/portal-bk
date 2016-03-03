package com.kii.extension.ruleengine.sdk.entity.thingif;

public class CommandQuery {

	private int bestLimit;

	private String nextPaginationKey;

	public int getBestLimit() {
		return bestLimit;
	}

	public void setBestLimit(int bestLimit) {
		this.bestLimit = bestLimit;
	}

	public String getNextPaginationKey() {
		return nextPaginationKey;
	}

	public void setNextPaginationKey(String nextPaginationKey) {
		this.nextPaginationKey = nextPaginationKey;
	}
}
