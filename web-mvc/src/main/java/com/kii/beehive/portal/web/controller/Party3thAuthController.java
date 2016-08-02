package com.kii.beehive.portal.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.entity.AuthRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

@RestController
@RequestMapping(value = "/party3rd", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class Party3thAuthController {


	@Autowired
	private AuthManager authManager;


	@RequestMapping(value = "/getTokenByID", method = {RequestMethod.POST})
	public AuthRestBean getTokenByID(@RequestBody Map<String, Object> request) {

		String userID = (String) request.get("userID");

		if (CollectUtils.containsBlank(userID)) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","userID");
		}

		AuthUser user= authManager.getTokenByID(userID);

		return new AuthRestBean(user);
	}
}
