package com.kii.beehive.portal.jdbc;

import com.kii.beehive.portal.common.utils.ThingIDTools;
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
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

@Rollback
public class Test2ThingDao extends TestTemplate {

	@Autowired
	private GlobalThingSpringDao thingDao;


	@Autowired
	private TagIndexDao tagDao;

	@Autowired
	private TagThingRelationDao relationDao;


	private List<Long> thingIDs = new ArrayList<>();

	@Before
	public void init() {

		List<Long> tagIDs = new ArrayList<>();

		for (int i = 0; i < 5; i++) {

			TagIndex tag = new TagIndex();
			tag.setDisplayName("name" + i);
			tag.setTagType(TagType.Custom);
			tag.setFullTagName(TagType.Custom.getTagName("name" + i));
			long id = tagDao.saveOrUpdate(tag);

			tagIDs.add(id);
		}


		for (int i = 0; i < 10; i++) {


			GlobalThingInfo info = new GlobalThingInfo();
			String fullKiiThingID = ThingIDTools.joinFullKiiThingID("app" + i % 5, "kiiID" + i);

			info.setFullKiiThingID(fullKiiThingID);

			info.setVendorThingID("vendorID" + i);
			info.setType(String.valueOf(i));

			long id = thingDao.insert(info);

			TagThingRelation relation = new TagThingRelation();
			relation.setThingID(id);
			relation.setTagID(tagIDs.get(i % 5));

			relationDao.insert(relation);


			TagThingRelation relation2 = new TagThingRelation();
			relation2.setThingID(id);
			relation2.setTagID(tagIDs.get((1 + i) % 5));

			relationDao.insert(relation2);

			thingIDs.add(id);

		}
	}

	@Test
	public void testGetThingByTagUnion() {

		String[] tags = {"Custom-name1", "Custom-name3"};

		Set<GlobalThingInfo> thingList = thingDao.queryThingByUnionTags(Arrays.asList(tags));

		assertEquals(8, thingList.size());

		//long count=thingList.stream().map(thing->Integer.parseInt(thing.getType())).filter(i->(i>=1&&i<=4)||(i>=6&&i<=9)).count();
		//assertEquals(8,count);
	}

	@Test
	public void testGetThingByTagIne() {
		String[] tags = {"Custom-name1", "Custom-name3"};

		Set<GlobalThingInfo> thingList = thingDao.queryThingByIntersectionTags(Arrays.asList(tags));

		assertEquals(0, thingList.size());

		tags[1] = "Custom-name2";

		thingList = thingDao.queryThingByIntersectionTags(Arrays.asList(tags));

		assertEquals(2, thingList.size());

		long count = thingList.stream().map(thing -> Integer.parseInt(thing.getType())).filter(i -> (i == 1 || i == 6)
		).count();

		assertEquals(2, count);

	}

	@Test
	public void testGetThingByTagIneAndType() {
		String[] tags = {"Custom-name1", "Custom-name3"};

		Set<GlobalThingInfo> thingList = thingDao.queryThingByUnionTags(Arrays.asList(tags), "1");

		// only one thing in thing type "1"
		assertEquals(1, thingList.size());

		assertEquals("1", thingList.iterator().next().getType());

	}

}
