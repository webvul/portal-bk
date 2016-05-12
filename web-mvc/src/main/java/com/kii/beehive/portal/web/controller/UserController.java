package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.BeehiveUserManager;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.entity.UserRestBean;
import com.kii.beehive.portal.web.exception.PortalException;


@RestController
@RequestMapping( consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController {


	@Autowired
	private BeehiveUserManager userManager;

	@Autowired
	private AuthManager authManager;


	/**
	 * 创建用户
	 * POST /users
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(value = "/usermanager", method = {RequestMethod.POST})
	public Map<String, Object> createUser(@RequestBody UserRestBean user) {

		user.verifyInput();

		BeehiveUser beehiveUser = user.getBeehiveUser();
		if(StringUtils.isEmpty(beehiveUser.getUserName())){
			if(!StringUtils.isEmpty(beehiveUser.getMail())){
				beehiveUser.setUserName(beehiveUser.getMail());
			}else if(!StringUtils.isEmpty(beehiveUser.getPhone())){
				beehiveUser.setUserName(beehiveUser.getPhone());
			}
		}

		return  userManager.addUser(beehiveUser,user.getTeamName());

	}


	@RequestMapping(path = "/usermanager/{userid}/resetpassword", method = { RequestMethod.POST })
	public Map<String,Object> resetPassword(@PathVariable("userid") String userID) {


		String token= authManager.resetPwd(userID);

		Map<String, Object> map = new HashMap<>();

		map.put("userID", userID);
		map.put("activityToken", token);

		return map;

	}


	/**
	 * 更新用户
	 * PATCH /users/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param user
	 */
	@RequestMapping(value = "/usermanager/{userID}", method = {RequestMethod.PATCH})
	public Map<String, String> updateUser(@PathVariable("userID") String userID, @RequestBody BeehiveUser user) {

		userManager.updateUser(user, userID);


		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		return map;
	}

	/**
	 * 通过userID查询用户
	 * GET /users/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(value = "/usermanager/{userID}", method = {RequestMethod.GET}, consumes = {"*"})
	public UserRestBean getUser(@PathVariable("userID") String userID) {

		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByIDDirectly(userID));

		return bean;
	}

	/**
	 * 用户修改密码
	 * POST /oauth2/changepassword
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/users/changepassword", method = { RequestMethod.POST })
	public void changePassword(@RequestBody Map<String, Object> request) {

		String oldPassword = (String)request.get("oldPassword");
		String newPassord = (String)request.get("newPassword");

		if(CollectUtils.containsBlank(oldPassword, newPassord)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,  HttpStatus.BAD_REQUEST);
		}

		authManager.changePassword(oldPassword, newPassord);

	}




	@RequestMapping(value = "/users/me", method = {RequestMethod.GET}, consumes = {"*"})
	public UserRestBean getUser() {

		String userID=AuthInfoStore.getUserID();
		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByIDDirectly(userID));

		return bean;
	}

	@RequestMapping(value = "/users/me", method = {RequestMethod.PATCH})
	public Map<String, String> updateUser( @RequestBody BeehiveUser user) {


		BeehiveUser  updateUser=new BeehiveUser();
		BeanUtils.copyProperties(user,updateUser,"kiiUserID","activityToken","userPassword","roleName","userName");
		userManager.updateUser(updateUser, AuthInfoStore.getUserID());


		Map<String, String> map = new HashMap<>();
		map.put("userID", AuthInfoStore.getUserID());
		return map;
	}


	/**
	 * 通过userID查询用户
	 * GET /users/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(value = "/users/{userID}", method = {RequestMethod.GET}, consumes = {"*"})
	public UserRestBean getUserByID(@PathVariable("userID") String userID) {

		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByID(userID));

		return bean;
	}


	@RequestMapping(value = "/users/permissionTree", method = {RequestMethod.GET},consumes = {"*"})
	public PermissionTree getUserPermissTree(){

		String userID=AuthInfoStore.getUserID();

		return userManager.getUsersPermissonTree(userID);
	}


	/**
	 * 查询用户
	 * POST /users/simplequery
	 *
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param queryMap
	 */
	@RequestMapping(path="/users/simplequery",method={RequestMethod.POST})
	public List<UserRestBean> queryUserByProps(@RequestBody Map<String,Object> queryMap){

		return  userManager.simpleQueryUser(queryMap).stream()
				.map((e) -> new UserRestBean(e))
				.collect(Collectors.toCollection(ArrayList::new));

	}
}
