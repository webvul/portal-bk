package com.kii.beehive.portal.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.UserRuleSet;
import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class UserRuleDao  extends AbstractDataAccess<UserRuleSet> {



	@Cacheable(cacheNames="ttl_cache",key="'userRule'+#name")
	public UserRuleSet getRuleSetByName(String name){


		return super.getObjectByID(name);

	}

	@CacheEvict(cacheNames="ttl_cache",key="'userRule'+#ruleSet.ruleName")
	public void addUserRuleSet(UserRuleSet  ruleSet){

		super.addEntity(ruleSet,ruleSet.getId());

	}
	@Override
	protected Class<UserRuleSet> getTypeCls() {
		return UserRuleSet.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("UserRuleSet");
	}
}
