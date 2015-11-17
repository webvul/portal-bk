package com.kii.beehive.portal.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.helper.AppInfoService;

@Component
public class AppInfoManager {


	@Autowired
	private AppInfoService appDao;


	public void initAppInfos(String userName,String pwd,String master){

		appDao.initDataWithDevPortal(userName,pwd);


		appDao.setMasterSalve(master);


	}



}
