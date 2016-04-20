package com.kii.beehive.portal.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.entitys.PermissionTree;
import com.kii.beehive.portal.service.BeehiveUserDao;
import com.kii.beehive.portal.service.UserRuleDao;
import com.kii.beehive.portal.store.entity.BeehiveUser;
import com.kii.beehive.portal.store.entity.UserRuleSet;

@Component
public class RuleSetService {

	@Autowired
	private BeehiveUserDao userDao;

	@Autowired
	private PermissionTreeService  permissionTreeService;

	@Autowired
	private UserRuleDao ruleDao;

	public PermissionTree getUserPermissionTree(String userID) {

		BeehiveUser user = userDao.getUserByID(userID);

		UserRuleSet ruleSet = ruleDao.getRuleSetByName(user.getRoleName());

		if(!ruleSet.getAcceptRuleSet().isEmpty()) {
			return  permissionTreeService.getAcceptRulePermissionTree(ruleSet.getAcceptRuleSet());
		}else if(!ruleSet.getDenyRuleSet().isEmpty()){
			return  permissionTreeService.getDenyRulePermissionTree(ruleSet.getDenyRuleSet());
		}else{
			throw new NullPointerException();
		}
	}

}
