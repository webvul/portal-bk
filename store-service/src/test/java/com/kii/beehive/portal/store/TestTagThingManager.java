package com.kii.beehive.portal.store;


import static junit.framework.TestCase.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.business.manager.TagThingManager;

public class TestTagThingManager extends TestInit {

    @Autowired
    private TagThingManager thingTagService;

    @Autowired
    private TagIndexDao tagIndexDao;

    @Test
    public void testFindLocations() {

        //
        String location1 = "floor1-room1-counter1";
        TagIndex tag1 = new TagIndex();
        tag1.setTagType(TagType.Location);
        tag1.setDisplayName(location1);

        tagIndexDao.saveOrUpdate(tag1);

        String location2 = "floor1-room1-counter2";
        TagIndex tag2 = new TagIndex();
        tag2.setTagType(TagType.Location);
        tag2.setDisplayName(location2);

        tagIndexDao.saveOrUpdate(tag2);

        String location3 = "floor1-room2-counter1";
        TagIndex tag3 = new TagIndex();
        tag3.setTagType(TagType.Location);
        tag3.setDisplayName(location3);

        tagIndexDao.saveOrUpdate(tag3);

        String location4 = "floor1-room2-counter2";
        TagIndex tag4 = new TagIndex();
        tag4.setTagType(TagType.Location);
        tag4.setDisplayName(location4);

        tagIndexDao.saveOrUpdate(tag4);

        String location5 = "floor2-room1-counter1";
        TagIndex tag5 = new TagIndex();
        tag5.setTagType(TagType.Location);
        tag5.setDisplayName(location5);

        tagIndexDao.saveOrUpdate(tag5);

        //
        List<String> locations = thingTagService.findLocations("");
        assertEquals(5, locations.size());

        locations = thingTagService.findLocations("floor1");
        assertEquals(4, locations.size());
        assertEquals(location1, locations.get(0));
        assertEquals(location2, locations.get(1));
        assertEquals(location3, locations.get(2));
        assertEquals(location4, locations.get(3));

        locations = thingTagService.findLocations("floor1-room2");
        assertEquals(2, locations.size());
        assertEquals(location3, locations.get(0));
        assertEquals(location4, locations.get(1));

        locations = thingTagService.findLocations("floor1-room2-counter1");
        assertEquals(1, locations.size());
        assertEquals(location3, locations.get(0));

    }

}
