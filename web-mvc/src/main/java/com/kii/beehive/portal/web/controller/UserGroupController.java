package com.kii.beehive.portal.web.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.UserGroupRestBean;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - User API
 *
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/usergroup",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserGroupController extends AbstractController{
	
	@Autowired
	private BeehiveUserDao beehiveUserDao;
	
    /**
     * 创建用户群组
     * POST /usergroup
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Create User Group (创建用户群组)" for more details
     *
     * @param userGroupRestBean
     */
    @RequestMapping(path="",method={RequestMethod.POST})
    public ResponseEntity createUserGroup(@RequestBody UserGroupRestBean userGroupRestBean, HttpServletRequest httpRequest){

		userGroupRestBean.verifyInput();

		UserGroup userGroup = userGroupRestBean.getUserGroup();
        String loginUserID = getLoginUserID(httpRequest);
        Long userGroupID = null;
        if(userGroup.getId() == null){//create
        	userGroupID = userManager.createUserGroup(userGroup, loginUserID);
        }else{//update
        	userGroupID = userManager.updateUserGroup(userGroup, loginUserID);
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("userGroupID", String.valueOf(userGroupID));
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
    
    /**
     * 用户加入群组
     * POST /usergroup/{userGroupID}/user/{userID}
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}/user/{userID}",method={RequestMethod.POST})
    public ResponseEntity addUserToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userID") String userID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	
		if(checkUserGroup(loginUserID, userGroupID)){
			List<UserGroup> orgiList = userGroupDao.findUserGroup(userID, userGroupID, null);
			if(orgiList.size() == 0){
				GroupUserRelation gur = new GroupUserRelation(userID, userGroupID);
	    		groupUserRelationDao.saveOrUpdate(gur);
			}
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 用户從群组刪除
     * PUT /usergroup/{userGroupID}/user/{userID}
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}/user/{userID}",method={RequestMethod.DELETE})
    public ResponseEntity removeUserToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userID") String userID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	
		if(checkUserGroup(loginUserID, userGroupID)){
    		groupUserRelationDao.delete(userID, userGroupID);
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }

	/**
	 * 复数用户加入群组
	 * POST /usergroup/{userGroupID}/users/{userID...}
	 *
	 * @param userGroupID
	 */
	@RequestMapping(path="/{userGroupID}/users/{userIDs}",method={RequestMethod.POST})
	public ResponseEntity addUsersToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userIDs") String userIDs, HttpServletRequest httpRequest){
		String loginUserID = getLoginUserID(httpRequest);

		if(checkUserGroup(loginUserID, userGroupID)){

			List<String> userIDList = Arrays.asList(userIDs.split(","));
			userManager.addUserToUserGroup(userIDList, userGroupID);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 复数用户從群组刪除
	 * PUT /usergroup/{userGroupID}/users/{userID...}
	 *
	 * @param userGroupID
	 */
	@RequestMapping(path="/{userGroupID}/users/{userIDs}",method={RequestMethod.DELETE})
	public ResponseEntity removeUsersFromUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userIDs") String userIDs, HttpServletRequest httpRequest){
		String loginUserID = getLoginUserID(httpRequest);

		if(checkUserGroup(loginUserID, userGroupID)){

			List<String> userIDList = Arrays.asList(userIDs.split(","));
			groupUserRelationDao.deleteUsers(userIDList, userGroupID);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

    /**
     * 删除用户群组
     * DELETE /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Delete User Group (删除用户群组)" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}",method={RequestMethod.DELETE})
    public ResponseEntity deleteUserGroup(@PathVariable("userGroupID") Long userGroupID){

    	UserGroup orig =  userGroupDao.findByID(userGroupID);
		
		if(orig == null){
			throw new PortalException("Thing Not Found", "Thing with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		}
		
		userManager.deleteUserGroup(userGroupID);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 取得群組內的用戶
     * GET /usergroup/{userGroupID}
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Detail User Group" for more details
     *
     * @param userGroupID
     */
    @RequestMapping(path="/{userGroupID}",method={RequestMethod.GET})
    public ResponseEntity getUserGroupDetail(@PathVariable("userGroupID") Long userGroupID, HttpServletRequest httpRequest){
    	String loginUserID = getLoginUserID(httpRequest);
    	UserGroupRestBean ugrb = null;
		if(checkUserGroup(loginUserID, userGroupID)){
			List<String> userIdList = new ArrayList<String>(); 
			List<GroupUserRelation> relList = groupUserRelationDao.findByUserGroupID(userGroupID);
			if(relList.size() > 0){
				for(GroupUserRelation gur:relList){
					userIdList.add(gur.getUserID());
				}
				List<BeehiveUser> list = beehiveUserDao.getUserByIDs(userIdList);
				UserGroup ug = userGroupDao.findByID(userGroupID);
				ugrb = new UserGroupRestBean(ug);
				ugrb.setUsers(list);
			}
		}

        return new ResponseEntity<>(ugrb, HttpStatus.OK);
    }
    
    /**
     * 列出用户群组
     * POST /usergroup/list
     *
     * refer to doc "Beehive API - User API" for request/response details
     * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
     *
     * @param httpRequest
     */
    @RequestMapping(path = "/list", method = {RequestMethod.GET})
	public ResponseEntity<List<UserGroupRestBean>> getUserGroupList(HttpServletRequest httpRequest) {
    	String loginUserID = getLoginUserID(httpRequest);
		List<UserGroup> list = userGroupDao.findUserGroup(loginUserID, null , null);
		List<UserGroupRestBean> restBeanList = this.convertList(list);
		return new ResponseEntity<>(restBeanList, HttpStatus.OK);
	}
    
    @RequestMapping(path = "/all", method = {RequestMethod.GET})
	public ResponseEntity<List<UserGroupRestBean>> getUserGroupAll(HttpServletRequest httpRequest) {
		List<UserGroup> list = userGroupDao.findAll();
		List<UserGroupRestBean> restBeanList = this.convertList(list);
		return new ResponseEntity<>(restBeanList, HttpStatus.OK);
	}
    
    private boolean checkUserGroup(String loginUserID, Long userGroupID){
    	//loginUser can edit, when loginUser is in this group ,
    	List<UserGroup> checkAuth = userGroupDao.findUserGroup(loginUserID, userGroupID, null);
		if(checkAuth.size() == 1){
			return true;
		}else{
			return false;
		}
    }

	private List<UserGroupRestBean> convertList(List<UserGroup> userGroupList) {

		List<UserGroupRestBean> list = new ArrayList<>();

		if(userGroupList != null) {
			for(UserGroup userGroup : userGroupList) {
				list.add(new UserGroupRestBean(userGroup));
			}
		}

		return list;
	}

}
