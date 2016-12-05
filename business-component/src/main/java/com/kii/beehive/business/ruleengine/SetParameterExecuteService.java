package com.kii.beehive.business.ruleengine;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.service.TriggerParameterDao;

@Component
public class SetParameterExecuteService {

	@Autowired
	private TriggerParameterDao  paramDao;


	

}
