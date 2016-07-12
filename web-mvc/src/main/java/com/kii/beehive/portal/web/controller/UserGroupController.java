package com.kii.beehive.portal.web.controller;


import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.common.manager.UserManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.web.entity.UserGroupRestBean;

/**
 * Beehive API - User API
 * <p>
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(value = "/usergroup")
public class UserGroupController {

//	@Autowired
//	private BeehiveUserDao beehiveUserDao;

//	@Autowired
//	private BeehiveUserJdbcDao beehiveUserDao;
//
//	@Autowired
//	private TagGroupRelationDao tagGroupRelationDao;
//
//	@Autowired
//	private TagIndexDao tagIndexDao;

	@Autowired
	protected UserManager userManager;

//	@Autowired
//	protected UserGroupDao userGroupDao;
//
//
//	@Autowired
//	protected GroupUserRelationDao groupUserRelationDao;

	/**
	 * 创建用户群组
	 * POST /usergroup
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Create User Group (创建用户群组)" for more details
	 *
	 * @param userGroupRestBean
	 */
	@RequestMapping(value = "", method = {RequestMethod.POST})
	public ResponseEntity createUserGroup(@RequestBody UserGroupRestBean userGroupRestBean, HttpServletRequest httpRequest) {

		userGroupRestBean.verifyInput();

		UserGroup userGroup = userGroupRestBean.getUserGroup();
		Long userGroupID = null;
		if (userGroup.getId() == null) {//create
			userGroupID = userManager.createUserGroup(userGroup, AuthInfoStore.getUserIDInLong());
		} else {//update
			userGroupID = userManager.updateUserGroup(userGroup, AuthInfoStore.getUserIDInLong());
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("userGroupID", String.valueOf(userGroupID));
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	/**
	 * 复数用户加入群组
	 * POST /usergroup/{userGroupID}/user/{userID...}
	 *
	 * @param userGroupID
	 */
	@RequestMapping(value = "/{userGroupID}/user/{userIDs}", method = {RequestMethod.POST})
	public ResponseEntity addUsersToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userIDs") String userIDs) {


		List<String> userIDList = getIDList(userIDs);
		userManager.addUserToUserGroup(userIDList, userGroupID);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private List<String> getIDList(@PathVariable("userIDs") String userIDs) {
		return  Arrays.asList(userIDs.split(","));
	}

	/**
	 * 复数用户從群组刪除
	 * DELETE /usergroup/{userGroupID}/user/{userID...}
	 *
	 * @param userGroupID
	 */
	@RequestMapping(value = "/{userGroupID}/user/{userIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public  void removeUsersFromUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userIDs") String userIDs) {

		userManager.removeUserFromGroup(userGroupID,getIDList(userIDs));

		return ;
	}

	/**
	 * 删除用户群组
	 * DELETE /usergroup/{userGroupID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Delete User Group (删除用户群组)" for more details
	 *
	 * @param userGroupID
	 */
	@RequestMapping(value = "/{userGroupID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public ResponseEntity deleteUserGroup(@PathVariable("userGroupID") Long userGroupID) {



		userManager.deleteUserGroup(userGroupID);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 取得群組內的用戶
	 * GET /usergroup/{userGroupID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Detail User Group" for more details
	 *
	 * @param userGroupID
	 */
	@RequestMapping(value = "/{userGroupID}", method = {RequestMethod.GET}, consumes = {"*"})
	public UserGroupRestBean getUserGroupDetail(@PathVariable("userGroupID") Long userGroupID) {

		UserGroup ug=userManager.getUserGroupDetail(userGroupID);

		UserGroupRestBean ugrb = new UserGroupRestBean(ug);

		return ugrb;
	}

	/**
	 * 取得群組內的tags
	 * GET /usergroup/{userGroupID}/tags
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Detail User Group" for more details
	 *
	 * @param userGroupID
	 */
	@RequestMapping(value = "/{userGroupID}/tags", method = {RequestMethod.GET}, consumes = {"*"})
	public List<TagIndex> getUserGroupTag(@PathVariable("userGroupID") Long userGroupID) {
//		List<TagIndex> tagList = null;
//		if (isGroupOfUser(AuthInfoStore.getUserIDInLong(), userGroupID)) {
//			List<Long> tagIDList = new ArrayList<Long>();
//			List<TagGroupRelation> relList = tagGroupRelationDao.findByUserGroupID(userGroupID);
//			if (relList.size() > 0) {
//				relList.forEach(tgr -> tagIDList.add(tgr.getTagID()));
//				tagList = tagIndexDao.findByIDs(tagIDList);
//			}
//		} else {
//			throw new PortalException(ErrorCode.USERGROUP_NO_PRIVATE,HttpStatus.NOT_FOUND);
//		}
//		if (tagList == null) {
//			throw new PortalException(ErrorCode.USERGROUP_NO_PRIVATE, HttpStatus.NOT_FOUND);
//		}
		return userManager.getTagIndexList(userGroupID);

	}

	/**
	 * 列出用户群组
	 * GET /usergroup/list
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.GET}, consumes = {"*"})
	public List<UserGroupRestBean> getUserGroupList() {
		List<UserGroup> list  = userManager.findUserGroup();
		List<UserGroupRestBean> restBeanList = this.convertList(list);
		return restBeanList;
	}

	@RequestMapping(value = "/all", method = {RequestMethod.GET}, consumes = {"*"})
	public List<UserGroupRestBean> getUserGroupAll() {
		List<UserGroup> list = userManager.findAll();
		List<UserGroupRestBean> restBeanList = this.convertList(list);
		return restBeanList;
	}


	private List<UserGroupRestBean> convertList(List<UserGroup> userGroupList) {

		List<UserGroupRestBean> list = new ArrayList<>();

		if (userGroupList != null) {
			for (UserGroup userGroup : userGroupList) {
				list.add(new UserGroupRestBean(userGroup));
			}
		}

		return list;
	}

}
