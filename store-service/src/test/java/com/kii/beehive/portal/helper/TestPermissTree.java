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


		assertEquals(4,subModSet.get("system").getSubmodule().size());

		PermissionTree  info=subModSet.get("system").getSubmodule().get("info");

		assertEquals("root.system.info",info.getFullPath());
		assertEquals("/info",info.getUrl());
		assertEquals("版本信息",info.getDisplayName());
		assertEquals("GET",info.getMethod());

	}


	@Test
	public void testVerify(){

		Set<String> set=new HashSet<>();

		set.add("info");
		set.add("Login");
		set.add("Logout");
		set.add("UpdateUser");



		PermissionTree ruleTree=service.getRulePermissionTree(set);

		assertTrue(ruleTree.doVerify("PATCH","/users/abc"));

		assertFalse(ruleTree.doVerify("GET","/users/abc"));

		assertTrue(ruleTree.doVerify("GET","/info"));

	}




	@Test
	public void testVerifyAll(){

		Set<String> set=new HashSet<>();

		set.add("root");


		PermissionTree ruleTree=service.getRulePermissionTree(set);

		assertTrue(ruleTree.doVerify("PATCH","/users/abc"));

		assertTrue(ruleTree.doVerify("GET","/users/abc"));

		assertTrue(ruleTree.doVerify("GET","/info"));

		assertTrue(ruleTree.doVerify("GET","/syncuser/abc"));


	}

	@Test
	public void testPathExpand(){

		Set<String> set=new HashSet<>();

		set.add("info");
		set.add("Login");
		set.add("Logout");
		set.add("UpdateUser");

		PermissionTree  tree=service.getRulePermissionTree(set);

		assertFalse(tree.getSubmodule().containsKey("tag"));

		assertTrue(tree.getSubmodule().containsKey("auth"));

		PermissionTree auth=tree.getSubmodule().get("auth");

		assertFalse(auth.getSubmodule().containsKey("ActiviteUser"));

		assertTrue(auth.getSubmodule().containsKey("Login"));

		assertEquals(2,auth.getSubmodule().size());

	}

}
