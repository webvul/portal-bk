package com.kii.beehive.portal.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.kii.beehive.business.service.sms.SmsSendService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.face.BeehiveFaceService;
import com.kii.beehive.portal.face.faceyitu.entitys.BeehiveFaceImageInput;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.manager.BeehiveUserManager;
import com.kii.beehive.portal.manager.UserAdminManager;
import com.kii.beehive.portal.service.UserCustomDataDao;
import com.kii.beehive.portal.store.entity.CustomData;
import com.kii.beehive.portal.web.entity.UserRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;


@RestController
@RequestMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserController {


	@Autowired
	private BeehiveUserManager userManager;

	@Autowired
	private UserAdminManager adminManager;

	@Autowired
	private AuthManager authManager;

	@Autowired
	private BeehiveFaceService beehiveFaceService;

	@Autowired
	private SmsSendService smsService;

	@Autowired
	private UserCustomDataDao dataDao;


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

		Map<String, Object> newUser = adminManager.addUser(user.getBeehiveUser());

		BeehiveJdbcUser userInfo = (BeehiveJdbcUser) newUser.get("user");

		String token = (String) newUser.get("activityToken");

		smsService.sendActivitySms(userInfo, token);

		Map<String, Object> result = new HashMap<>();
		result.put("activityToken", token);
		result.put("userID", userInfo.getUserID());

		return result;
	}


	@RequestMapping(path = "/usermanager/{userid}/resetpassword", method = {RequestMethod.POST})
	public Map<String, Object> resetPassword(@PathVariable("userid") String userID) {


		String token = authManager.resetPwd(userID);

		Map<String, Object> map = new HashMap<>();

		map.put("userID", userID);
		map.put("activityToken", token);

		smsService.sendResetPwdSms(userID, token);

		return map;

	}

	@RequestMapping(path = "/usermanager/{userid}/resetActivate", method = {RequestMethod.POST})
	public Map<String, Object> resetActivate(@PathVariable("userid") String userID) {


		Map<String, Object> newUser = adminManager.resetActivate(userID);

		BeehiveJdbcUser userInfo = (BeehiveJdbcUser) newUser.get("user");

		String token = (String) newUser.get("activityToken");

		smsService.sendActivitySms(userInfo, token);


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
	public Map<String, String> updateUser(@PathVariable("userID") String userID, @RequestBody BeehiveJdbcUser user) {


		BeehiveJdbcUser updateUser = new BeehiveJdbcUser();
		BeanUtils.copyProperties(user, updateUser, "kiiUserID", "activityToken", "userPassword", "roleName", "userID", "enable");

		adminManager.updateUser(updateUser, userID);


		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		return map;
	}


	@RequestMapping(value = "/usermanager/{userID}/enable", method = {RequestMethod.PUT})
	public Map<String, String> enableUser(@PathVariable("userID") String userID) {

		adminManager.updateUserSign(userID, true);


		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		map.put("enable", Boolean.toString(true));

		return map;
	}

	@RequestMapping(value = "/usermanager/{userID}/disable", method = {RequestMethod.PUT})
	public Map<String, String> disableUser(@PathVariable("userID") String userID) {

		adminManager.disableUser(userID);


		Map<String, String> map = new HashMap<>();
		map.put("userID", userID);
		map.put("enable", Boolean.toString(false));
		return map;
	}


	@RequestMapping(path = "/usermanager/{userID}", method = {RequestMethod.DELETE}, consumes = {MediaType.ALL_VALUE})
	public void hardDeleteUser(@PathVariable("userID") String userID) {

		BeehiveJdbcUser beehiveJdbcUser = userManager.getUserByUserID(userID);
		adminManager.removeUser(beehiveJdbcUser.getId());

	}

	/**
	 * 通过userID查询用户
	 * GET /usermanager/{userID}
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param userID
	 */
	@RequestMapping(value = "/usermanager/{userID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public UserRestBean getUser(@PathVariable("userID") String userID) {

		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(adminManager.getUserByUserID(userID));

		return bean;
	}

	/**
	 * 用户修改密码
	 * POST /oauth2/changepassword
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/users/changepassword", method = {RequestMethod.POST})
	public void changePassword(@RequestBody Map<String, Object> request) {

		String oldPassword = (String) request.get("oldPassword");
		String newPassword = (String) request.get("newPassword");

		if (Strings.isBlank(oldPassword)) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "oldPassword");
		}

		AuthController.veifyPwd(newPassword);


		authManager.changePassword(oldPassword, newPassword);

	}


	@RequestMapping(value = "/users/me", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public UserRestBean getUser() {

		Long userID = AuthInfoStore.getUserID();
		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByIDDirectly(userID));

		return bean;
	}

	@RequestMapping(value = "/users/me", method = {RequestMethod.PATCH})
	public Map<String, String> updateUser(@RequestBody BeehiveJdbcUser user) {


		BeehiveJdbcUser updateUser = new BeehiveJdbcUser();
		BeanUtils.copyProperties(user, updateUser, "kiiUserID", "activityToken", "userPassword", "roleName", "userID", "enable");

		BeehiveJdbcUser beehiveJdbcUser = userManager.getUserByIDDirectly(AuthInfoStore.getUserID());
		userManager.updateUser(updateUser, beehiveJdbcUser.getUserID());


		Map<String, String> map = new HashMap<>();
		map.put("userID", beehiveJdbcUser.getUserID());
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
	@RequestMapping(value = "/users/{userID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public UserRestBean getUserByID(@PathVariable("userID") String userID) {

		UserRestBean bean = new UserRestBean();
		bean.setBeehiveUser(userManager.getUserByUserID(userID));

		return bean;
	}


	@RequestMapping(value = "/users/permissionTree", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public PermissionTree getUserPermissTree() {


		return userManager.getUsersPermissonTree(AuthInfoStore.getUserID());
	}


	/**
	 * 查询用户
	 * POST /users/simplequery
	 * <p>
	 * refer to doc "Beehive API - User API" for request/response details
	 *
	 * @param queryMap
	 */
	@RequestMapping(path = "/users/simplequery", method = {RequestMethod.POST})
	public List<UserRestBean> queryUserByProps(@RequestBody Map<String, Object> queryMap) {

		return userManager.simpleQueryUser(queryMap).stream()
				.map((e) -> new UserRestBean(e))
				.collect(Collectors.toCollection(ArrayList::new));

	}

	@RequestMapping(path = "/users/me", method = {RequestMethod.DELETE}, consumes = {MediaType.ALL_VALUE})
	public void deleteUser() {


		userManager.removeUser(AuthInfoStore.getUserID());

	}

	@RequestMapping(path = "/users/me/customData/{name}", method = {RequestMethod.GET},
			consumes = {MediaType.ALL_VALUE})
	public CustomData getCustomData(@PathVariable(value = "name") String name) {
		BeehiveJdbcUser user = userManager.getUserByIDDirectly(AuthInfoStore.getUserID());
		return dataDao.getUserData(name, user.getUserID());

	}


	@RequestMapping(path = "/users/me/customData/{name}", method = {RequestMethod.PUT})
	public void setCustomData(@PathVariable(value = "name") String name, @RequestBody(required = false) CustomData data) {

		if (data == null) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "custom data content");
		}
		BeehiveJdbcUser user = userManager.getUserByIDDirectly(AuthInfoStore.getUserID());

