package com.kii.beehive.portal.manager;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.business.service.KiiUserService;
import com.kii.beehive.portal.entitys.AuthRestBean;
import com.kii.beehive.portal.jdbc.entity.BeehiveJdbcUser;
import com.kii.beehive.portal.service.UserRuleDao;
import com.kii.beehive.portal.store.StoreServiceTestInit;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.service.UserService;

public class TestUserManger  extends StoreServiceTestInit {

	@Autowired
	private BeehiveUserManager  userManager;

	@Autowired
	private AuthManager  authManager;

	@Autowired
	private KiiUserService kiiUserService;




	@Autowired
	private UserService userService;


	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private UserRuleDao ruleDao;


	@Test
	public void createUser(){

		BeehiveJdbcUser user=new BeehiveJdbcUser();

		String name="testForUserManger";
		user.setUserName(name);
//		user.setCompany("kiicloud");

		Map<String,Object> maps=userManager.addUser(user,"");

		String oneTimeToken=authManager.activite(name, (String) maps.get("activityToken"));

		authManager.initPassword(oneTimeToken,user.getUserName(),"qwerty");

		AuthRestBean  bean=authManager.login(name,"qwerty");

		String newToken=bean.getAccessToken();

		AuthRestBean newBean=authManager.validateUserToken(newToken);

		assertEquals(newBean.getUser().getId(),bean.getUser().getId());

	}


	@Test
	public void resetPwd(){


//		BeehiveUser user=new BeehiveUser();

		String name="testForUserManger";
//		user.setUserName(name);
//		user.setCompany("kiicloud");


		AuthRestBean  rest=authManager.login(name,"qwerty");

		String userID=rest.getUser().getUserID();

		String token=authManager.resetPwd(userID);

		String oneTimeToken=authManager.activite(name, token);

		authManager.initPassword(oneTimeToken,name,"qwerty");

		AuthRestBean  bean=authManager.login(name,"qwerty");

		String newToken=bean.getAccessToken();

		AuthRestBean newBean=authManager.validateUserToken(newToken);

		assertEquals(newBean.getUser().getId(),bean.getUser().getId());

	}

	@Test
	public void login(){


		BeehiveJdbcUser user=userManager.getUserByID("Alfred");

		String pwd=user.getUserPassword();

		String token=kiiUserService.bindToUser(user,pwd);

		assertNotNull(token);

	}
}
