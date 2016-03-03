package com.kii.extension.ruleengine.sdk.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.ruleengine.sdk.query.ConditionBuilder;
import com.kii.extension.ruleengine.sdk.query.FieldType;
import com.kii.extension.ruleengine.sdk.query.QueryParam;

public class TestQuery extends TestTemplate{


	@Autowired
	private ObjectMapper mapper;



	@Test
	public void test() throws JsonProcessingException {


		QueryParam param=ConditionBuilder.andCondition().equal("value","1").fieldExist("val", FieldType.INTEGER).getFinalCondition().asc().orderBy("value").build();


		String json=mapper.writeValueAsString(param);


		System.out.println(json);

	}



}
