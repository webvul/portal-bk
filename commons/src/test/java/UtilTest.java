import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.portal.auth.UrlTemplateVerify;
import com.kii.extension.tools.CronGeneral;
import com.kii.extension.tools.JsonToFlat;

public class UtilTest {


	private Pattern codeP=Pattern.compile("<code>([^<]+)",Pattern.MULTILINE);
	private Pattern mobileP=Pattern.compile("\\<desmobile\\>([^<]+)",Pattern.MULTILINE);
	private Pattern msgP=Pattern.compile("\\<msgid\\>([^<]+)",Pattern.MULTILINE);




	private ObjectMapper mapper=new ObjectMapper();


	@Test
	public void testMax() throws IOException {

		Map<String,Object> val=new HashMap<>();

		BigInteger  big=new BigInteger("92233720368547758079223372036854775807");
		val.put("long",big);
		val.put("double",1.0e15);

		String json=mapper.writeValueAsString(val);

		System.out.println(json);

		val=mapper.readValue(json,Map.class);

		BigInteger v= (BigInteger) val.get("long");

		double d=(double)val.get("double");

		System.out.println(v);
	}
	@Test
	public void testReplace(){

		String a="abc@agc#code";

		assertEquals("abc.agc.code", StringUtils.replacePattern(a,"\\W","."));

	}
	@Test
	public void testCron(){

		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.MINUTE,60);

		String cron= CronGeneral.getCurrentCron(cal);

		System.out.println(cron);

	}

	@Test
	public void testPattern(){

		String xml="\t<?xml version=\"1.0\" encoding=\"gbk\"?>\n" +
				"<response>\n" +
				"<code>03</code>\n" +
				"<message>\n" +
				"\t<desmobile>13900000000</desmobile>\n" +
				"\t<msgid>200811041234253654785</msgid>\n" +
				"</message>\n" +
				"</response>";

		Matcher  match=codeP.matcher(xml);

		assertEquals(true,match.find());

		assertEquals("03",match.group(1));

	}

	@Test
	public void testJsonFlat() throws IOException {

		Map<String,Object> input=new HashMap<>();
		input.put("a",123);

		Map<String,Object> one=new HashMap<>();
		one.put("one","abc");
		List<String> lista=new ArrayList();
		lista.add("x");
		lista.add("y");
		lista.add("z");

		Set<Integer> setb=new HashSet<>();
		setb.add(1);
		setb.add(2);
		setb.add(3);

		Map<String,Object> two=new HashMap<>();
		two.put("two",2);
		two.put("set",setb);

		one.put("three",two);
		one.put("two",lista);

		input.put("one",one);

		input.put("c","abcdef");

		 ObjectMapper mapper=new ObjectMapper();

		String json=mapper.writeValueAsString(input);

		System.out.println(json);


		String newJson= JsonToFlat.flatJson(json);


		Map<String,Object> newMap=mapper.readValue(newJson,Map.class);

		System.out.println(newMap);


	}


	@Test
	public void testXor(){

		boolean a1=true;
		boolean b1=true;


		boolean a2=false;
		boolean b2=false;

		assertFalse(a1^b1);
		assertTrue(a1^b2);
		assertTrue(a2^b1);
		assertFalse(a2^b2);

	}

	@Test
	public void testUrlMatch(){

		assertTrue(UrlTemplateVerify.verfiyUrlTemplate("/**","/abc"));

		assertTrue(UrlTemplateVerify.verfiyUrlTemplate("/**","/abc/sys"));

		assertTrue(UrlTemplateVerify.verfiyUrlTemplate("/**","/a/b/c"));

		assertTrue(UrlTemplateVerify.verfiyUrlTemplate("/*","/abc"));


//		assertTrue(UrlTemplateVerify.verfiyUrlTemplate("/[a|b]","/a"));

	}

	@Test
	public void testSetHash(){
		Set<String>  set=new HashSet<>();
		set.add("b");
		set.add("a");
		set.add("C");

		System.out.println(set.toString());


	}
	@Test
	public void testMap(){


		Map<String,Integer> map=new HashMap<>();

		for(int i=0;i<10;i++){
			map.put("key"+i,i);
		}

		assertEquals(1,map.get("key1").intValue());

		Set<String> ids=map.keySet();

		Set<String>  newIDs=new HashSet<>();
		for(int i=1;i<10;i+=2){
			newIDs.add("key"+i);
		}

		ids.removeAll(newIDs);

		assertEquals(null,map.get("key1"));

	}


}
