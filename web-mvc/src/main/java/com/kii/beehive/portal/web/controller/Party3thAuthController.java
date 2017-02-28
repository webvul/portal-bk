package com.kii.beehive.portal.web.controller;

import static com.kii.beehive.business.ruleengine.RemoteUrlStore.FIRE_BUSINESS_FUN;
import static com.kii.beehive.business.ruleengine.RemoteUrlStore.FIRE_THING_CMD;
import static com.kii.beehive.business.ruleengine.RemoteUrlStore.THIRD_PARTY_URL;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.ruleengine.ExecuteCommandManager;
import com.kii.beehive.business.ruleengine.entitys.BusinessFunctionParam;
import com.kii.beehive.business.ruleengine.entitys.ThingCommandExecuteParam;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.entitys.AuthUser;
import com.kii.beehive.portal.manager.AuthManager;
import com.kii.beehive.portal.web.entity.AuthRestBean;
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

@RestController
@RequestMapping(value = THIRD_PARTY_URL, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE})
public class Party3thAuthController {


	@Autowired
	private AuthManager authManager;
	@Autowired
	private ExecuteCommandManager executeManager;
	
	@RequestMapping(value = "/getTokenByID", method = {RequestMethod.POST})
	public AuthRestBean getTokenByID(@RequestBody Map<String, Object> request) {

		String userID = (String) request.get("userID");

		if (CollectUtils.containsBlank(userID)) {

			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","userID");
		}

		AuthUser user= authManager.getTokenByID(userID);

		return new AuthRestBean(user);
	}
	
	@RequestMapping(value = FIRE_THING_CMD, method = {RequestMethod.POST})
	public Map<Long,String> executeCommand(@RequestBody ThingCommandExecuteParam param) {
		
		
		Map<Long,String> results=executeManager.executeCommand(param);
		
		
		
		return results;
	}
	
	@RequestMapping(value = FIRE_BUSINESS_FUN, method = {RequestMethod.POST})
	public Map<String,Object> executeBusinessFunction(@RequestBody BusinessFunctionParam param) {
		
		return executeManager.doBusinessFunCall(param);
	}
}
