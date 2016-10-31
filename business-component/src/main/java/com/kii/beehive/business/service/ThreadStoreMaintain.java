package com.kii.beehive.business.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.SafeThreadTool;

@Component
public class ThreadStoreMaintain {


	@Scheduled(fixedRate = 60*60*1000 )
	public void maintainThreadLocal(){

		SafeThreadTool.cleanOuttime();
	}

}
