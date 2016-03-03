package com.kii.extension.ruleengine.sdk.query;


import com.kii.extension.ruleengine.sdk.query.condition.All;

public class BucketClause {

	private Condition clause=new All();

	private String orderBy;
	
	private boolean descending;

	public BucketClause(){


	}

	public BucketClause(BucketClause inst){
		this.clause=inst.clause;
		this.orderBy=inst.orderBy;
		this.descending=inst.descending;
	}

	public Condition getClause() {
		return clause;
	}

	public void setClause(Condition clause) {
		this.clause = clause;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean descending) {
		this.descending = descending;
	}
	
	
	
}
