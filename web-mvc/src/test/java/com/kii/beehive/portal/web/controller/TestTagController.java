package com.kii.beehive.portal.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.TagThingManager;
import com.kii.beehive.business.manager.UserManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.*;
import com.kii.beehive.portal.jdbc.entity.*;
import com.kii.beehive.portal.web.WebTestTemplate;
import com.kii.beehive.portal.web.constant.Constants;
import com.kii.beehive.portal.web.exception.PortalException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by USER on 12/1/15.
 */
public class TestTagController extends WebTestTemplate {

    private final static String KII_APP_ID = "0af7a7e7";
    @Autowired
    private ObjectMapper mapper;

    @Spy
    @Autowired
    private TagIndexDao tagIndexDao;

    @Spy
    @Autowired
    private TagThingManager tagThingManager;

    @Spy
    @Autowired
    private GlobalThingSpringDao globalThingDao;

    @Spy
    @Autowired
    private TagThingRelationDao tagThingRelationDao;

    @InjectMocks
    private TagController tagController;

    @Spy
    @Autowired
    private TagGroupRelationDao tagGroupRelationDao;

    @Spy
    @Autowired
    private TagUserRelationDao tagUserRelationDao;


    @Spy
    @Autowired
    private UserGroupDao userGroupDao;

    @Spy
    @Autowired
    private TeamTagRelationDao teamTagRelationDao;

    @Spy
    @Autowired
    private UserManager userManager;

    private String displayName = "someDisplayName";

    private Long tagIDForTest;

    private String tokenForTest = BEARER_SUPER_TOKEN;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        List<TagIndex> tags = tagIndexDao.findAll();
        for (TagIndex tag : tags) {
            tagIndexDao.deleteByID(tag.getId());
        }
        AuthInfoStore.setAuthInfo(null);
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
    public void testBindTagToUser() throws Exception {
        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);

        AuthInfoStore.setAuthInfo("TagCreator");
        Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

        doReturn(new HashSet<>(Arrays.asList("1"))).when(userManager).checkNonExistingUserID(any());

        // Error test
        String[] blankTagIds = new String[]{null, " "};
        String[] blankUserIds = new String[]{null, " "};
        for (String tagIds : blankTagIds) {
            for (String userId : blankUserIds) {
                try {
                    tagController.bindTagToUser(tagIds, userId);
                    fail("Expect a PortalException");
                } catch (PortalException e) {
                    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
                }
            }
        }

        // Existence test
        try {
            tagController.bindTagToUser(tagId + "", "123");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        doReturn(new HashSet<String>()).when(userManager).checkNonExistingUserID(any());
        AuthInfoStore.setAuthInfo("Someone");
        try {
            tagController.bindTagToUser(tagId + "", "123");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }

        AuthInfoStore.setAuthInfo("TagCreator");

        doAnswer(invocation -> {
            List<Long> tagIds = ((List<String>) invocation.getArguments()[0]).stream().map(Long::valueOf).
                    collect(Collectors.toList());
            List<String> userIds = (List<String>) invocation.getArguments()[1];
            tagIds.forEach(id -> {
                userIds.forEach(userId -> {
                    TagUserRelation relation = new TagUserRelation();
                    relation.setTagId(id);
                    relation.setUserId(userId);
                    tagUserRelationDao.saveOrUpdate(relation);
                });
            });
            return null;
        }).when(tagThingManager).bindTagToUser(anyList(), anyList());

        try {
            tagController.bindTagToUser(tagId + "", "Someone");
        } catch (Exception e) {
            fail("Should not throw any exception");
        }

        TagUserRelation relation = tagUserRelationDao.find(tagId, "Someone");
        assertNotNull("Should have the relation", relation);
    }

    @Test
    public void testUnbindTagFromUser() throws Exception {
        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);
        tagIndex.setCreateBy("TagCreator");

        AuthInfoStore.setAuthInfo("TagCreator");
        Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

        TagUserRelation relation = new TagUserRelation();
        relation.setTagId(tagId);
        relation.setUserId("Someone");
        tagUserRelationDao.saveOrUpdate(relation);

        doReturn(new HashSet<String>()).when(userManager).checkNonExistingUserID(any());

