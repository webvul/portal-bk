package com.kii.beehive.portal.jdbc;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by hdchen on 3/18/16.
 */
public class TestThingUserRelationDao extends TestTemplate {

	@Autowired
	private GlobalThingSpringDao globalThingSpringDao;

	@Autowired
	private ThingUserRelationDao thingUserRelationDao;

	private List<Long> allThingIds = new ArrayList<Long>();

	private List<Long> user1ThingIds = new ArrayList<Long>();

	private List<Long> user2ThingIds = new ArrayList<Long>();


	@Before
	public void setUp() throws Exception {
		Long thingId;
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		ThingUserRelation relation = new ThingUserRelation();

		thingInfo.setVendorThingID("demo_vendor_thing_id 1");
		thingInfo.setKiiAppID("appID");
		thingInfo.setCustom("custom");
		thingInfo.setType("thingType A");
		thingInfo.setStatus("this is a test about long text,we don't know the final required,so....");
		thingInfo.setFullKiiThingID(ThingIDTools.joinFullKiiThingID("abcdefghijk", "appID"));
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.user1ThingIds.add(thingId);

		relation.setThingId(thingId);
		relation.setUserId("user1");
		thingUserRelationDao.saveOrUpdate(relation);

		thingInfo.setVendorThingID("demo_vendor_thing_id 2");
		thingInfo.setType("thingType B");
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.user1ThingIds.add(thingId);
		this.user2ThingIds.add(thingId);
		relation.setThingId(thingId);
		thingUserRelationDao.saveOrUpdate(relation);

		relation.setUserId("user2");
		thingUserRelationDao.saveOrUpdate(relation);

		thingInfo.setVendorThingID("demo_vendor_thing_id 3");
		thingInfo.setType("thingType C");
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.user2ThingIds.add(thingId);
		relation.setThingId(thingId);
		thingUserRelationDao.saveOrUpdate(relation);
	}

	@Test
	public void testFindThingIds() throws Exception {
		Optional<List<Long>> thingIds = thingUserRelationDao.findThingIds("user1");
		assertNotNull("Thing ids should not be null", thingIds);
		assertEquals("There should be two thing ids", 2, thingIds.get().size());
		assertTrue("The thing ids are incorrect.", new HashSet<Long>(thingIds.get()).containsAll(this.user1ThingIds));

		thingIds = thingUserRelationDao.findThingIds("user2");
		assertNotNull("Thing ids should not be null", thingIds);
		assertEquals("There should be two thing ids", 2, thingIds.get().size());
		assertTrue("The thing ids are incorrect.", new HashSet<Long>(thingIds.get()).containsAll(this.user2ThingIds));
	}

	@Test
	public void testFindUserIds() throws Exception {
		List<String> userIds = thingUserRelationDao.findUserIds(this.allThingIds.get(0));
		assertNotNull("User ids should not be null", userIds);
		assertEquals("There should be one user id", 1, userIds.size());
		assertTrue("user id should be user1", "user1".equals(userIds.get(0)));

		userIds = thingUserRelationDao.findUserIds(this.allThingIds.get(1));
		assertNotNull("User ids should not be null", userIds);
		assertEquals("There should be one user id", 2, userIds.size());
		assertTrue("user id should be user1 and user2", userIds.contains("user1") && userIds.contains("user2"));

		userIds = thingUserRelationDao.findUserIds(this.allThingIds.get(2));
		assertNotNull("User ids should not be null", userIds);
		assertEquals("There should be one user id", 1, userIds.size());
		assertTrue("user id should be user1", "user2".equals(userIds.get(0)));
	}

	@Test
	public void testFindByThingId() throws Exception {
		List<ThingUserRelation> relations = thingUserRelationDao.findByThingId(this.allThingIds.get(0));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User id should be user1", "user1".equals(relations.get(0).getUserId()));

		relations = thingUserRelationDao.findByThingId(this.allThingIds.get(1));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 2, relations.size());
		Set<String> userIds = relations.stream().map(ThingUserRelation::getUserId).collect(Collectors.toSet());
		assertTrue("User id should be user1 and user2", userIds.contains("user1") && userIds.contains("user2"));

		relations = thingUserRelationDao.findByThingId(this.allThingIds.get(2));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User id should be user1", "user2".equals(relations.get(0).getUserId()));
	}

	@Test
	public void testFindByUserId() throws Exception {
		List<ThingUserRelation> relations = thingUserRelationDao.findByUserId("user1");
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be two relations.", 2, relations.size());
		for (ThingUserRelation relation : relations) {
			assertTrue("The thing id is not associated with user", this.user1ThingIds.contains(relation.getThingId()));
		}

		relations = thingUserRelationDao.findByUserId("user2");
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be two relations.", 2, relations.size());
		for (ThingUserRelation relation : relations) {
			assertTrue("The thing id is not associated with user", this.user2ThingIds.contains(relation.getThingId()));
		}
	}

	@Test
	public void testFindByThingIdAndUserId() throws Exception {
		for (Long thingId : this.user1ThingIds) {
			assertNotNull("Cannot find the relation", thingUserRelationDao.find(thingId, "user1"));
		}

		for (Long thingId : this.user2ThingIds) {
			assertNotNull("Cannot find the relation", thingUserRelationDao.find(thingId, "user2"));
		}

		assertNull("Should not find the relation", thingUserRelationDao.find(this.allThingIds.get(2), "user1"));
		assertNull("Should not find the relation", thingUserRelationDao.find(this.allThingIds.get(0), "user2"));
	}

	@Test
	public void testDeleteByThingId() throws Exception {
		assertNotNull(thingUserRelationDao.find(this.allThingIds.get(0), "user1"));
		thingUserRelationDao.deleteByThingId(this.allThingIds.get(0));
		assertNull("Relation should be removed", thingUserRelationDao.find(this.allThingIds.get(0), "user1"));
	}
}
