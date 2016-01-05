package com.kii.extension.sdk.test.thingif;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.entity.thingif.ConditionExpress;
import com.kii.extension.sdk.entity.thingif.EventSourceType;
import com.kii.extension.sdk.entity.thingif.Predicate;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.TriggerConditionEntry;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.service.ObjectMapperFactory;

public class TestConditionSerial {

	private Logger log= LoggerFactory.getLogger(TestConditionSerial.class);

	private ObjectMapper mapper=new ObjectMapperFactory().getObjectMapper();

	@Test
	public void testCond() throws IOException {

		StatePredicate  predicate=new StatePredicate();

		ConditionBuilder cond1= ConditionBuilder.andCondition().equal("foo",1).greatAndEq("bar",3);

		ConditionBuilder cond2= ConditionBuilder.orCondition().equal("abc",1).less("syz","abc");

		Condition condition=ConditionBuilder.andCondition().addSubClause(cond1,cond2).getConditionInstance();

		predicate.setCondition(condition);


		String json=mapper.writeValueAsString(predicate);


		log.info(json);


	}
}