        doAnswer(invocation -> {
            List<Long> tagIds = ((List<String>) invocation.getArguments()[0]).stream().map(Long::valueOf).
                    collect(Collectors.toList());
            List<String> userIds = (List<String>) invocation.getArguments()[1];
            tagIds.forEach(id -> {
                userIds.forEach(userId -> {
                    tagUserRelationDao.deleteByTagIdAndUserId(id, userId);
                });
            });
            return null;
        }).when(tagThingManager).unbindTagFromUser(anyList(), anyList());

        try {
            tagController.unbindTagFromUser(tagId + "", "Someone");
        } catch (Exception e) {
            fail("Should not throw any exception");
        }

        relation = tagUserRelationDao.find(tagId, "Someone");
        assertNull("Should not have the relation", relation);
    }

    @Test
    public void testBindUserGroupToTag() throws Exception {
        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);
        AuthInfoStore.setAuthInfo("TagCreator");
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
                    tagController.bindTagToUserGroup(tagIds, userGroupIds);
                    fail("Expect a PortalException");
                } catch (PortalException e) {
                    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
                }
            }
        }

        // Existence test
        try {
            tagController.bindTagToUserGroup(tagId + ",test2", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.bindTagToUserGroup(tagId + "", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }

        AuthInfoStore.setAuthInfo(tagIndex.getCreateBy());
        try {
            tagController.bindTagToUserGroup(tagId + "", userGroupId + ",userGroup1");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.bindTagToUserGroup(tagId + "", userGroupId + "");
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
        AuthInfoStore.setAuthInfo("TagCreator");
        Long tagId = tagIndexDao.saveOrUpdate(tagIndex);

        UserGroup userGroup = new UserGroup();
        userGroup.setName("User Group");
        userGroup.setCreateBy("Someone");
        Long userGroupId = userGroupDao.saveOrUpdate(userGroup);

        AuthInfoStore.setAuthInfo(tagIndex.getCreateBy());
        tagController.bindTagToUserGroup(tagId + "", userGroupId + "");

        TagGroupRelation relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId);
        assertNotNull("Should have the relation", relation);

        AuthInfoStore.setAuthInfo("Someone");

        // Error test
        String[] blankTagIds = new String[]{null, " "};
        String[] blankUserGroupIds = new String[]{null, " "};
        for (String tagIds : blankTagIds) {
            for (String userGroupIds : blankUserGroupIds) {
                try {
                    tagController.unbindTagFromUserGroup(tagIds, userGroupIds);
                    fail("Expect a PortalException");
                } catch (PortalException e) {
                    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
                }
            }
        }

        // Existence test
        try {
            tagController.unbindTagFromUserGroup(tagId + ",test2", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.unbindTagFromUserGroup(tagId + "", userGroupId + "");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }

        AuthInfoStore.setAuthInfo(tagIndex.getCreateBy());
        try {
            tagController.unbindTagFromUserGroup(tagId + "", userGroupId + ",userGroup1");
            fail("Expect a PortalException");
        } catch (PortalException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        try {
            tagController.unbindTagFromUserGroup(tagId + "", userGroupId + "");
        } catch (Exception e) {
            fail("Should not throw any exception");
        }

        relation = tagGroupRelationDao.findByTagIDAndUserGroupID(tagId, userGroupId);
        assertNull("Should not have the relation", relation);
    }

    @Test
    public void testFindTags() throws Exception {
        AuthInfoStore.setAuthInfo("TagCreator");
        Set<Long> tagIds = new HashSet<>();
        TagIndex tagIndex = new TagIndex();
        tagIndex.setDisplayName("Tag 1");
        tagIndex.setDescription("Tag");
        tagIndex.setTagType(TagType.Custom);
        tagIndex.setCreateBy("TagCreator");
        tagIds.add(tagIndexDao.saveOrUpdate(tagIndex));

        tagIndex.setDisplayName("Tag 2");
        tagIndex.setTagType(TagType.Location);
        tagIds.add(tagIndexDao.saveOrUpdate(tagIndex));

        tagIndex.setDisplayName("Tag 3");
        tagIndex.setTagType(TagType.System);
        tagIds.add(tagIndexDao.saveOrUpdate(tagIndex));

        AuthInfoStore.setAuthInfo("TagCreator 2");
        tagIndex.setDisplayName("Tag 3");
        tagIndex.setTagType(TagType.System);
        tagIndex.setCreateBy("TagCreator 2");
        tagIds.add(tagIndexDao.saveOrUpdate(tagIndex));

        AuthInfoStore.setAuthInfo("Someone");

        List<TagIndex> tagIndexes = tagController.findTags("", "");
        assertNotNull("Result should not be null", tagIndexes);
        assertTrue("Result should be an empty list", tagIndexes.isEmpty());

        tagIndexes = tagController.findTags("Tag 1", TagType.Custom.name());
        assertNotNull("Result should not be null", tagIndexes);
        assertTrue("Result should be an empty list", tagIndexes.isEmpty());

        AuthInfoStore.setAuthInfo("TagCreator");

        tagIndexes = tagController.findTags("", "");
        assertNotNull("Result should not be null", tagIndexes);
        assertEquals("There should be three tags", 3, tagIndexes.size());

        tagIndexes = tagController.findTags("", "Tag 1");
        assertNotNull("Result should not be null", tagIndexes);
        assertEquals("There should be one tag", 1, tagIndexes.size());
        assertEquals("Display name should be 'Tag 1'", "Tag 1", tagIndexes.get(0).getDisplayName());
        assertEquals("Tag type doesn't match", TagType.Custom, tagIndexes.get(0).getTagType());

        tagIndexes = tagController.findTags("", "Tag 2");
        assertNotNull("Result should not be null", tagIndexes);
        assertEquals("There should be one tag", 1, tagIndexes.size());
        assertEquals("Display name should be 'Tag 1'", "Tag 2", tagIndexes.get(0).getDisplayName());
        assertEquals("Tag type doesn't match", TagType.Location, tagIndexes.get(0).getTagType());

        tagIndexes = tagController.findTags("", "Tag 3");
        assertNotNull("Result should not be null", tagIndexes);
        assertEquals("There should be one tag", 1, tagIndexes.size());
        assertEquals("Display name should be 'Tag 3'", "Tag 3", tagIndexes.get(0).getDisplayName());
        assertEquals("Tag type doesn't match", TagType.System, tagIndexes.get(0).getTagType());

        AuthInfoStore.setAuthInfo("TagCreator 2");
        tagIndexes = tagController.findTags(TagType.System.name(), "");
        assertNotNull("Result should not be null", tagIndexes);
        assertEquals("There should be one tag", 1, tagIndexes.size());
        assertEquals("Display name should be 'Tag 3'", "Tag 3", tagIndexes.get(0).getDisplayName());
        assertEquals("Tag type doesn't match", TagType.System, tagIndexes.get(0).getTagType());
    }

    @Test
    public void testFindLocation() throws Exception {
        Set<String> displayNames = new HashSet<>();
        displayNames.add("floor1-lobby");
        displayNames.add("floor1-room1-counter1");
        displayNames.add("floor1-room1-counter2");
        displayNames.add("floor2-room1");
        displayNames.add("floor2-room2");

        AuthInfoStore.setAuthInfo("TagCreator");
        TagIndex tagIndex = new TagIndex();
        tagIndex.setTagType(TagType.Location);
        displayNames.forEach(name -> {
            tagIndex.setDisplayName(name);
            tagIndexDao.saveOrUpdate(tagIndex);
        });

        List<String> locations = tagController.findAllLocations();
        assertNotNull("Location should not be null", locations);
        assertTrue("Didn't find all locations", displayNames.containsAll(locations) &&
                locations.containsAll(displayNames));

        locations = tagController.findLocations("floor2");
        assertNotNull("Location should not be null", locations);
        locations.forEach(location -> assertTrue("Location should start with floor2", location.startsWith("floor2-")));

        locations = tagController.findLocations("room1");
        assertTrue("Should not find any locations", null == locations || locations.isEmpty());

        AuthInfoStore.setAuthInfo("Someone");
        locations = tagController.findLocations("");
        assertTrue("Should not find any locations", null == locations || locations.isEmpty());
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
    public void testWebFindLocations() throws Exception {

        // create location
        List<String> displayNames = new ArrayList<>();
        displayNames.add("floor1-lobby");
        displayNames.add("floor1-room1-counter1");
        displayNames.add("floor1-room1-counter2");
        displayNames.add("floor2-room1");
        displayNames.add("floor2-room2");
        List<Long> tagIDs = new ArrayList<>();

        AuthInfoStore.setAuthInfo("211102");
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
