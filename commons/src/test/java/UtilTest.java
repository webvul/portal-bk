import static junit.framework.TestCase.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class UtilTest {


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
