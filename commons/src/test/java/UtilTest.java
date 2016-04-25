import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.kii.beehive.portal.auth.UrlTemplateVerify;

public class UtilTest {


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
