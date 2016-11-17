package com.kii.extension.ruleengine.test;

import static junit.framework.TestCase.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.kii.extension.ruleengine.drools.ExpressConvert;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;

public class TestUtil {




	@Test
	public void testFullExpress(){

		ExpressConvert  convert=new ExpressConvert();

		String express="$p{foo.bar}+ml.score($p{one},$e{one.two})*$p{a['b']}>$e{a.b.c} ";

		String output=convert.convertExpress(express);

		System.out.println(output);

		

	}



	@Test
	public void testExpress(){


		Map<String,Object> map=new HashMap<>();

		Entry entry=new Entry();
		entry.setVal("val");
		Entry item=new Entry();
		item.setVal("item");
		item.setArray(new String[]{"one","two","three"});

		Map<String,Object> subMap=new HashMap<>();
		subMap.put("item",item);
		subMap.put("one",1);

		entry.setMap(subMap);

		entry.setArray(new Entry[]{item});

		map.put("entry",entry);

		map.put("val","val");

		map.put("map",subMap);

		ThingStatusInRule status=new ThingStatusInRule("abc");
		status.setValues(map);

		assertEquals(1, status.getValue("entry.map['one']"));
		assertEquals("item",status.getValue("entry.map['item'].val"));
		assertEquals("val",status.getValue("entry.val"));
		assertEquals("val",status.getValue("val"));
		assertEquals("item",status.getValue("entry.array[0].val"));
		assertEquals("two",status.getValue("entry.map['item'].array[1]"));
		assertEquals("two",status.getValue("map['item'].array[1]"));

	}

	public static class Entry{

		private String val;

		private Map<String,Object>  map=new HashMap<>();

		private Object[] array;

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}

		public Map<String, Object> getMap() {
			return map;
		}

		public void setMap(Map<String, Object> map) {
			this.map = map;
		}

		public Object[] getArray() {
			return array;
		}

		public void setArray(Object[] array) {
			this.array = array;
		}
	}

}
