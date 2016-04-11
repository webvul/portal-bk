package com.kii.beehive.portal.web.controller;


import com.kii.beehive.business.manager.UserManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.exception.InvalidAuthException;
import com.kii.beehive.portal.jdbc.dao.GroupUserRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagGroupRelationDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.entity.GroupUserRelation;
import com.kii.beehive.portal.jdbc.entity.TagGroupRelation;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.UserGroup;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.entity.UserGroupRestBean;
import com.kii.beehive.portal.web.exception.BeehiveUnAuthorizedException;
import com.kii.beehive.portal.web.exception.PortalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Beehive API - User API
 * <p>
 * refer to doc "Tech Design - Beehive API" section "User API" for details
 */
@RestController
@RequestMapping(path = "/usergroup")
public class UserGroupController {

	@Autowired
	private BeehiveUserDao beehiveUserDao;

	@Autowired
	private TagGroupRelationDao tagGroupRelationDao;

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	protected UserManager userManager;

	@Autowired
	protected UserGroupDao userGroupDao;


	@Autowired
	protected GroupUserRelationDao groupUserRelationDao;

	/**
	 * 创建用户群组
	 * POST /usergroup
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Create User Group (创建用户群组)" for more details
	 *
	 * @param userGroupRestBean
	 */
	@RequestMapping(path = "", method = {RequestMethod.POST})
	public ResponseEntity createUserGroup(@RequestBody UserGroupRestBean userGroupRestBean, HttpServletRequest httpRequest) {

		userGroupRestBean.verifyInput();

		UserGroup userGroup = userGroupRestBean.getUserGroup();
		Long userGroupID = null;
		if (userGroup.getId() == null) {//create
			userGroupID = userManager.createUserGroup(userGroup, AuthInfoStore.getUserID());
		} else {//update
			userGroupID = userManager.updateUserGroup(userGroup, AuthInfoStore.getUserID());
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
	@RequestMapping(path = "/{userGroupID}/user/{userIDs}", method = {RequestMethod.POST})
	public ResponseEntity addUsersToUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userIDs") String userIDs) {
		UserGroup ug = this.userGroupDao.findByID(userGroupID);
		if (!ug.getCreateBy().equals(AuthInfoStore.getUserID())) {
			throw new InvalidAuthException(ug.getCreateBy(), AuthInfoStore.getUserID());
		}

		List<String> userIDList = Arrays.asList(userIDs.split(","));
		userManager.addUserToUserGroup(userIDList, userGroupID);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 复数用户從群组刪除
	 * DELETE /usergroup/{userGroupID}/user/{userID...}
	 *
	 * @param userGroupID
	 */
	@RequestMapping(path = "/{userGroupID}/user/{userIDs}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public ResponseEntity removeUsersFromUserGroup(@PathVariable("userGroupID") Long userGroupID, @PathVariable("userIDs") String userIDs) {
		UserGroup ug = this.userGroupDao.findByID(userGroupID);
		if (ug == null) {
			throw new PortalException("UserGroup Not Found", "UserGroup with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		} else if (!ug.getCreateBy().equals(AuthInfoStore.getUserID())) {
			throw new BeehiveUnAuthorizedException("Current user is not the creator of the user group.");
		} else {
			List<String> userIDList = new ArrayList<String>();
			String[] userIDArray = userIDs.split(",");
			for (String uID : userIDArray) {
				if (!uID.equals(AuthInfoStore.getUserID())) {
					userIDList.add(uID);
				} else {
					throw new BeehiveUnAuthorizedException("the creator can't remove");
				}
			}
			if (userIDList.size() > 0) {
				groupUserRelationDao.deleteUsers(userIDList, userGroupID);
			}

		}
		return new ResponseEntity<>(HttpStatus.OK);
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
	@RequestMapping(path = "/{userGroupID}", method = {RequestMethod.DELETE}, consumes = {"*"})
	public ResponseEntity deleteUserGroup(@PathVariable("userGroupID") Long userGroupID) {

		UserGroup orig = userGroupDao.findByID(userGroupID);

		if (orig == null) {
			throw new PortalException("UserGroup Not Found", "UserGroup with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		} else if (!orig.getCreateBy().equals(AuthInfoStore.getUserID())) {
			throw new BeehiveUnAuthorizedException("Current user is not the creator of the user group.");
		}

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
	@RequestMapping(path = "/{userGroupID}", method = {RequestMethod.GET}, consumes = {"*"})
	public ResponseEntity<UserGroupRestBean> getUserGroupDetail(@PathVariable("userGroupID") Long userGroupID) {
		UserGroupRestBean ugrb = null;
		if (isGroupOfUser(AuthInfoStore.getUserID(), userGroupID)) {
			List<GroupUserRelation> relList = groupUserRelationDao.findByUserGroupID(userGroupID);
			if (relList.size() > 0) {
				List<String> userIdList = new ArrayList<String>();
				relList.forEach(gur -> userIdList.add(gur.getUserID()));
				List<BeehiveUser> list = beehiveUserDao.getUserByIDs(userIdList);
				UserGroup ug = userGroupDao.findByID(userGroupID);
				ugrb = new UserGroupRestBean(ug);
				ugrb.setUsers(list);
			}
		} else {
			throw new BeehiveUnAuthorizedException("Current user is not in the user group.");
		}
		if (ugrb == null) {
			throw new PortalException("UserGroup Not Found", "UserGroup with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(ugrb, HttpStatus.OK);
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
	@RequestMapping(path = "/{userGroupID}/tags", method = {RequestMethod.GET}, consumes = {"*"})
	public ResponseEntity<List<TagIndex>> getUserGroupTag(@PathVariable("userGroupID") Long userGroupID, HttpServletRequest httpRequest) {
		List<TagIndex> tagList = null;
		if (isGroupOfUser(AuthInfoStore.getUserID(), userGroupID)) {
			List<Long> tagIDList = new ArrayList<Long>();
			List<TagGroupRelation> relList = tagGroupRelationDao.findByUserGroupID(userGroupID);
			if (relList.size() > 0) {
				relList.forEach(tgr -> tagIDList.add(tgr.getTagID()));
				tagList = tagIndexDao.findByIDs(tagIDList);
			}
		} else {
			throw new BeehiveUnAuthorizedException("Current user is not in the user group.");
		}
		if (tagList == null) {
			throw new PortalException("UserGroup Not Found", "UserGroup with userGroupID:" + userGroupID + " Not Found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(tagList, HttpStatus.OK);
	}

	/**
	 * 列出用户群组
	 * GET /usergroup/list
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Inquire User Group (查询用户群组)" for more details
	 */
	@RequestMapping(path = "/list", method = {RequestMethod.GET}, consumes = {"*"})
	public ResponseEntity<List<UserGroupRestBean>> getUserGroupList() {
		List<UserGroup> list = list = userGroupDao.findUserGroup(AuthInfoStore.getUserID(), null, null);
		List<UserGroupRestBean> restBeanList = this.convertList(list);
		return new ResponseEntity<>(restBeanList, HttpStatus.OK);
	}

	@RequestMapping(path = "/all", method = {RequestMethod.GET}, consumes = {"*"})
	public ResponseEntity<List<UserGroupRestBean>> getUserGroupAll() {
		List<UserGroup> list = userGroupDao.findAll();
		List<UserGroupRestBean> restBeanList = this.convertList(list);
		return new ResponseEntity<>(restBeanList, HttpStatus.OK);
	}

	private boolean isGroupOfUser(String loginUserID, Long userGroupID) {
		List<UserGroup> checkAuth = userGroupDao.findUserGroup(loginUserID, userGroupID, null);

		if (checkAuth.size() == 1) {
			return true;
		} else {
			throw new BeehiveUnAuthorizedException("loginUser isn't in the group");
		}
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
