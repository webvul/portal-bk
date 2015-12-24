//package com.kii.beehive.portal.store;
//
//import static junit.framework.TestCase.assertEquals;
//
//import java.io.IOException;
//import java.util.Base64;
//
//import org.apache.commons.codec.Charsets;
//import org.junit.Test;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.kii.beehive.portal.store.entity.BeehiveUser;
//import com.kii.beehive.portal.store.entity.GlobalThingInfo;
//
//public class TestJson {
//
//	private ObjectMapper mapper=new ObjectMapper();
//
//
//	@Test
//	public void testEncode(){
//
//		String name="中文名";
//
//		String loginName= new String(Base64.getEncoder().encode(name.getBytes(Charsets.UTF_8)));
//
//		System.out.println(loginName);
//	}
//
//	@Test
//	public void testUser() throws IOException{
//
//		String json="{\n\"userName\": \"bob\",\n\"company\": \"test\",\n}";
//
//		json="{  userName: \"john\",\n" +
//				"company: \"test\",\n" +
//				"}";
//
//		BeehiveUser user=mapper.readValue(json, BeehiveUser.class);
//
//
//
//
//
//	}
//
//	@Test
//	public void testSet() throws IOException {
//
//		GlobalThingInfo info=new GlobalThingInfo();
//
//		info.getTags().add("a");
//		info.getTags().add("b");
//
//		String json=mapper.writeValueAsString(info);
//
//		info=mapper.readValue(json, GlobalThingInfo.class);
//
//		assertEquals(2,info.getTags().size());
//
//	}
//}
