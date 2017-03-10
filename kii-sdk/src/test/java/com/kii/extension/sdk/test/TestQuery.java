package com.kii.extension.sdk.test;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.FieldType;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

public class TestQuery extends TestTemplate{


	@Autowired
	private ObjectMapper mapper;



	@Test
	public void test() throws JsonProcessingException {


		QueryParam param=ConditionBuilder.andCondition().equal("value","1").fieldExist("val", FieldType.INTEGER).getFinalCondition().asc().orderBy("value").build();


		String json=mapper.writeValueAsString(param);


		System.out.println(json);

	}
	
	@Test
	public void testPager() {
		
		AbstractDataAccess.KiiBucketPager pager = AbstractDataAccess.KiiBucketPager.getInstance("2_3");
		
		assertEquals(pager.getStart(), 2);
		assertEquals(pager.getSize(), 3);
		
		pager = AbstractDataAccess.KiiBucketPager.getInstance("4");
		
		assertEquals(pager.getStart(), 0);
		assertEquals(pager.getSize(), 4);
		
		pager = AbstractDataAccess.KiiBucketPager.getInstance("2/3");
		
		assertEquals(pager.getStart(), 2);
		assertEquals(pager.getSize(), 3);
	}



}
