package com.kii.beehive.business;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.ruleengine.ExpressCompute;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;

public class TestRuleengine extends TestTemplate {

	@Autowired
	private ExpressCompute  compute;


	@Test
	public void testSimple(){

		Condition cond= ConditionBuilder.andCondition().equal("foo",123).greatAndEq("bar","$(target)").getConditionInstance();


		Map<String,Object> param=new HashMap<>();
		param.put("foo",123);
		param.put("bar",100);
		param.put("target",90);

		assertTrue(compute.doExpress(cond,param));

		param.put("target",100);

		assertTrue(compute.doExpress(cond,param));

		param.put("target",101f);

		assertFalse(compute.doExpress(cond,param));

	}
}
