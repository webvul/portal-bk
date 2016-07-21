package com.kii.extension.sdk.query;

import com.kii.extension.sdk.query.condition.All;

/**
 * the package of restful's queryParam
 *
 */
public class ThingQueryParam {
	
	/*
  "bestEffortLimit":10

	 */

	private int bestEffortLimit=50;
	
	private String paginationKey;
	
	private BucketClause thingQuery = new BucketClause();

	public ThingQueryParam(){}
	public ThingQueryParam(int bestEffortLimit, String paginationKey, BucketClause thingQuery){
		this.bestEffortLimit = bestEffortLimit;
		this.paginationKey = paginationKey;
		this.thingQuery = thingQuery;
	}

	public int getBestEffortLimit() {
		return bestEffortLimit;
	}

	public void setBestEffortLimit(int bestEffortLimit) {
		this.bestEffortLimit = bestEffortLimit;
	}

	public String getPaginationKey() {
		return paginationKey;
	}

	public void setPaginationKey(String paginationKey) {
		this.paginationKey = paginationKey;
	}

	public BucketClause getThingQuery() {
		return thingQuery;
	}

	public void setThingQuery(BucketClause thingQuery) {
		this.thingQuery = thingQuery;
	}

	public void setCondition(Condition cond) {
		if(cond==null){
			cond=new All();
		}
		thingQuery.setClause(cond);
		
	}


	public static ThingQueryParam clone(ThingQueryParam inst){
		ThingQueryParam param=new ThingQueryParam();
		param.bestEffortLimit=inst.bestEffortLimit;
		param.paginationKey=inst.paginationKey;
		param.thingQuery=new BucketClause(inst.getThingQuery());

		return param;

	}

    public static ThingQueryParam generQueryParam(Condition condition){
        ThingQueryParam param=new ThingQueryParam();

        param.setCondition(condition);

        return param;

    }

    public static ThingQueryParam generQueryParam(Condition condition, String orderBy, boolean isDescend){
        ThingQueryParam param=new ThingQueryParam();

        param.setCondition(condition);
        param.getThingQuery().setOrderBy(orderBy);
        param.getThingQuery().setDescending(isDescend);
        return param;

    }


    public static ThingQueryParam generAllCondition() {
        ThingQueryParam param=new ThingQueryParam();

        param.setCondition(new All());

        return param;


    }
}
