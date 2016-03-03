package com.kii.beehive.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;

@Component
public class SimpleQueryTool {



	public  QueryParam  getSimpleQuery(String field, String value) {
		QueryParam query= ConditionBuilder.newCondition().equal(field, value).getFinalCondition().build();

		return query;
	}



	public QueryParam getEntitys(String field, List<?> values) {

		QueryParam query= ConditionBuilder.newCondition().In(field, values).getFinalCondition().build();

		return query;
	}

	public QueryParam getEntitysByFields(Map<String,Object> fields){

		ConditionBuilder builder= ConditionBuilder.andCondition();

		fields.forEach((k,v)->{


			builder.equal(k,v);
		});

		return builder.getFinalCondition().build();

	}


}
