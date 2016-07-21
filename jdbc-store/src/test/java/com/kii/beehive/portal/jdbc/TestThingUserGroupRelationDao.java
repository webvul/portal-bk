package com.kii.beehive.portal.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.ThingUserGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.ThingUserGroupRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;

/**
 * Created by hdchen on 3/18/16.
 */
public class TestThingUserGroupRelationDao extends TestTemplate {
	@Autowired
	private GlobalThingSpringDao globalThingSpringDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private ThingUserGroupRelationDao thingUserGroupRelationDao;

	@Autowired
	private GroupUserRelationDao groupUserRelationDao;

	private List<Long> allThingIds = new ArrayList<Long>();

	private List<Long> group1ThingIds = new ArrayList<Long>();

	private List<Long> group2ThingIds = new ArrayList<Long>();

	private List<Long> userGroupIds = new ArrayList<Long>();

	@Before
	public void setUp() throws Exception {
		Long thingId;
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		ThingUserGroupRelation relation = new ThingUserGroupRelation();
		UserGroup userGroup = new UserGroup();
		userGroup.setName("User Group 1");
		userGroup.setDescription("User Group");
		this.userGroupIds.add(userGroupDao.saveOrUpdate(userGroup));
		this.userGroupIds.add(userGroupDao.saveOrUpdate(userGroup));

		thingInfo.setVendorThingID("demo_vendor_thing_id 1");
		thingInfo.setKiiAppID("appID");
		thingInfo.setCustom("custom");
		thingInfo.setType("thingType A");
		thingInfo.setStatus("this is a test about long text,we don't know the final required,so....");
		thingInfo.setFullKiiThingID(ThingIDTools.joinFullKiiThingID("abcdefghijk", "appID"));
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.group1ThingIds.add(thingId);

		relation.setThingId(thingId);
		relation.setUserGroupId(this.userGroupIds.get(0));
		thingUserGroupRelationDao.saveOrUpdate(relation);

		thingInfo.setVendorThingID("demo_vendor_thing_id 2");
		thingInfo.setType("thingType B");
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.group1ThingIds.add(thingId);
		this.group2ThingIds.add(thingId);
		relation.setThingId(thingId);
		relation.setUserGroupId(this.userGroupIds.get(0));
		thingUserGroupRelationDao.saveOrUpdate(relation);
		relation.setThingId(thingId);
		relation.setUserGroupId(this.userGroupIds.get(1));
		thingUserGroupRelationDao.saveOrUpdate(relation);

		thingInfo.setVendorThingID("demo_vendor_thing_id 3");
		thingInfo.setType("thingType C");
		thingId = globalThingSpringDao.saveOrUpdate(thingInfo);
		this.allThingIds.add(thingId);
		this.group2ThingIds.add(thingId);
		relation.setThingId(thingId);
		relation.setUserGroupId(this.userGroupIds.get(1));
		thingUserGroupRelationDao.saveOrUpdate(relation);
	}

	@Test
	public void testFindThingIds() throws Exception {
		Optional<List<Long>> thingIds = thingUserGroupRelationDao.findThingIds(this.userGroupIds.get(0));
		assertNotNull("Thing ids should not be null", thingIds);
		assertEquals("There should be two thing ids", 2, thingIds.get().size());
		assertTrue("The thing ids are incorrect.", new HashSet<Long>(thingIds.get()).containsAll(this.group1ThingIds));

		thingIds = thingUserGroupRelationDao.findThingIds(this.userGroupIds.get(1));
		assertNotNull("Thing ids should not be null", thingIds);
		assertEquals("There should be two thing ids", 2, thingIds.get().size());
		assertTrue("The thing ids are incorrect.", new HashSet<Long>(thingIds.get()).containsAll(this.group2ThingIds));
	}

	@Test
	public void testFindUserGroupIds() throws Exception {
		Optional<List<Long>> userGroupIds = thingUserGroupRelationDao.findUserGroupIds(this.allThingIds.get(0));
		assertNotNull("User group ids should not be null", userGroupIds);
		assertEquals("There should be only one user group id", 1, userGroupIds.get().size());
		assertTrue("The user group id is incorrect.", userGroupIds.get().get(0).equals(this.userGroupIds.get(0)));

		userGroupIds = thingUserGroupRelationDao.findUserGroupIds(this.allThingIds.get(1));
		assertNotNull("User group ids should not be null", userGroupIds);
		assertEquals("There should be two user group ids", 2, userGroupIds.get().size());
		assertTrue("The user group ids are incorrect.", new HashSet<Long>(this.userGroupIds).containsAll(userGroupIds
				.get()));

		userGroupIds = thingUserGroupRelationDao.findUserGroupIds(this.allThingIds.get(2));
		assertNotNull("User group ids should not be null", userGroupIds);
		assertEquals("There should be only one user group id", 1, userGroupIds.get().size());
		assertTrue("The user group id is incorrect.", userGroupIds.get().get(0).equals(this.userGroupIds.get(1)));
	}

