package com.kii.extension.ruleengine;

import java.util.Arrays;
import java.util.List;

import com.kii.extension.ruleengine.store.trigger.condition.InCollect;
import com.kii.extension.ruleengine.store.trigger.condition.LogicCol;
import com.kii.extension.ruleengine.store.trigger.condition.OrLogic;
import com.kii.extension.ruleengine.store.trigger.Condition;
import com.kii.extension.ruleengine.store.trigger.condition.All;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.ruleengine.store.trigger.condition.Like;
import com.kii.extension.ruleengine.store.trigger.condition.NotLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Range;


public class TriggerConditionBuilder {
	
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
	
	
	public TriggerConditionBuilder equal(String field, Object value) {
		if(value!=null) {
			Equal eq = new Equal(field, value);
			fill(eq);
		}
		return this;
	}

	public TriggerConditionBuilder equalExpress(String field, String express) {

		Equal eq = new Equal(field, null);
		eq.setExpress(express);
		fill(eq);

		return this;
	}



	public TriggerConditionBuilder In(String field, List<?> objList) {
		InCollect q = new InCollect();
		q.setField(field);
		q.setValues(objList);

		fill(q);
		return this;
	}

	public TriggerConditionBuilder In(String field, Object[] objArray) {
		InCollect q = new InCollect();
		q.setField(field);

		q.setValues(Arrays.asList(objArray));

		fill(q);
		return this;
	}

	public TriggerConditionBuilder like(String field, String value) {
		Like q = new Like();
		q.setField(field);
		q.setValue(value);

		fill(q);
		return this;
	}

	public TriggerConditionBuilder likeExp(String field, String value) {
		Like q = new Like();
		q.setField(field);
		q.setExpress(value);

		fill(q);
		return this;
	}

	private <T> TriggerConditionBuilder range(String field, T lower, boolean withLower,
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

	private  TriggerConditionBuilder rangeExp(String field, String lower, boolean withLower,
											  String upper, boolean withUpper) {

		Range q = new Range();
		q.setField(field);
		if (upper != null) {
			q.setUpperExpress(upper);
		}
		if (lower != null) {
			q.setLowerExpress(lower);
		}

		q.setUpperIncluded(withUpper);
		q.setLowerIncluded(withLower);


		fill(q);

		return this;
	}

	public TriggerConditionBuilder addSubClause(TriggerConditionBuilder...  logic) {
		for(TriggerConditionBuilder b:logic){
			if(b.clauses!=null){
				fill(b.clauses);
			}else {
				fill(b.condition);
			}
		}
		return this;
	}
	
	public TriggerConditionBuilder addSubClause(Condition...  logic) {
		for(Condition b:logic){
			fill(b);
		}
		return this;
	}

	public <T> TriggerConditionBuilder less(String field, T value) {
		return range(field, null, false, value, false);

	}

	public <T> TriggerConditionBuilder great(String field, T value) {
		return range(field, value, false, null, false);
	}

	public <T> TriggerConditionBuilder lessAndEq(String field, T value) {
		return range(field, null, false, value, true);
	}

	public <T> TriggerConditionBuilder greatAndEq(String field, T value) {
		return range(field, value, true, null, false);
	}


	public <T> TriggerConditionBuilder lessExp(String field, String value) {
		return rangeExp(field, null, false, value, false);

	}

	public <T> TriggerConditionBuilder greatExp(String field, String value) {
		return rangeExp(field, value, false, null, false);
	}

	public <T> TriggerConditionBuilder lessAndEqExp(String field, String value) {
		return rangeExp(field, null, false, value, true);
	}

	public <T> TriggerConditionBuilder greatAndEqExp(String field, String value) {
		return rangeExp(field, value, true, null, false);
	}

//	public <T> TriggerConditionBuilder betweenIn(String field, T lower, boolean withLower,
//												 T upper, boolean withUpper) {
//		return range(field, lower, withLower, upper, withUpper);
//	}

	
	public static TriggerConditionBuilder andCondition() {
		TriggerConditionBuilder cb=new TriggerConditionBuilder();
		cb.clauses=new AndLogic();
		return cb;
	}

	public static TriggerConditionBuilder orCondition() {
		TriggerConditionBuilder cb=new TriggerConditionBuilder();
		cb.clauses=new OrLogic();
		return cb;
	}


	/**
	 * create simple condition (no logic oper)
	 * @return
	 */
	public static TriggerConditionBuilder newCondition() {
		TriggerConditionBuilder cb=new TriggerConditionBuilder();
		return cb;
	}
	
	/**
	 * create a conition with not oper
	 * @return
	 */
	public static TriggerConditionBuilder notCondition(Condition subCond) {
		TriggerConditionBuilder cb=new TriggerConditionBuilder();
		cb.clauses=new NotLogic();
		cb.clauses.addClause(subCond);
		return cb;
	}

	public static TriggerConditionBuilder notCondition() {
		TriggerConditionBuilder cb=new TriggerConditionBuilder();
		cb.clauses=new NotLogic();
		return cb;
	}
	


	public Condition getConditionInstance() {
		if (clauses==null) {
			return condition;
		} else {
			return clauses;
		}
	}



	public static TriggerConditionBuilder getAll() {
		TriggerConditionBuilder cb=new TriggerConditionBuilder();
		cb.condition=new All();
		return cb;
	}
}
