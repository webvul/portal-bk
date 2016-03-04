package com.kii.extension.ruleengine.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kii.extension.ruleengine.drools.ExpressConvert;

public class TestRuleGeneral {



	ExpressConvert replace=new ExpressConvert();

	@Test
	public void test(){

		String templace=" $s{abc.foo} and $p{abc.bar} or $e{xyz} ";

		String result=replace.convertExpress(templace);


		assertEquals("  values[\"abc.foo\"]  and  values[\"abc.bar\"]  or  ext.params[\"xyz\"]  ",result );




	}
}