//		CustomData customData=new CustomData();
//		customData.setData(data);
		dataDao.setUserData(data, name, user.getUserID());

	}




	@RequestMapping(value = "/user/photo/file", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public
	@ResponseBody
	UserRestBean uploadFacePhotoFile(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "photo") CommonsMultipartFile photo) throws IOException {

		if (photo == null) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "photo");
		}

		File photoFile = beehiveFaceService.createUserFaceTempFile(userId, photo.getBytes());

		BeehiveJdbcUser user = beehiveFaceService.updateUserWithFace(userId, photoFile);
		UserRestBean bean = new UserRestBean(user);
		return bean;
	}




	@RequestMapping(value = "/user/photo", method = RequestMethod.POST)
	public
	@ResponseBody
	UserRestBean uploadFacePhoto(@RequestBody BeehiveFaceImageInput faceImage) throws IOException {

		if (StringUtils.isEmpty(faceImage.getUserId())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "userId");
		}
		if (StringUtils.isEmpty(faceImage.getPictureImageContentBase64())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "field", "pictureImageContentBase64");
		}

		byte[] bytes = Base64.decodeBase64(faceImage.getPictureImageContentBase64());
		File photoFile = beehiveFaceService.createUserFaceTempFile(faceImage.getUserId(), bytes);

		BeehiveJdbcUser user = beehiveFaceService.updateUserWithFace(faceImage.getUserId(), photoFile);
		UserRestBean bean = new UserRestBean(user);
		return bean;
	}

	@RequestMapping(value = "/users/face/{faceUserID}", method = {RequestMethod.GET}, consumes = {MediaType.ALL_VALUE})
	public UserRestBean getUserByFaceUserID(@PathVariable("faceUserID") String faceUserID) {

		BeehiveJdbcUser user = userManager.getUserByFaceUserID(faceUserID);
		UserRestBean bean = new UserRestBean(user);

		return bean;
	}

}
