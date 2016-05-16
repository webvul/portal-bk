package com.kii.extension.ruleengine.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kii.extension.ruleengine.drools.ExpressConvert;

public class TestRuleGeneral {



	ExpressConvert replace=new ExpressConvert();

	@Test
	public void test(){

		String template=" $p{abc.foo}i and $p{abc.bar} or $e{name.xyz} ";

		String result=replace.convertExpress(template);


		assertEquals("  numValue(\"abc.foo\")  and  value(\"abc.bar\")  or  $ext.value(\"name.xyz\")  ",result );


		result=replace.convertRightExpress(template,true);

		assertEquals("  $status.getNumValue(\"abc.foo\")  and  $status.getValue(\"abc.bar\")  or  $ext.getValue(\"name.xyz\")  ",result );

	}

	@Test
	public void testCompute(){

		String templace=" ($s{abc.foo} *  10.3 + $p{abc.bar}) >= $e{xyz} ";

		String result=replace.convertExpress(templace);


		assertEquals("( values[\"abc.foo\"]  *  10.3 +  values[\"abc.bar\"] ) >=  ext.params[\"xyz\"] ",result );




	}


}
