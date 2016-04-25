package com.kii.beehive.portal.helper;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kii.beehive.portal.entitys.PermissionTree;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/kii/beehive/portal/store/testPermissConfig.xml" })
public class TestPermissTree  {


	@Autowired
	private PermissionTreeService  service;

	@Test
	public void init(){

	}

	@Test
	public void testGetFullTree(){

		PermissionTree  tree=service.getFullPermissionTree();

		Map<String,PermissionTree> subModSet=tree.getSubmodule();

		assertTrue(subModSet.containsKey("tag"));


		assertEquals(2,subModSet.get("system").getSubmodule().size());

		PermissionTree  info=subModSet.get("system").getSubmodule().get("AppRegist");

		assertEquals("root.system.AppRegist",info.getFullPath());
		assertEquals("/sys/appRegist/*",info.getUrl());
		assertEquals("添加新kiiCloud app",info.getDisplayName());
		assertEquals("POST",info.getMethod());

	}


	@Test
	public void testVerify(){

		Set<String> set=new HashSet<>();

		set.add("Login");
		set.add("Logout");
		set.add("UpdateUser");
		set.add("system");



		PermissionTree ruleTree=service.getAcceptRulePermissionTree(set);

		assertTrue(ruleTree.doVerify("PATCH","/users/abc"));

		assertFalse(ruleTree.doVerify("GET","/users/abc"));

		assertTrue(ruleTree.doVerify("PATCH","/sys/abc"));

		assertFalse(ruleTree.doVerify("PATCH","/tags/abc"));


	}




	@Test
	public void testVerifyAll(){

		Set<String> set=new HashSet<>();

		set.add("root");


		PermissionTree ruleTree=service.getAcceptRulePermissionTree(set);

		assertTrue(ruleTree.doVerify("PATCH","/users/abc"));

		assertTrue(ruleTree.doVerify("GET","/users/abc"));

//		assertFalse(ruleTree.doVerify("GET","/sys/appRegist/*"));

		assertTrue(ruleTree.doVerify("POST","/sys/appRegist/*"));


		assertTrue(ruleTree.doVerify("POST","/sysappRegist/*"));

	}

	@Test
	public void testPathExpand(){

		Set<String> set=new HashSet<>();

		set.add("info");
		set.add("Login");
		set.add("Logout");
		set.add("UpdateUser");

		PermissionTree  tree=service.getAcceptRulePermissionTree(set);

		assertFalse(tree.getSubmodule().containsKey("tag"));

		assertTrue(tree.getSubmodule().containsKey("auth"));

		PermissionTree auth=tree.getSubmodule().get("auth");

		assertFalse(auth.getSubmodule().containsKey("ActiviteUser"));

		assertTrue(auth.getSubmodule().containsKey("Login"));

		assertEquals(2,auth.getSubmodule().size());

	}


	@Test
	public void testDenyVerify(){

		Set<String> set=new HashSet<>();

		set.add("system");
		set.add("Login");
		set.add("Logout");
		set.add("UpdateUser");

		PermissionTree  tree=service.getDenyRulePermissionTree(set);
//
		assertTrue(tree.doVerify("POST","/tags/abc"));

		assertFalse(tree.doVerify("POST","/oauth2/logout"));

//		PermissionTree auth=tree.getSubmodule().get("auth");

		assertTrue(tree.doVerify("POST","/oauth2/ActiviteUser"));
//
		assertFalse(tree.doVerify("POST","/oauth2/login"));
//
//		assertEquals(3,auth.getSubmodule().size());


		assertFalse(tree.doVerify("/sys/abc"));

		assertTrue(tree.doVerify("/info"));


	}


}
