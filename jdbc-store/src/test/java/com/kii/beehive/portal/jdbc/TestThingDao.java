package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamThingRelation;

public class TestThingDao extends TestTemplate{

	@Autowired
	private GlobalThingSpringDao dao;
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private TagThingRelationDao tagThingRelationDao;
	
	@Autowired
	private TeamDao teamDao;
	
	@Autowired
	private TeamThingRelationDao teamThingRelationDao;
	
	private GlobalThingInfo  thing=new GlobalThingInfo();
	
	@Before
	public void init(){
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("thingType");
		thing.setStatus("this is a test about long text,we don't know the final required,so....");

		String fullKiiThingID= ThingIDTools.joinFullKiiThingID("abcdefghijk","appID");
		thing.setFullKiiThingID(fullKiiThingID);
		long id=dao.saveOrUpdate(thing);
		thing.setId(id);
		
		AuthInfoStore.setTeamID(null);
	}
	

	@Test
	public void testFindByID(){

		GlobalThingInfo  entity=dao.findByID(thing.getId());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());

	}
	
	@Test
	public void testFindByIDs(){
		GlobalThingInfo thing2 = new GlobalThingInfo();
		thing2.setVendorThingID("demo_vendor_thing_id2");
		thing2.setKiiAppID("appID2");
		thing2.setStatus("1");

		long id2=dao.saveOrUpdate(thing2);
		List<Long> ids = new ArrayList<>();
		ids.add(thing.getId());
		ids.add(id2);
		List<GlobalThingInfo>  list=dao.findByIDs(ids);

		assertEquals(2,list.size());
	}
	
	@Test
	public void testDelete(){
		dao.deleteByID(thing.getId());
		GlobalThingInfo  entity=dao.findByID(thing.getId());
		assertNull(entity);
	}

	
	@Test
	public void testGetThingByVendorThingID(){
		GlobalThingInfo  entity = dao.getThingByVendorThingID(thing.getVendorThingID());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}

	@Test
	public void testUpdate(){
		thing.setVendorThingID("demo_vendor_thing_id_update");
		thing.setCustom("customUpdate");
		thing.setType("typeUpdate");
		thing.setStatus("Update");
		dao.updateEntityAllByID(thing);
		GlobalThingInfo  entity=dao.findByID(thing.getId());
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	@Test
	public void testFindAllThingTypes() {
		List<String> list = dao.findAllThingTypes();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testFindAllThingTypesWithTeamID() {
		Long teamID = createTeamRel();
		
		AuthInfoStore.setTeamID(teamID);
		List<String> list = dao.findAllThingTypes();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testFindAllThingTypesWithThingCount() {
		List<Map<String, Object>> list = dao.findAllThingTypesWithThingCount();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testFindAllThingTypesWithThingCountWithTeamID() {
		Long teamID = createTeamRel();
		
		AuthInfoStore.setTeamID(teamID);
		
		List<Map<String, Object>> list = dao.findAllThingTypesWithThingCount();
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testGetThingByType() {
		List<GlobalThingInfo> list = dao.getThingByType(thing.getType());
		GlobalThingInfo  entity=list.get(0);
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	@Test
	public void testGetThingByTypeWithTeamID() {
		List<GlobalThingInfo> list = dao.getThingByType(thing.getType());
		GlobalThingInfo  entity=list.get(0);
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	@Test
	public void testFindThingByTag() {
		TagIndex tag = createTagRel();
		
		List<GlobalThingInfo> list = dao.findThingByTag(tag.getFullTagName());
		assertTrue(list.size() > 0);
		GlobalThingInfo  entity=list.get(0);
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	@Test
	public void testFindThingByTagWithTeamID() {
		TagIndex tag = createTagRel();
		
		Long teamID = createTeamRel();
		
		AuthInfoStore.setTeamID(teamID);
		List<GlobalThingInfo> list = dao.findThingByTag(tag.getFullTagName());
		assertTrue(list.size() > 0);
		GlobalThingInfo  entity=list.get(0);
		assertEquals(thing.getVendorThingID(),entity.getVendorThingID());
		assertEquals(thing.getKiiAppID(),entity.getKiiAppID());
		assertEquals(thing.getCustom(),entity.getCustom());
		assertEquals(thing.getType(),entity.getType());
		assertEquals(thing.getStatus(),entity.getStatus());
	}
	
	@Test
	public void testfindThingTypeBytagIDs() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();
		
		List<Map<String, Object>> list = dao.findThingTypeBytagIDs(tag.getId()+","+tag2.getId());
		
		assertTrue(list.size() > 0);
	}
	
	@Test
	public void testfindThingTypeBytagIDsWithTeamID() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();
		Long teamID = createTeamRel();
		
		AuthInfoStore.setTeamID(teamID);
		List<Map<String, Object>> list = dao.findThingTypeBytagIDs(tag.getId()+","+tag2.getId());
		
		assertTrue(list.size() > 0);
	}

	@Test
	public void testfindThingTypeByFullTagNames() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();

		List<String> tagCollect = new ArrayList<>();
		tagCollect.add(tag.getFullTagName());
		tagCollect.add(tag2.getFullTagName());

		List<String> list = dao.findThingTypeByFullTagNames(tagCollect);

		assertTrue(list.size() == 1);
		assertTrue("thingType".equals(list.get(0)));
	}

	@Test
	public void testfindThingTypeByFullTagNamesWithTeamID() {
		TagIndex tag = createTagRel();
		TagIndex tag2 = createTagRel();
		Long teamID = createTeamRel();

		List<String> tagCollect = new ArrayList<>();
		tagCollect.add(tag.getFullTagName());
		tagCollect.add(tag2.getFullTagName());

		AuthInfoStore.setTeamID(teamID);
		List<String> list = dao.findThingTypeByFullTagNames(tagCollect);

		assertTrue(list.size() == 1);
		assertTrue("thingType".equals(list.get(0)));
	}
	
	private TagIndex createTagRel(){
		TagIndex  tag =new TagIndex();
		tag.setDisplayName("DisplayNameTest");
		tag.setTagType(TagType.Custom);
		tag.setDescription("DescriptionTest");
		tag.setFullTagName(TagType.Custom.getTagName("DisplayNameTest"));
		long id=tagIndexDao.saveOrUpdate(tag);
		tag.setId(id);
		
		tagThingRelationDao.saveOrUpdate(new TagThingRelation(tag.getId(), thing.getId()));
		return tag;
	}
	
	private Long createTeamRel(){
		Team t = new Team();
		t.setName("TeamTest");
		Long teamID = teamDao.saveOrUpdate(t);
		
		teamThingRelationDao.saveOrUpdate(new TeamThingRelation(teamID,thing.getId()));
		return teamID;
	}
}
