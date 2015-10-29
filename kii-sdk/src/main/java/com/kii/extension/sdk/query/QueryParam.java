package com.kii.extension.sdk.query;

import com.kii.extension.sdk.query.condition.All;

/**
 * the package of restful's queryParam
 * 
 * @see  http://documentation.kii.com/rest/#data_management-manage_objects-group_scope-query_for_objects
 * @author ethan
 *
 */
public class QueryParam {
	
	/*
	 *   "paginationKey":"asd12ijdfasdfjadfjgk",
  "bestEffortLimit":10

	 */

	private int bestEffortLimit=50;
	
	private String paginationKey;
	
	private BucketClause bucketQuery=new BucketClause();
	

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

	public BucketClause getBucketQuery() {
		return bucketQuery;
	}

	public void setBucketQuery(BucketClause bucketQuery) {
		this.bucketQuery = bucketQuery;
	}



	public void setCondition(Condition cond) {
		if(cond==null){
			cond=new All();
		}
		bucketQuery.setClause(cond);
		
	}


	public static QueryParam clone(QueryParam inst){
		QueryParam param=new QueryParam();
		param.bestEffortLimit=inst.bestEffortLimit;
		param.paginationKey=inst.paginationKey;
		param.bucketQuery=new BucketClause(inst.getBucketQuery());

		return param;

	}

    public static QueryParam generQueryParam(Condition condition){
        QueryParam param=new QueryParam();

        param.setCondition(condition);

        return param;

    }

    public static QueryParam generQueryParam(Condition condition,String orderBy,boolean isDescend){
        QueryParam param=new QueryParam();

        param.setCondition(condition);
        param.getBucketQuery().setOrderBy(orderBy);
        param.getBucketQuery().setDescending(isDescend);
        return param;

    }


    public static QueryParam generAllCondition() {
        QueryParam param=new QueryParam();

        param.setCondition(new All());

        return param;


    }
}
