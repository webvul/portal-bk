package com.kii.beehive.portal.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.manager.TagThingManager;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;


@RestController
@RequestMapping(path = "/**", method = { RequestMethod.OPTIONS })
public class CORSController {

	@RequestMapping(path = "")
	public void doOptions(HttpServletRequest request, HttpServletResponse response) {

	}

}
