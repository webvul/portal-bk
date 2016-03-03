package com.kii.extension.ruleengine.sdk.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserQuery extends QueryParam {

	@Override
	@JsonProperty("userQuery")
	public BucketClause getBucketQuery() {
		return super.getBucketQuery();
	}
	
	
}
