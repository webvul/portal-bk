//package com.kii.beehive.portal.jdbc;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.kii.beehive.portal.service.BeehiveUserDao;
//import com.kii.beehive.portal.jdbc.entity.BeehiveUser;
//import com.kii.beehive.portal.service.BeehiveUserService;
//
//import static junit.framework.Assert.assertNull;
//import static junit.framework.TestCase.assertEquals;
//
//public class TestBeehiveUserService extends TestTemplate {
//
//    @Autowired
//    private BeehiveUserService beehiveUserService;
//
//    @Test
//    public void testAddUser(){
//
//        //
//        BeehiveUser user = new BeehiveUser();
//
//        user.setKiiUserID("kiiUserID");
//        user.setKiiLoginName("kiiLoginName");
//        user.setUserName("userName") ;
//        user.setPhone("phone") ;
//        user.setMail("mail") ;
//        user.setCompany("company");
//        user.setRole("1");
//
//        long userID = beehiveUserService.addUser(user);
//
//        System.out.println("userID: " + userID);
//
//        //
//        user = beehiveUserService.getUserByID(userID);
//
//        assertEquals(userID, user.getId());
//        assertEquals("kiiUserID", user.getKiiUserID());
//        assertEquals("kiiLoginName", user.getKiiLoginName());
//        assertEquals("userName", user.getUserName());
//        assertEquals("phone", user.getPhone());
//        assertEquals("mail", user.getMail());
//        assertEquals("company", user.getCompany());
//        assertEquals("1", user.getRole());
//
//    }
//
////    @Test
//    // TODO insertTool doesn't update
//    public void testUpdateUser() {
//
//        //
//        BeehiveUser user = new BeehiveUser();
//
//        user.setKiiUserID("kiiUserID");
//        user.setKiiLoginName("kiiLoginName");
//        user.setUserName("userName") ;
//        user.setPhone("phone") ;
//        user.setMail("mail") ;
//        user.setCompany("company");
//        user.setRole("1");
//
//        long userID = beehiveUserService.addUser(user);
//
//        System.out.println("userID: " + userID);
//
//        //
//        user = beehiveUserService.getUserByID(userID);
//
//        assertEquals(userID, user.getId());
//        assertEquals("kiiUserID", user.getKiiUserID());
//        assertEquals("kiiLoginName", user.getKiiLoginName());
//        assertEquals("userName", user.getUserName());
//        assertEquals("phone", user.getPhone());
//        assertEquals("mail", user.getMail());
//        assertEquals("company", user.getCompany());
//        assertEquals("1", user.getRole());
//
//        //
//        user.setKiiUserID("kiiUserID.new");
//        user.setKiiLoginName("kiiLoginName.new");
//        user.setUserName("userName.new") ;
//        user.setPhone("phone.new") ;
//        user.setMail("mail.new") ;
//        user.setCompany("company.new");
//        user.setRole("2");
//
//        System.out.println("userID: " + userID);
//        System.out.println("KiiLoginName: " + user.getKiiLoginName());
//
//        beehiveUserService.updateUser(user, user.getId());
//
//        System.out.println("userID: " + userID);
//
//        //
//        user = beehiveUserService.getUserByID(userID);
//
//        System.out.println("userID: " + userID);
//        System.out.println("KiiLoginName: " + user.getKiiLoginName());
//
//        assertEquals(userID, user.getId());
//        assertEquals("kiiUserID.new", user.getKiiUserID());
//        assertEquals("kiiLoginName.new", user.getKiiLoginName());
//        assertEquals("userName.new", user.getUserName());
//        assertEquals("phone.new", user.getPhone());
//        assertEquals("mail.new", user.getMail());
//        assertEquals("company.new", user.getCompany());
//        assertEquals("2", user.getRole());
//    }
//
//    @Test
//    public void testSimpleQueryUser(){
//
//        //
//        BeehiveUser user = new BeehiveUser();
//
//        user.setKiiUserID("kiiUserID");
//        user.setKiiLoginName("kiiLoginName");
//        user.setUserName("userName") ;
//        user.setPhone("phone") ;
//        user.setMail("mail") ;
//        user.setCompany("company");
//        user.setRole("1");
//
//        long userID = beehiveUserService.addUser(user);
//
//        System.out.println("userID: " + userID);
//
//        //
//        Map<String, Object> map = new HashMap<>();
//        map.put(BeehiveUser.USER_NAME, "am");
//        map.put(BeehiveUser.PHONE, "ho");
//        map.put(BeehiveUser.MAIL, "ai");
//        map.put(BeehiveUser.COMPANY, "pa");
//        map.put(BeehiveUser.ROLE, "1");
//
//        List<BeehiveUser> userList = beehiveUserService.simpleQueryUser(map);
//
//        assertEquals(1, userList.size());
//
//        user = userList.get(0);
//
//        assertEquals(userID, user.getId());
//        assertEquals("kiiUserID", user.getKiiUserID());
//        assertEquals("kiiLoginName", user.getKiiLoginName());
//        assertEquals("userName", user.getUserName());
//        assertEquals("phone", user.getPhone());
//        assertEquals("mail", user.getMail());
//        assertEquals("company", user.getCompany());
//        assertEquals("1", user.getRole());
//
//    }
//
//
//    @Test
//    public void testDeleteUser() {
//
//        //
//        BeehiveUser user = new BeehiveUser();
//
//        user.setKiiUserID("kiiUserID");
//        user.setKiiLoginName("kiiLoginName");
//        user.setUserName("userName") ;
//        user.setPhone("phone") ;
//        user.setMail("mail") ;
//        user.setCompany("company");
//        user.setRole("1");
//
//        long userID = beehiveUserService.addUser(user);
//
//        System.out.println("userID: " + userID);
//
//        //
//        user = beehiveUserService.getUserByID(userID);
//
//        assertEquals(userID, user.getId());
//        assertEquals("kiiUserID", user.getKiiUserID());
//        assertEquals("kiiLoginName", user.getKiiLoginName());
//        assertEquals("userName", user.getUserName());
//        assertEquals("phone", user.getPhone());
//        assertEquals("mail", user.getMail());
//        assertEquals("company", user.getCompany());
//        assertEquals("1", user.getRole());
//
//        //
//        beehiveUserService.deleteUser(user.getId());
//
//        System.out.println("userID: " + userID);
//
//        //
//        user = beehiveUserService.getUserByID(userID);
//
//        assertNull(user);
//    }
//
//
//}
