package com.kii.extension.ruleengine.schedule;

import org.springframework.stereotype.Component;

@Component
public class BusinessBean {

	public void output(String triggerID){
		System.out.println(triggerID);
	}


}
