package com.kii.beehive.portal.jdbc;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.dao.TeamDao;
import com.kii.beehive.portal.jdbc.dao.TeamTagRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.jdbc.entity.Team;
import com.kii.beehive.portal.jdbc.entity.TeamTagRelation;

public class TestTagIndexDao extends TestTemplate{

	@Autowired
	private TagIndexDao dao;
	
	@Autowired
	private GlobalThingSpringDao globalThingDao;
	
	@Autowired
	private TagThingRelationDao tagThingRelationDao;
	
	@Autowired
	private TeamDao teamDao;
	
	@Autowired
	private TeamTagRelationDao teamTagRelationDao;
	
	private TagIndex  tag =new TagIndex();
	
	@Before
	public void init(){
		tag.setDisplayName("DisplayNameTest");
		tag.setTagType(TagType.Custom);
		tag.setDescription("DescriptionTest");
		tag.setFullTagName(TagType.Custom.getTagName("DisplayNameTest"));
		long id=dao.saveOrUpdate(tag);
		tag.setId(id);
		AuthInfoStore.setTeamID(null);
	}
	
	@Test
	public void testFindByID(){

		TagIndex  entity=dao.findByID(tag.getId());
		assertEquals(tag.getDisplayName(),entity.getDisplayName());
		assertEquals(tag.getTagType(),entity.getTagType());
		assertEquals(tag.getDescription(),entity.getDescription());

	}
	
	@Test
	public void testUpdate(){
		tag.setDisplayName("DisplayNameUpdate");
		dao.updateEntityAllByID(tag);
		TagIndex  entity=dao.findByID(tag.getId());
		assertEquals("DisplayNameUpdate",entity.getDisplayName());
		assertEquals(tag.getTagType(),entity.getTagType());
		assertEquals(tag.getDescription(),entity.getDescription());

	}
	
	@Test
	public void testFindByIDs(){
		TagIndex  tag2 =new TagIndex();
		tag2.setDisplayName("DisplayName2");
		tag2.setTagType(TagType.Location);
		tag2.setDescription("Description2");
		tag2.setFullTagName(TagType.Location.getTagName("DisplayName2"));
		long id2=dao.saveOrUpdate(tag2);
		List<Long> ids = new ArrayList<>();
		ids.add(tag.getId());
		ids.add(id2);
		List<TagIndex>  list=dao.findByIDs(ids);

		assertEquals(2,list.size());
	}
	
	@Test
	public void testDelete(){
		dao.deleteByID(tag.getId());
		TagIndex  entity=dao.findByID(tag.getId());
		assertNull(entity);
	}
	
	@Test
	public void testIsIdExist(){
		boolean b = dao.IsIdExist(tag.getId());
		assertTrue(b);
	}
	
	@Test
	public void testFindTagByTagTypeAndName(){
		List<TagIndex> list = dao.findTagByTagTypeAndName(null,tag.getDisplayName());
		assertEquals(1,list.size());
		list = dao.findTagByTagTypeAndName(TagType.Custom.toString(),tag.getDisplayName());
		assertEquals(1,list.size());
	}
	
	@Test
	public void testFindTagByTagTypeAndNameWithTeamID(){
		Long teamID = createTeamRel();
		
		AuthInfoStore.setTeamID(teamID);
		
		List<TagIndex> list = dao.findTagByTagTypeAndName(null,tag.getDisplayName());
		assertEquals(1,list.size());
		list = dao.findTagByTagTypeAndName(TagType.Custom.toString(),tag.getDisplayName());
		assertEquals(1,list.size());
	}
	
	@Test
	public void testFindLocations(){
		TagIndex t = new TagIndex();
		t.setDisplayName("LocationTest");
		t.setTagType(TagType.Location);
		t.setDescription("DescriptionTest");
		t.setFullTagName(TagType.Location.getTagName("LocationTest"));
		dao.saveOrUpdate(t);
		
		List<String> list = dao.findLocations(t.getDisplayName());
		assertEquals(1,list.size());
	}
	
	@Test
	public void testFindLocationsWithTeamID(){
		Long teamID = createTeamRel();
		AuthInfoStore.setTeamID(teamID);
		
		TagIndex t = new TagIndex();
		t.setDisplayName("LocationTest");
		t.setTagType(TagType.Location);
		t.setDescription("DescriptionTest");
		t.setFullTagName(TagType.Location.getTagName("LocationTest"));
		Long tID =dao.saveOrUpdate(t);
		
		teamTagRelationDao.saveOrUpdate(new TeamTagRelation(teamID,tID));
		
		List<String> list = dao.findLocations(t.getDisplayName());
		assertEquals(1,list.size());
	}
	
	@Test
	public void testFindTagByGlobalThingID(){
		GlobalThingInfo  thing=new GlobalThingInfo();
		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("1");
		long thingID=globalThingDao.saveOrUpdate(thing);
		
		TagThingRelation rel =new TagThingRelation();
		rel.setTagID(tag.getId());
		rel.setThingID(thingID);
		tagThingRelationDao.insert(rel);
		
		List<TagIndex> list = dao.findTagByGlobalThingID(thingID);
		assertEquals(1,list.size());
		assertEquals(tag.getDisplayName(),list.get(0).getDisplayName());
		assertEquals(tag.getTagType(),list.get(0).getTagType());
		assertEquals(tag.getDescription(),list.get(0).getDescription());
	}
	
	private Long createTeamRel(){
		Team t = new Team();
		t.setName("TeamTest");
		Long teamID = teamDao.saveOrUpdate(t);
		
		teamTagRelationDao.saveOrUpdate(new TeamTagRelation(teamID,tag.getId()));
		return teamID;
	}
	
}
