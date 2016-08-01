package com.kii.beehive.portal.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.service.UserRuleDao;
import com.kii.beehive.portal.store.entity.UserRuleSet;

@Component
public class RuleSetService {

	@Autowired
	private BeehiveUserJdbcDao userDao;

	@Autowired
	private PermissionTreeService permissionTreeService;

	@Autowired
	private UserRuleDao ruleDao;


	@Value("${beehive.defaultUserRule.config.json:defaultUserRule}")
	private String defaultUserRule;

	@Autowired
	private ResourceLoader loader;

	@Autowired
	private ObjectMapper mapper;

	private String basePath="classpath:com/kii/beehive/portal/permission/config/";


	public void initRuleList()  {


		try {

			String fullPath=basePath+defaultUserRule+".json";

			String json = StreamUtils.copyToString(loader.getResource(fullPath).getInputStream(), Charsets.UTF_8);


			List<UserRuleSet> list = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, UserRuleSet.class));

			list.forEach(u -> {

				ruleDao.addUserRuleSet(u);
			});

		}catch(Exception e){

			throw new  IllegalArgumentException(e);
		}

	}

	public PermissionTree getUserPermissionTree(Long userID) {

		BeehiveJdbcUser user = userDao.getUserByID(userID);

		UserRuleSet ruleSet = ruleDao.getRuleSetByName(user.getRoleName());

		if (!ruleSet.getAcceptRuleSet().isEmpty()) {
			return permissionTreeService.getAcceptRulePermissionTree(ruleSet.getAcceptRuleSet());
		} else if (!ruleSet.getDenyRuleSet().isEmpty()) {
			return permissionTreeService.getDenyRulePermissionTree(ruleSet.getDenyRuleSet());
		} else {
			throw new NullPointerException();
		}
	}

}
