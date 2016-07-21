package com.kii.beehive.portal.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingUserRelation;

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
		relation.setBeehiveUserID(101l);
		thingUserRelationDao.saveOrUpdate(relation);

		thingInfo.setVendorThingID("demo_vendor_thing_id 2");
		thingInfo.setType("thingType B");
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.user1ThingIds.add(thingId);
		this.user2ThingIds.add(thingId);
		relation.setThingId(thingId);
		thingUserRelationDao.saveOrUpdate(relation);

		relation.setBeehiveUserID(102l);
		thingUserRelationDao.saveOrUpdate(relation);

		thingInfo.setVendorThingID("demo_vendor_thing_id 3");
		thingInfo.setType("thingType C");
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.user2ThingIds.add(thingId);
		relation.setThingId(thingId);
		thingUserRelationDao.saveOrUpdate(relation);
	}

//	@Test
//	public void testFindThingIds() throws Exception {
//		Optional<List<Long>> thingIds = thingUserRelationDao.findThingIds(101l);
//		assertNotNull("Thing ids should not be null", thingIds);
//		assertEquals("There should be two thing ids", 2, thingIds.get().size());
//		assertTrue("The thing ids are incorrect.", new HashSet<Long>(thingIds.get()).containsAll(this.user1ThingIds));
//
//		thingIds = thingUserRelationDao.findThingIds(102l);
//		assertNotNull("Thing ids should not be null", thingIds);
//		assertEquals("There should be two thing ids", 2, thingIds.get().size());
//		assertTrue("The thing ids are incorrect.", new HashSet<Long>(thingIds.get()).containsAll(this.user2ThingIds));
//	}

//	@Test
//	public void testFindUserIds() throws Exception {
//		List<String> userIds = thingUserRelationDao.findUserIds(this.allThingIds.get(0));
//		assertNotNull("User ids should not be null", userIds);
//		assertEquals("There should be one user id", 1, userIds.size());
//		assertTrue("user id should be user1", 101l.equals(userIds.get(0)));
//
//		userIds = thingUserRelationDao.findUserIds(this.allThingIds.get(1));
//		assertNotNull("User ids should not be null", userIds);
//		assertEquals("There should be one user id", 2, userIds.size());
//		assertTrue("user id should be user1 and user2", userIds.contains(101l) && userIds.contains(102l));
//
//		userIds = thingUserRelationDao.findUserIds(this.allThingIds.get(2));
//		assertNotNull("User ids should not be null", userIds);
//		assertEquals("There should be one user id", 1, userIds.size());
//		assertTrue("user id should be user1", 102l.equals(userIds.get(0)));
//	}

	@Test
	public void testFindByThingId() throws Exception {
		List<ThingUserRelation> relations = thingUserRelationDao.findByThingId(this.allThingIds.get(0));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User id should be user1", 101l==(relations.get(0).getBeehiveUserID()));

		relations = thingUserRelationDao.findByThingId(this.allThingIds.get(1));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 2, relations.size());
		Set<Long> userIds = relations.stream().map(ThingUserRelation::getBeehiveUserID).collect(Collectors.toSet());
		assertTrue("User id should be user1 and user2", userIds.contains(101l) && userIds.contains(102l));

		relations = thingUserRelationDao.findByThingId(this.allThingIds.get(2));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User id should be user1", 102l==(relations.get(0).getBeehiveUserID()));
	}

	@Test
	public void testFindByUserId() throws Exception {
		List<ThingUserRelation> relations = thingUserRelationDao.findByUserId(101l);
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be two relations.", 2, relations.size());
		for (ThingUserRelation relation : relations) {
			assertTrue("The thing id is not associated with user", this.user1ThingIds.contains(relation.getThingId()));
		}

		relations = thingUserRelationDao.findByUserId(102l);
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be two relations.", 2, relations.size());
		for (ThingUserRelation relation : relations) {
			assertTrue("The thing id is not associated with user", this.user2ThingIds.contains(relation.getThingId()));
		}
	}

	@Test
	public void testFindByThingIdAndUserId() throws Exception {
		for (Long thingId : this.user1ThingIds) {
			assertNotNull("Cannot find the relation", thingUserRelationDao.find(thingId, 101l));
		}

		for (Long thingId : this.user2ThingIds) {
			assertNotNull("Cannot find the relation", thingUserRelationDao.find(thingId, 102l));
		}

		assertNull("Should not find the relation", thingUserRelationDao.find(this.allThingIds.get(2), 101l));
		assertNull("Should not find the relation", thingUserRelationDao.find(this.allThingIds.get(0), 102l));
	}

	@Test
	public void testDeleteByThingId() throws Exception {
		assertNotNull(thingUserRelationDao.find(this.allThingIds.get(0), 101l));
		thingUserRelationDao.deleteByThingId(this.allThingIds.get(0));
		assertNull("Relation should be removed", thingUserRelationDao.find(this.allThingIds.get(0), 101l));
	}
}
