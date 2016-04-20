package com.kii.beehive.portal.helper;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import com.kii.beehive.portal.config.CacheConfig;
import com.kii.beehive.portal.entitys.PatternSet;
import com.kii.beehive.portal.entitys.PermissionTree;

@Component
public class PermissionTreeService {


	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ResourceLoader  loader;



	private PermissionTree permissionEntry;

	private Map<String,String> fullPathMap=new HashMap<>();

//	private TreeMap<String,PermissionTree> indexMap=new TreeMap<>();

	@Value("${com.kii.beehive.portal.permission.config.file}")
	private String configName;

	@PostConstruct
	public void init() throws IOException {

		String json= StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/portal/permission/config/"+configName+".json").getInputStream(), Charsets.UTF_8);


		permissionEntry=mapper.readValue(json,PermissionTree.class);

		fullPathMap=permissionEntry.fillFullPath();

	}

	@Cacheable(cacheNames = CacheConfig.LONGLIVE_CACHE,key="full_permission_tree")
	public PermissionTree getFullPermissionTree()  {

		return permissionEntry.clone();
	}



	@Cacheable(cacheNames = CacheConfig.TTL_CACHE,key="'acceptPermissionRule'+#ruleSet.toString()" )
	public PermissionTree getAcceptRulePermissionTree(Set<String> ruleSet){

		PermissionTree newTree=permissionEntry.clone();

		Set<String> set = ruleSet.stream().map((k) -> fullPathMap.get(k)).collect(Collectors.toSet());

		PatternSet pattern = new PatternSet(set);

		newTree.doAcceptFilter(pattern);

		return newTree;

	}

	@Cacheable(cacheNames = CacheConfig.TTL_CACHE,key="'denyPermissionRule'+#ruleSet.toString()" )
	public PermissionTree getDenyRulePermissionTree(Set<String> ruleSet){

		PermissionTree newTree=permissionEntry.clone();

		Set<String> set = ruleSet.stream().map((k) -> fullPathMap.get(k)).collect(Collectors.toSet());

		PatternSet pattern = new PatternSet(set);

		boolean sign=newTree.doDenyFilter(pattern);

		if(sign){
			return null;
		}else {
			return newTree;
		}

	}





}
