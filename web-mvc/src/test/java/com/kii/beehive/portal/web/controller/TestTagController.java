package com.kii.beehive.portal.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.PortalException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by USER on 12/1/15.
 */
public class TestTagController extends WebTestTemplate {

    private final static String KII_APP_ID = "0af7a7e7";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TagIndexDao tagIndexDao;
    @Autowired
    private GlobalThingSpringDao globalThingDao;
    @Autowired
    private TagThingRelationDao tagThingRelationDao;

    @Autowired
    private TagController tagController;

    @Autowired
    private TagGroupRelationDao tagGroupRelationDao;

    @Autowired
    private TagUserRelationDao tagUserRelationDao;

    @Autowired
    private UserGroupDao userGroupDao;

    private String displayName = "someDisplayName";

    private Long tagIDForTest;

    private String tokenForTest = BEARER_SUPER_TOKEN;

    @Before
    public void setUp() throws Exception {
        List<TagIndex> tags = tagIndexDao.findAll();
        for (TagIndex tag : tags) {
            tagIndexDao.deleteByID(tag.getId());
        }
    }

    @Test
    public void testCreateTag() throws Exception {
        String keyId = "id";
        String keyName = "tagName";

        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);

        AuthInfoStore.setAuthInfo("user1");

        Map<String, Object> result = tagController.createTag(tagIndex);
        assertNotNull("Result of createTag should not be null", result);
        Long id = (Long) result.get(keyId);
        String name = result.get(keyName).toString();
        assertEquals("Name doesn't match", TagType.Custom.getTagName(tagIndex.getDisplayName()), name);

        List<TagUserRelation> relations = tagUserRelationDao.findByUserId("user1");
        assertNotNull("Relations should exist.", relations);
        assertEquals("There should be only one relation.", 1, relations.size());
        assertEquals("Tag id doesn't match", id, relations.get(0).getTagId());
        assertEquals("User id doesn't match", "user1", relations.get(0).getUserId());

