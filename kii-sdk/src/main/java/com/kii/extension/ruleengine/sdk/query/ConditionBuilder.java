package com.kii.extension.ruleengine.sdk.query;

import java.util.Arrays;
import java.util.List;

import com.kii.extension.ruleengine.sdk.query.condition.All;
import com.kii.extension.ruleengine.sdk.query.condition.AndLogic;
import com.kii.extension.ruleengine.sdk.query.condition.Equal;
import com.kii.extension.ruleengine.sdk.query.condition.FieldExist;
import com.kii.extension.ruleengine.sdk.query.condition.InCollect;
import com.kii.extension.ruleengine.sdk.query.condition.LogicCol;
import com.kii.extension.ruleengine.sdk.query.condition.NotLogic;
import com.kii.extension.ruleengine.sdk.query.condition.OrLogic;
import com.kii.extension.ruleengine.sdk.query.condition.PrefixLike;
import com.kii.extension.ruleengine.sdk.query.condition.Range;


public class ConditionBuilder {
	
	private LogicCol clauses;
	
	private Condition condition;

	private void fill(Condition newCond){
		if(clauses==null&&condition==null){
			condition=newCond;
		}else if(clauses!=null){
			clauses.addClause(newCond);
		}else if(condition!=null){
			clauses=new AndLogic();
			clauses.addClause(condition);
			clauses.addClause(newCond);
			condition=null;
		}
	}
	
	
	public ConditionBuilder equal(String field, Object value) {
		if(value!=null) {
			Equal eq = new Equal(field, value);
			fill(eq);
		}
		return this;
	}

	public ConditionBuilder fieldExist(String field, FieldType type) {
		FieldExist q = new FieldExist();
		q.setField(field);
		q.setFieldType(type);

		fill(q);
		return this;
	}

	public ConditionBuilder In(String field, List<?> objList) {
		InCollect q = new InCollect();
		q.setField(field);
		q.setValues(objList);

		fill(q);
		return this;
	}

	public ConditionBuilder In(String field, Object[] objArray) {
		InCollect q = new InCollect();
		q.setField(field);

		q.setValues(Arrays.asList(objArray));

		fill(q);
		return this;
	}

	public ConditionBuilder prefixLike(String field, String value) {
		PrefixLike q = new PrefixLike();
		q.setField(field);
		q.setPrefix(value);

		fill(q);
		return this;
	}

	private <T> ConditionBuilder range(String field, T lower, boolean withLower,
			T upper, boolean withUpper) {

		T sign = lower;
		if (lower == null) {
			sign = upper;
		}

		Range q = new Range();
		q.setField(field);
		if (upper != null) {
			q.setUpperLimit(upper);
		}
		if (lower != null) {
			q.setLowerLimit(lower);
		}

		q.setUpperIncluded(withUpper);
		q.setLowerIncluded(withLower);


		fill(q);

		return this;
	}

	public ConditionBuilder addSubClause(ConditionBuilder...  logic) {
		for(ConditionBuilder b:logic){
			if(b.clauses!=null){
				fill(b.clauses);
			}else {
				fill(b.condition);
			}
		}
		return this;
	}

	public <T> ConditionBuilder less(String field, T value) {
		return range(field, null, false, value, false);

	}

	public <T> ConditionBuilder great(String field, T value) {
		return range(field, value, false, null, false);
	}

	public <T> ConditionBuilder lessAndEq(String field, T value) {
		return range(field, null, false, value, true);
	}

	public <T> ConditionBuilder greatAndEq(String field, T value) {
		return range(field, value, true, null, false);
	}

	public <T> ConditionBuilder betweenIn(String field, T lower, boolean withLower,
			T upper, boolean withUpper) {
		return range(field, lower, withLower, upper, withUpper);
	}

	
	public static ConditionBuilder andCondition() {
		ConditionBuilder cb=new ConditionBuilder();
		cb.clauses=new AndLogic();
		return cb;
	}

	public static ConditionBuilder orCondition() {
		ConditionBuilder cb=new ConditionBuilder();
		cb.clauses=new OrLogic();
		return cb;
	}


	/**
	 * create simple condition (no logic oper)
	 * @return
	 */
	public static ConditionBuilder newCondition() {
		ConditionBuilder cb=new ConditionBuilder();
		return cb;
	}
	
	/**
	 * create a conition with not oper
	 * @return
	 */
	public static ConditionBuilder notCondition(Condition subCond) {
		ConditionBuilder cb=new ConditionBuilder();
		cb.clauses=new NotLogic();
		cb.clauses.addClause(subCond);
		return cb;
	}

	public static ConditionBuilder notCondition() {
		ConditionBuilder cb=new ConditionBuilder();
		cb.clauses=new NotLogic();
		return cb;
	}
	
	public QueryBuilder getFinalCondition() {
		if (clauses==null) {
			return new QueryBuilder(condition);
		} else {
			return new QueryBuilder(clauses);
		}
	}

	public Condition getConditionInstance() {
		if (clauses==null) {
			return condition;
		} else {
			return clauses;
		}
	}

	public QueryParam getFinalQueryParam(){
		return this.getFinalCondition().build();
	}


	public static ConditionBuilder getAll() {
		ConditionBuilder cb=new ConditionBuilder();
		cb.condition=new All();
		return cb;
	}
}
