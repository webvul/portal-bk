package com.kii.beehive.portal.manager;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.store.TestInit;
import com.kii.beehive.portal.store.entity.BeehiveUser;

public class TestUserManger  extends TestInit {

	@Autowired
	private BeehiveUserManager  userManager;

	@Autowired
	private AuthManager  authManager;

	@Autowired
	private KiiUserService kiiUserService;


	@Test
	public void createUser(){

		BeehiveUser user=new BeehiveUser();

		String name="testForUserManger";
		user.setUserName(name);
		user.setCompany("kiicloud");

		String token=userManager.addUser(user);

		String oneTimeToken=authManager.activite(name,token);

		authManager.initPassword(oneTimeToken,user.getUserName(),"qwerty");

		AuthRestBean  bean=authManager.login(name,"qwerty",false);

		String newToken=bean.getAccessToken();

		AuthRestBean newBean=authManager.validateUserToken(newToken);

		assertEquals(newBean.getUser().getId(),bean.getUser().getId());

	}

	@Test
	public void login(){


		BeehiveUser  user=userManager.getUserByID("Alfred");

		String pwd=user.getDefaultPassword();

		String token=kiiUserService.bindToUser(user,pwd);

		assertNotNull(token);

	}
}
