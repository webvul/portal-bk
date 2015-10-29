package com.kii.extension.sdk.query.condition;

import javax.xml.bind.annotation.XmlTransient;

import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionType;

public abstract class LogicCol implements Condition {

	public LogicCol() {

	}
	


	public abstract LogicCol addClause(Condition clause);

}