        TagIndex tag = tagIndexDao.findByID(id);
        assertEquals("Creator doesn't match.", "user1", tag.getCreateBy());
    }

    @Test
    public void testRemoveTag() throws Exception {
        try {
            tagController.removeTag("Random");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }

        String keyId = "id";

        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);

        AuthInfoStore.setAuthInfo("user1");

        Map<String, Object> result = tagController.createTag(tagIndex);
        TagIndex tagFromDB = tagIndexDao.findByID((Long) result.get(keyId));
        assertNotNull("Cannot find the created tag.", tagFromDB);

        AuthInfoStore.setAuthInfo("user2");

        try {
            tagController.removeTag(tagIndex.getDisplayName());
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }

        AuthInfoStore.setAuthInfo("user1");
        try {
            tagController.removeTag(tagIndex.getDisplayName());
        } catch (Exception e) {
            fail("Should not throw any exception");
        }

        tagFromDB = tagIndexDao.findByID((Long) result.get(keyId));
        assertNull("Should not find the created tag.", tagFromDB);

        TagUserRelation relation = tagUserRelationDao.find((Long) result.get(keyId), "user1");
        assertNull("Should not have the relation", relation);
    }

    @Test
    public void testBindUserGroupToTag() throws Exception {
        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);
        tagIndex.setCreateBy("TagCreator");
        Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

        UserGroup userGroup = new UserGroup();
        userGroup.setName("User Group");
        userGroup.setCreateBy("Someone");
        Long userGroupId = userGroupDao.saveOrUpdate(userGroup);

        AuthInfoStore.setAuthInfo("Someone");

        // Error test
        String[] blankTagIds = new String[]{null, " "};
        String[] blankUserGroupIds = new String[]{null, " "};
        for (String tagIds : blankTagIds) {
            for (String userGroupIds : blankUserGroupIds) {
                try {
                    tagController.addTagToUserGroup(tagIds, userGroupIds);
                    fail("Expect a PortalException");
                } catch (PortalException e) {
                    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
                }
            }
        }

        // Existence test
        try {
            tagController.addTagToUserGroup(tagId + ",test2", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.addTagToUserGroup(tagId + "", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }

        AuthInfoStore.setAuthInfo(tagIndex.getCreateBy());
        try {
            tagController.addTagToUserGroup(tagId + "", userGroupId + ",userGroup1");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.addTagToUserGroup(tagId + "", userGroupId + "");
        } catch (Exception e) {
            fail("Should not throw any exception");
        }

        TagGroupRelation relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId);
        assertNotNull("Should have the relation", relation);
    }

    @Test
    public void testUnbindUserGroupFromTag() throws Exception {
        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);
        tagIndex.setCreateBy("TagCreator");
        Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

        UserGroup userGroup = new UserGroup();
        userGroup.setName("User Group");
        userGroup.setCreateBy("Someone");
        Long userGroupId = userGroupDao.saveOrUpdate(userGroup);

        AuthInfoStore.setAuthInfo(tagIndex.getCreateBy());
        tagController.addTagToUserGroup(tagId + "", userGroupId + "");

        TagGroupRelation relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId);
        assertNotNull("Should have the relation", relation);

        AuthInfoStore.setAuthInfo("Someone");

        // Error test
        String[] blankTagIds = new String[]{null, " "};
        String[] blankUserGroupIds = new String[]{null, " "};
        for (String tagIds : blankTagIds) {
            for (String userGroupIds : blankUserGroupIds) {
                try {
                    tagController.removeTagFromUserGroup(tagIds, userGroupIds);
                    fail("Expect a PortalException");
                } catch (PortalException e) {
                    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
                }
            }
        }

        // Existence test
        try {
            tagController.removeTagFromUserGroup(tagId + ",test2", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.removeTagFromUserGroup(tagId + "", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }

        AuthInfoStore.setAuthInfo(tagIndex.getCreateBy());
        try {
            tagController.removeTagFromUserGroup(tagId + "", userGroupId + ",userGroup1");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.removeTagFromUserGroup(tagId + "", userGroupId + "");
        } catch (Exception e) {
            fail("Should not throw any exception");
        }

        relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId);
        assertNull("Should not have the relation", relation);
    }

    @Test
    public void testWebCreateTag() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("displayName", displayName);
        request.put("description", "some description");

        String ctx = mapper.writeValueAsString(request);

        String result = this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> map = mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        tagIDForTest = Long.valueOf((int) map.get("id"));

        // assert http return
        String tagName = (String) map.get("tagName");
        assertEquals(TagType.Custom + "-" + displayName, tagName);

    }

    @Test
    public void testWebUpdateTag() throws Exception {

        this.testWebCreateTag();

        // update the tag
        Map<String, Object> request = new HashMap<>();
        request.put("displayName", displayName);
        request.put("description", "some description new");

        String ctx = mapper.writeValueAsString(request);

        String result = this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> map = mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        // assert http return
        Long tagID = Long.valueOf((int) map.get("id"));
        assertEquals(tagIDForTest, tagID);

        String tagName = (String) map.get("tagName");
        assertEquals(TagType.Custom + "-" + displayName, tagName);

        TagIndex tagIndex = tagIndexDao.findByID(tagID);
        assertEquals(tagIDForTest, (Long) tagIndex.getId());
        assertEquals(displayName, tagIndex.getDisplayName());
        assertEquals("some description new", tagIndex.getDescription());


        // create another tag as displayName changed
        request = new HashMap<>();
        request.put("displayName", displayName + "_new");
        request.put("description", "some description new");

        ctx = mapper.writeValueAsString(request);

        result = this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        map = mapper.readValue(result, Map.class);

        System.out.println("Response:" + result);

        // assert http return
        tagID = Long.valueOf((int) map.get("id"));
        assertEquals(Long.valueOf(tagIDForTest + 1), tagID);

        tagName = (String) map.get("tagName");
        assertEquals(TagType.Custom + "-" + displayName + "_new", tagName);

        tagIndex = tagIndexDao.findByID(tagID);
        assertEquals(Long.valueOf(tagIDForTest + 1), (Long) tagIndex.getId());
        assertEquals(displayName + "_new", tagIndex.getDisplayName());
        assertEquals("some description new", tagIndex.getDescription());

    }

    @Test
    public void testWebCreateTagException() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("description", "some description");

        String ctx = mapper.writeValueAsString(request);

        String result = this.mockMvc.perform(
                post("/tags/custom").content(ctx)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void testWebRemoveTag() throws Exception {

        this.testWebCreateTag();

        TagIndex tagIndex = tagIndexDao.findByID(tagIDForTest);
        assertNotNull(tagIndex);

        String result = this.mockMvc.perform(
                delete("/tags/custom/" + displayName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        tagIndex = tagIndexDao.findByID(tagIDForTest);
        assertNull(tagIndex);

    }

    @Test
    public void testWebRemoveTagException() throws Exception {

        String result = this.mockMvc.perform(
                delete("/tags/custom/" + "some_non_existing_displayName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void testWebGetAllTag() throws Exception {

        // test no tag
        String result = this.mockMvc.perform(
                get("/tags/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> list = mapper.readValue(result, List.class);
        assertEquals(0, list.size());

        // test tag existing

        List<String> displayNames = new ArrayList<>();
        displayNames.add("someDisplayName1");
        displayNames.add("someDisplayName2");
        displayNames.add("someDisplayName3");
        List<Long> tagIDs = new ArrayList<>();

        // create tag
        TagIndex tagIndex = new TagIndex();
        tagIndex.setTagType(com.kii.beehive.portal.jdbc.entity.TagType.Custom);
        tagIndex.setDisplayName(displayNames.get(0));
        long tagID1 = tagIndexDao.saveOrUpdate(tagIndex);
        tagIDs.add(tagID1);

        tagIndex = new TagIndex();
        tagIndex.setTagType(com.kii.beehive.portal.jdbc.entity.TagType.Custom);
        tagIndex.setDisplayName(displayNames.get(1));
        long tagID2 = tagIndexDao.saveOrUpdate(tagIndex);
        tagIDs.add(tagID2);

        tagIndex = new TagIndex();
        tagIndex.setTagType(com.kii.beehive.portal.jdbc.entity.TagType.Location);
        tagIndex.setDisplayName(displayNames.get(2));
        long tagID3 = tagIndexDao.saveOrUpdate(tagIndex);
        tagIDs.add(tagID3);

        result = this.mockMvc.perform(
                get("/tags/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        list = mapper.readValue(result, List.class);

        // assert http return
        assertEquals(3, list.size());

        for (Map<String, Object> map : list) {
            System.out.println("response: " + map);

            assertTrue(tagIDs.contains(Long.valueOf((int) map.get("id"))));
            assertTrue(map.get("tagType").equals(TagType.Custom.toString()) || map.get("tagType").equals(TagType.Location.toString()));
            assertTrue(displayNames.contains(map.get("displayName")));
        }

    }

    @Test
    public void testWebFindLocations() throws Exception {

        // create location
        List<String> displayNames = new ArrayList<>();
        displayNames.add("floor1-lobby");
        displayNames.add("floor1-room1-counter1");
        displayNames.add("floor1-room1-counter2");
        displayNames.add("floor2-room1");
        displayNames.add("floor2-room2");
        List<Long> tagIDs = new ArrayList<>();

        // create tag
        for (int i = 0; i < displayNames.size(); i++) {
            TagIndex tagIndex = new TagIndex();
            tagIndex.setTagType(TagType.Location);
            tagIndex.setDisplayName(displayNames.get(i));
            tagIndexDao.saveOrUpdate(tagIndex);
        }

        // find location floor1
        String result = this.mockMvc.perform(
                get("/tags/locations/" + "floor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<String> list = mapper.readValue(result, List.class);

        // assert http return
        assertEquals(3, list.size());
        assertEquals(displayNames.get(0), list.get(0));
        assertEquals(displayNames.get(1), list.get(1));
        assertEquals(displayNames.get(2), list.get(2));

        // find location floor1-room1
        result = this.mockMvc.perform(
                get("/tags/locations/" + "floor1-room1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        list = mapper.readValue(result, List.class);

        // assert http return
        assertEquals(2, list.size());
        assertEquals(displayNames.get(1), list.get(0));
        assertEquals(displayNames.get(2), list.get(1));

        // find all locations
        result = this.mockMvc.perform(
                get("/tags/locations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        list = mapper.readValue(result, List.class);

        // assert http return
        assertEquals(5, list.size());
        assertEquals(displayNames.get(0), list.get(0));
        assertEquals(displayNames.get(1), list.get(1));
        assertEquals(displayNames.get(2), list.get(2));
        assertEquals(displayNames.get(3), list.get(3));
        assertEquals(displayNames.get(4), list.get(4));

    }


    @Test
    public void testWebFindTags() throws Exception {

        // create thing
        String[] vendorThingIDs = new String[]{"vendorThingIDForTest1", "vendorThingIDForTest2", "vendorThingIDForTest3"};
        Long[] globalThingIDs = this.creatThingsForTest(vendorThingIDs, KII_APP_ID, "LED");

        // create tag
        String[] displayNames = new String[]{"displayNameForCustom1", "displayNameForCustom2", "displayNameForCustom3"};
        Long[] tagIDs = this.creatTagsForTest(TagType.Custom, displayNames);

        // create relation
        TagThingRelation relation = new TagThingRelation();
        relation.setTagID(tagIDs[0]);
        relation.setThingID(globalThingIDs[0]);
        tagThingRelationDao.insert(relation);

        relation = new TagThingRelation();
        relation.setTagID(tagIDs[0]);
        relation.setThingID(globalThingIDs[1]);
        tagThingRelationDao.insert(relation);

        relation = new TagThingRelation();
        relation.setTagID(tagIDs[1]);
        relation.setThingID(globalThingIDs[2]);
        tagThingRelationDao.insert(relation);


        // search custom tag
        String result = this.mockMvc.perform(
                get("/tags/search?" + "tagType=" + TagType.Custom)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header(Constants.ACCESS_TOKEN, tokenForTest)
        )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Map<String, Object>> list = mapper.readValue(result, List.class);

        assertEquals(3, list.size());

        for (Map<String, Object> map : list) {
            System.out.println("Response Map: " + map);
            long tagID = ((Integer) map.get("id")).longValue();
            int count = (Integer) map.get("count");
            List<Integer> intThings = (List<Integer>) map.get("things");
            List<Long> things = new ArrayList<>();
            if (intThings != null) {
                for (Integer i : intThings) {
                    things.add(i.longValue());
                }
            }

            if (tagID == tagIDs[0]) {
                assertEquals(2, count);
                System.out.println(things.contains(globalThingIDs[0]));
                assertTrue(things.contains(globalThingIDs[0]));
                assertTrue(things.contains(globalThingIDs[1]));
            } else if (tagID == tagIDs[1]) {
                assertEquals(1, count);
                assertTrue(things.contains(globalThingIDs[2]));
            } else if (tagID == tagIDs[2]) {
                assertEquals(0, count);
                assertTrue(things.isEmpty());
            }
        }

    }

    private Long[] creatThingsForTest(String[] vendorThingIDs, String kiiAppID, String type) {

        Long[] globalThingIDs = new Long[vendorThingIDs.length];

        for (int i = 0; i < vendorThingIDs.length; i++) {
            GlobalThingInfo thingInfo = new GlobalThingInfo();
            thingInfo.setVendorThingID(vendorThingIDs[i]);
            thingInfo.setKiiAppID(kiiAppID);
            thingInfo.setType(type);
            globalThingIDs[i] = globalThingDao.saveOrUpdate(thingInfo);

            System.out.println("create thing: " + globalThingIDs[i]);
        }

        return globalThingIDs;
    }

    private Long[] creatTagsForTest(TagType tagType, String[] displayNames) {

        Long[] tagIDs = new Long[displayNames.length];

        for (int i = 0; i < displayNames.length; i++) {
            TagIndex tagIndex = new TagIndex(tagType, displayNames[i], null);
            tagIDs[i] = tagIndexDao.saveOrUpdate(tagIndex);

            System.out.println("create tag: " + tagIDs[i]);
        }

        return tagIDs;
    }

}
