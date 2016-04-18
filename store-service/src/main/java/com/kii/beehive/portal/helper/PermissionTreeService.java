package com.kii.beehive.portal.helper;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.entitys.PermissionEntry;
import com.kii.beehive.portal.service.RuleDetailDao;
import com.kii.beehive.portal.store.entity.RuleDetail;

@Component
public class PermissionTreeService {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ResourceLoader  loader;

	@Autowired
	private RuleDetailDao ruleDetailDao;

	@CacheEvict(cacheNames = CacheConfig.TTL_CACHE,key="ruleBindPermiss#rule.id")
	public void updateRuleDetail(RuleDetail  detail){



	}

	@Cacheable(cacheNames = CacheConfig.LONGLIVE_CACHE,key="full_permission_tree")
	public PermissionEntry getFullPermissionTree() throws IOException {

		String json= StreamUtils.copyToString(loader.getResource("com/kii/beehive/portal/permission/fullPermissionList.json").getInputStream(), Charsets.UTF_8);


		PermissionEntry  tree=mapper.readValue(json,PermissionEntry.class);

		return tree;
	}


	@Cacheable(cacheNames = CacheConfig.TTL_CACHE,key="ruleBindPermiss#rule.id")
	public PermissionEntry getRulePermissionTree(RuleDetail rule){


	}



}
