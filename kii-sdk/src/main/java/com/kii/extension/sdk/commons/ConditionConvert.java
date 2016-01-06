package com.kii.extension.sdk.commons;

import com.kii.extension.sdk.entity.thingif.ConditionExpress;
import com.kii.extension.sdk.entity.thingif.TriggerConditionEntry;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionType;
import com.kii.extension.sdk.query.condition.AndLogic;
import com.kii.extension.sdk.query.condition.Equal;
import com.kii.extension.sdk.query.condition.NotLogic;
import com.kii.extension.sdk.query.condition.OrLogic;
import com.kii.extension.sdk.query.condition.Range;

public class ConditionConvert {


	private static TriggerConditionEntry getExpress(Condition condition){

		ConditionExpress express=new ConditionExpress();

		switch(condition.getType()){
			case and:
				express.setExpress("and");
				for(Condition cond:((AndLogic)condition).getClauses()){
					express.addCondition(convert(cond));
				}
				return express;
			case or:
				express.setExpress("or");
				for(Condition cond:((OrLogic)condition).getClauses()){
					express.addCondition(convert(cond));
				}
				return express;
			default:
				throw new IllegalArgumentException("unsupported condition:"+condition.getType());
		}
	}


	private static TriggerConditionEntry getEntry(Condition condition){

		TriggerConditionEntry entry=new TriggerConditionEntry();

		switch(condition.getType()){

			case not:
				Condition subCond=((NotLogic)condition).getClause();
				if(subCond.getType()== ConditionType.eq) {
					entry = convert(subCond);
					entry.setExpress("!=");
				}else{
					throw new IllegalArgumentException();
				}
				return entry;
			case eq:
				entry.setExpress("=");
				entry.setValue( ( (Equal)condition).getValue());
				entry.setField(((Equal)condition).getField());
				return entry;
			case range:
				Range range=(Range)condition;
				entry.setField(range.getField());
				if(range.getLowerLimit()!=null){
					entry.setValue(range.getLowerLimit());
					if(range.isExistLower()){
						entry.setExpress(">=");
					}else{
						entry.setExpress(">");
					}
				}else if(range.getUpperLimit()!=null){
					entry.setValue(range.getUpperLimit());
					if(range.isExistUpper()){
						entry.setExpress("<=");
					}else{
						entry.setExpress("<");
					}
				}
				return entry;
			default:
				throw new IllegalArgumentException("unsupported condition:"+condition.getType());
		}
	}

	public static final TriggerConditionEntry  convert(Condition condition){

		if(condition.getType()==ConditionType.and||condition.getType()==ConditionType.or){
			return getExpress(condition);
		}else{
			return getEntry(condition);
		}

	}

}
