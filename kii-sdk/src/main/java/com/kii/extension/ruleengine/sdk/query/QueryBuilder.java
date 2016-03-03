package com.kii.extension.ruleengine.sdk.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBuilder {
	
	private final Logger log=LoggerFactory.getLogger(QueryBuilder.class);

	public QueryBuilder(){
//		queryParam.setBestEffortLimit(50);
	}
	
//	private SingleCol condition=new SingleCol();
//	
	private final QueryParam queryParam=new QueryParam();

	private final BucketClause clause=new BucketClause();

	
	//==========================

	public  QueryBuilder(Condition  condition){

		clause.setClause(condition);
		queryParam.setBucketQuery(clause);

	}

	
	//=====================================

	public QueryBuilder bestEffortLimit(int limit){
		queryParam.setBestEffortLimit(limit);
		return this;
	}

	public QueryBuilder orderBy(String field){
		clause.setOrderBy(field);
		return this;
	}

	public QueryBuilder asc(){
		clause.setDescending(false);
		return this;
	}

	public QueryBuilder desc(){
		clause.setDescending(true);
		return this;
	}

	public QueryBuilder pageKey(String key){
		queryParam.setPaginationKey(key);
		return this;
	}

	
	
	public QueryParam build( ){
		return   QueryParam.clone(queryParam);
	}
	
	
}
