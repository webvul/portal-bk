package com.kii.beehive.portal.jdbc;

import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class TestTagThingRelationDao extends TestTemplate {

	@Autowired
	private TagThingRelationDao dao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	private TagThingRelation rel = new TagThingRelation();
	private TagIndex tag = new TagIndex();
	private GlobalThingInfo thing = new GlobalThingInfo();

	@Before
	public void init() {

		tag.setDisplayName("DisplayName");
		tag.setTagType(TagType.Custom);
		tag.setDescription("Description");
		tag.setFullTagName(TagType.Custom.getTagName("DisplayName"));
		long tagID = tagIndexDao.saveOrUpdate(tag);
		tag.setId(tagID);

		thing.setVendorThingID("demo_vendor_thing_id");
		thing.setKiiAppID("appID");
		thing.setCustom("custom");
		thing.setType("type");
		thing.setStatus("1");
		long thingID = globalThingDao.saveOrUpdate(thing);
		thing.setId(thingID);

		rel.setTagID(tagID);
		rel.setThingID(thingID);
		long id = dao.insert(rel);
		rel.setId(id);
	}

	@Test
	public void testFindByID() {
		TagThingRelation entity = dao.findByID(rel.getId());
		assertEquals(rel.getTagID(), entity.getTagID());
		assertEquals(rel.getThingID(), entity.getThingID());
	}

	@Test
	public void testFindByThingIDAndTagID() {
		TagThingRelation entity = dao.findByThingIDAndTagID(thing.getId(), tag.getId());
		assertEquals(rel.getTagID(), entity.getTagID());
		assertEquals(rel.getThingID(), entity.getThingID());

		entity = dao.findByThingIDAndTagID(thing.getId(), null);
		assertNull(entity);
		entity = dao.findByThingIDAndTagID(null, tag.getId());
		assertNull(entity);
		entity = dao.findByThingIDAndTagID(null, null);
		assertNull(entity);
	}

	@Test
	public void testDelete() {
		dao.delete(tag.getId(), thing.getId());
		TagThingRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByTagID() {
		dao.delete(tag.getId(), null);
		TagThingRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteByThingID() {
		dao.delete(null, thing.getId());
		TagThingRelation entity = dao.findByID(rel.getId());
		assertNull(entity);
	}

	@Test
	public void testDeleteNull() {
		dao.delete(null, null);
	}

	@Test
	public void testFindTagIds() throws Exception {
		List<Long> tagIds = dao.findTagIds(thing.getId()).orElse(Collections.emptyList());
		assertEquals(1, tagIds.size());
		assertEquals(tag.getId(), tagIds.get(0));
	}
}