	@Test
	public void testFindByThingId() throws Exception {
		List<ThingUserGroupRelation> relations = thingUserGroupRelationDao.findByThingId(this.allThingIds.get(0));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User group id is incorrect", this.userGroupIds.get(0).equals(relations.get(0).getUserGroupId()));

		relations = thingUserGroupRelationDao.findByThingId(this.allThingIds.get(1));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 2, relations.size());
		Set<Long> groupIds = relations.stream().map(ThingUserGroupRelation::getUserGroupId).collect(Collectors.toSet());
		assertTrue("User group ids are incorrect", groupIds.containsAll(this.userGroupIds));

		relations = thingUserGroupRelationDao.findByThingId(this.allThingIds.get(2));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be one relation", 1, relations.size());
		assertTrue("User group id is incorrect", this.userGroupIds.get(1).equals(relations.get(0).getUserGroupId()));
	}

	@Test
	public void testFindByUserGroupId() throws Exception {
		List<ThingUserGroupRelation> relations = thingUserGroupRelationDao.findByUserGroupId(this.userGroupIds.get(0));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be two relations.", 2, relations.size());
		for (ThingUserGroupRelation relation : relations) {
			assertTrue("The thing id is not associated with user group", this.group1ThingIds.contains(relation.getThingId
					()));
		}

		relations = thingUserGroupRelationDao.findByUserGroupId(this.userGroupIds.get(1));
		assertNotNull("Relation list should not be null", relations);
		assertEquals("There should be two relations.", 2, relations.size());
		for (ThingUserGroupRelation relation : relations) {
			assertTrue("The thing id is not associated with user group", this.group2ThingIds.contains(relation
					.getThingId()));
		}
	}

	@Test
	public void testFindByThingIdAndUserGroupId() throws Exception {
		for (Long thingId : this.group1ThingIds) {
			assertNotNull("Cannot find the relation", thingUserGroupRelationDao.find(thingId, this.userGroupIds.get
					(0)));
		}

		for (Long thingId : this.group2ThingIds) {
			assertNotNull("Cannot find the relation", thingUserGroupRelationDao.find(thingId, this.userGroupIds.get
					(1)));
		}

		assertNull("Should not find the relation", thingUserGroupRelationDao.find(this.allThingIds.get(2), this
				.userGroupIds.get(0)));
		assertNull("Should not find the relation", thingUserGroupRelationDao.find(this.allThingIds.get(0), this
				.userGroupIds.get(1)));
	}

	@Test
	public void testFindByThingIdAndUserId() throws Exception {
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setKiiAppID("KiiAppId");
		thingInfo.setVendorThingID("vendorThingId");
		Long thingId = globalThingSpringDao.saveOrUpdate(thingInfo);

		List<Long> userGroupIds = new ArrayList();
		for (int i = 0; i < 3; ++i) {
			UserGroup group = new UserGroup();
			group.setName("Group " + i);
			userGroupIds.add(userGroupDao.saveOrUpdate(group));

			ThingUserGroupRelation relation = new ThingUserGroupRelation();
			relation.setThingId(thingId);
			relation.setUserGroupId(userGroupIds.get(i));
			thingUserGroupRelationDao.saveOrUpdate(relation);
		}

		GroupUserRelation groupUserRelation = new GroupUserRelation();
		groupUserRelation.setUserGroupID(userGroupIds.get(1));
		groupUserRelation.setBeehiveUserID(101l);
		groupUserRelationDao.saveOrUpdate(groupUserRelation);

		List<ThingUserGroupRelation> result = thingUserGroupRelationDao.findByThingIdAndUserId(thingId, "Someone");
		assertNotNull("Should find the relations", result);
		assertEquals("Should have one relation", 1, result.size());
	}

	@Test
	public void testDeleteByThingId() throws Exception {
		assertNotNull(thingUserGroupRelationDao.find(this.allThingIds.get(0), this.userGroupIds.get(0)));
		thingUserGroupRelationDao.deleteByThingId(this.allThingIds.get(0));
		assertNull("Relation should be removed", thingUserGroupRelationDao.find(this.allThingIds.get(0), this
				.userGroupIds.get(0)));
	}
}
