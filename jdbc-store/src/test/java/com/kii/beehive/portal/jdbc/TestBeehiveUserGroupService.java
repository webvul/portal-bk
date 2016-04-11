//package com.kii.beehive.portal.jdbc;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runners.Suite;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.beehive.portal.service.BeehiveUserDao;
//import com.kii.beehive.portal.jdbc.dao.BeehiveUserGroupDao;
//import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
//import com.kii.beehive.portal.jdbc.entity.BeehiveUserGroup;
//import com.kii.beehive.portal.service.BeehiveUserGroupService;
//import com.kii.beehive.portal.service.BeehiveUserService;
//
//import static junit.framework.Assert.assertNotNull;
//import static junit.framework.Assert.assertNull;
//import static junit.framework.Assert.assertTrue;
//import static junit.framework.TestCase.assertEquals;
//
//
//public class TestBeehiveUserGroupService extends TestTemplate {
//
//    @Autowired
//    private BeehiveUserGroupService beehiveUserGroupService;
//
//    @Autowired
//    private BeehiveUserService beehiveUserService;
//
//    @Autowired
//    private BeehiveUserGroupDao beehiveUserGroupDao;
//
//    @Autowired
//    private BeehiveUserDao beehiveUserDao;
//
//    @Test
//    public void testCreateUserGroup() {
//
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        //
//        group = beehiveUserGroupDao.findByID(userGroupID);
//
//        assertEquals(userGroupID, group.getId());
//        assertEquals("usergroupname", group.getUserGroupName());
//        assertEquals("somedecription", group.getDescription());
//
//    }
//
////    @Test
//    public void testUpdateUserGroup() {
//
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        //
//        group.setUserGroupName("usergroupname.new");
//        group.setDescription("somedescription.new");
//
//        beehiveUserGroupService.updateUserGroup(group);
//
//        group = beehiveUserGroupDao.findByID(userGroupID);
//
//        assertEquals(userGroupID, group.getId());
//        assertEquals("usergroupname.new", group.getUserGroupName());
//        assertEquals("somedescription.new", group.getDescription());
//
//    }
//
//    @Test
//    public void testDeleteUserGroup() {
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        //
//        group = beehiveUserGroupDao.findByID(userGroupID);
//
//        assertNotNull(group);
//
//        //
//        boolean isExist = beehiveUserGroupService.checkUserGroupIDExist(userGroupID);
//        assertTrue(isExist);
//
//        //
//        isExist = beehiveUserGroupService.checkUserGroupNameExist("usergroupname");
//        assertTrue(isExist);
//
//        //
//        beehiveUserGroupService.deleteUserGroup(userGroupID);
//
//        group = beehiveUserGroupDao.findByID(userGroupID);
//
//        assertNull(group);
//
//        //
//        isExist = beehiveUserGroupService.checkUserGroupIDExist(userGroupID);
//        assertTrue(!isExist);
//
//        //
//        isExist = beehiveUserGroupService.checkUserGroupNameExist("usergroupname");
//        assertTrue(!isExist);
//
//    }
//
//    @Test
//    public void testGetUserGroupByName() {
//
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        //
//        group = beehiveUserGroupService.getUserGroupByName("usergroupname");
//        assertNotNull(group);
//
//    }
//
//    @Test
//    public void testFindUserGroupsByNameLike() {
//
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        //
//        List<BeehiveUserGroup> list = beehiveUserGroupService.findUserGroupsByNameLike("group");
//
//        assertEquals(1, list.size());
//        assertEquals(userGroupID, list.get(0).getId());
//
//        list = beehiveUserGroupService.findUserGroupsByNameLike("user");
//
//        assertEquals(1, list.size());
//        assertEquals(userGroupID, list.get(0).getId());
//
//        list = beehiveUserGroupService.findUserGroupsByNameLike("name");
//
//        assertEquals(1, list.size());
//        assertEquals(userGroupID, list.get(0).getId());
//
//    }
//
//    @Test
//    public void testAddUsers() {
//
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        BeehiveUser user1 = new BeehiveUser();
//        user1.setUserName("userName1") ;
//        user1.setKiiUserID("kiiUserID1");
//        user1.setKiiLoginName("kiiLoginName1");
//        long userID1 = beehiveUserDao.createUser(user1);
//
//        BeehiveUser user2 = new BeehiveUser();
//        user2.setUserName("userName2") ;
//        user2.setKiiUserID("kiiUserID2");
//        user2.setKiiLoginName("kiiLoginName2");
//        long userID2 = beehiveUserDao.createUser(user2);
//
//        List<Long> userIDs = new ArrayList<>();
//        userIDs.add(userID1);
//        userIDs.add(userID2);
//        // user id 10000 is not supposed to exist
//        userIDs.add((long)10000);
//
//        List<Long> result = beehiveUserGroupService.addUsers(userGroupID, userIDs);
//
//        assertEquals(1, result.size());
//        assertEquals(Long.valueOf(10000), result.get(0));
//
//        //
//        List<BeehiveUser> users = beehiveUserService.findUsersByUserGroupID(userGroupID);
//
//        assertEquals(2, users.size());
//        assertEquals(userID1, users.get(0).getId());
//        assertEquals(userID2, users.get(1).getId());
//    }
//
//    @Test
//    public void testRemoveUsers() {
//
//        //
//        BeehiveUserGroup group = new BeehiveUserGroup();
//        group.setUserGroupName("usergroupname");
//        group.setDescription("somedecription");
//
//        long userGroupID = beehiveUserGroupService.createUserGroup(group);
//
//        System.out.println("userGroupID:" + userGroupID);
//
//        BeehiveUser user1 = new BeehiveUser();
//        user1.setUserName("userName1") ;
//        user1.setKiiUserID("kiiUserID1");
//        user1.setKiiLoginName("kiiLoginName1");
//        long userID1 = beehiveUserDao.createUser(user1);
//
//        BeehiveUser user2 = new BeehiveUser();
//        user2.setUserName("userName2") ;
//        user2.setKiiUserID("kiiUserID2");
//        user2.setKiiLoginName("kiiLoginName2");
//        long userID2 = beehiveUserDao.createUser(user2);
//
//        List<Long> userIDs = new ArrayList<>();
//        userIDs.add(userID1);
//        userIDs.add(userID2);
//
//        beehiveUserGroupService.addUsers(userGroupID, userIDs);
//
//        //
//        List<BeehiveUser> users = beehiveUserService.findUsersByUserGroupID(userGroupID);
//
//        assertEquals(2, users.size());
//
//        //
//        beehiveUserGroupService.removeUsers(userGroupID, userIDs);
//
//        users = beehiveUserService.findUsersByUserGroupID(userGroupID);
//
//        assertEquals(0, users.size());
//
//    }
//
//}
