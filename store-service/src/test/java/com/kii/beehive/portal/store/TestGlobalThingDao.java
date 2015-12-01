package com.kii.beehive.portal.store;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.helper.PortalTokenService;
import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.beehive.portal.store.entity.Token.PortalTokenType;

public class TestGlobalThingDao extends TestInit {
	
	
	@Autowired
	private GlobalThingDao thingDao;

	@Autowired
	private ThingManager thingManager;

	@Autowired
	private TagIndexDao tagIndexDao;


	@Autowired
	private PortalTokenService  portalToken;

	@Before
	public void addData(){

		portalToken.setToken("test", PortalTokenType.Demo);

		String suf=String.valueOf(System.currentTimeMillis());

		GlobalThingInfo thing=new GlobalThingInfo();
//		thing.setId("001"+suf);
		thing.setVendorThingID("MacAddr1"+suf);
		thing.setKiiAppID("a");
//		thing.setGlobalThingID(thing.get);

		thingDao.addThingInfo(thing);

		GlobalThingInfo info=thingDao.getObjectByID(thing.getGlobalThingID());

//		TokenInfo  token=new TokenInfo("test", PortalTokenService.PortalTokenType.Demo);

		assertEquals("Demo:test",info.getCreateBy());



//		thing.setId("002");
//		thing.setVendorThingID("MacAddr2");
//		thing.setKiiAppID("b");
//
//		thingDao.addThingInfo(thing);
//
//		thing.setId("003");
//		thing.setVendorThingID("MacAddr3");
//		thing.setKiiAppID("c");

//		thingDao.addThingInfo(thing);


		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System);
		tag.setDisplayName("demo1");
		tagIndexDao.addTagIndex(tag);

		tag.setTagType(TagType.System);
		tag.setDisplayName("demo2");
		tagIndexDao.addTagIndex(tag);


	}
	
	@Test
	public void addTag() throws Exception{
		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System);
		tag.setDisplayName("demo1");
		
		thingManager.bindTagToThing(tag.getId(),"001");
		
		tag = tagIndexDao.getTagIndexByID(tag.getId());
		//System.out.println(tag.getGlobalThings());
		//System.out.println(tag.getKiiAppIDs());
		
		assertEquals(1,tag.getGlobalThings().size());
		assertEquals(1,tag.getKiiAppIDs().size());
	}

	@Test
	public void addTags() throws Exception{
		TagIndex tag1=new TagIndex();
		tag1.setTagType(TagType.System);
		tag1.setDisplayName("demo1");
		
		TagIndex tag2=new TagIndex();
		tag2.setTagType(TagType.System);
		tag2.setDisplayName("demo2");
		
		thingManager.bindTagToThing(CollectUtils.createList(tag1.getId(),tag2.getId()),
				CollectUtils.createList("001","002"));
		
		TagIndex tag = tagIndexDao.getTagIndexByID(tag1.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getKiiAppIDs().size());
		
		tag = tagIndexDao.getTagIndexByID(tag2.getId());
		assertEquals(2,tag.getGlobalThings().size());
		assertEquals(2,tag.getKiiAppIDs().size());
		
		//String json=mapper.writeValueAsString(tag);
		//System.out.println(json);
	}
	
	@Test
	public void removeTag() throws Exception{
		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System);
		tag.setDisplayName("demo1");
		
		thingManager.bindTagToThing(tag.getId(),"001");
		
		//tag = tagIndexDao.getObjectByID(tag.getId());
		//System.out.println(tag.getGlobalThings());
		//System.out.println(tag.getKiiAppIDs());
		
		thingManager.unbindTagToThing(tag.getId(), "001");
		tag = tagIndexDao.getTagIndexByID(tag.getId());
		//System.out.println(tag.getGlobalThings());
		//System.out.println(tag.getKiiAppIDs());
		
		assertEquals(0,tag.getGlobalThings().size());
		assertEquals(0,tag.getKiiAppIDs().size());
	}

	@Test
	public void addThing() throws Exception{
		String vendorThingID = "VendorThingID"+System.currentTimeMillis();
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID(vendorThingID);
		thingInfo.setKiiAppID("AppID1");
		thingInfo.setGlobalThingID(vendorThingID+"-AppID1");
		
		List<String> tagList = new ArrayList<>();
//		TagIndex tag1=new TagIndex();
//		tag1.setTagType(TagType.System.toString());
//		tag1.setDisplayName("demo1");
//		tagList.add(tag1);
		
//		TagIndex tag2=new TagIndex();
//		tag2.setTagType(TagType.Location.toString());
//		tag2.setDisplayName("1F");
//		tagList.add(tag2);

		tagList.add("demo1");
		tagList.add("1F");




		thingManager.createThing(thingInfo, tagList);
		
		GlobalThingInfo info = thingManager.findThingByVendorThingID(vendorThingID);
		assertNotNull(info);
		assertEquals(2,info.getTags().size());
	}
	
	@Test
	public void deleteThing() throws Exception{

		thingDao.removeGlobalThingByID("001");
		GlobalThingInfo info = thingDao.getThingInfoByID("001");
		assertNull(info);
	}
	
	@Test
	public void deleteTag() throws Exception{
		TagIndex tag=new TagIndex();
		tag.setTagType(TagType.System);
		tag.setDisplayName("demo1");
		tagIndexDao.removeTagByID(tag.getId());
		TagIndex tagIndex = tagIndexDao.getTagIndexByID(tag.getId());
		assertNull(tagIndex);
	}

	@After
	public void cleanData(){

//		thingDao.removeEntity("001");
//		tagDao.removeEntity("sys-demo");
	}

}
