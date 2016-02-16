package com.kii.extension.ruleengine.drools;

import org.springframework.stereotype.Component;

@Component
public class CommandExec {

	public void doExecute(int triggerID){

		System.out.println("execute trigger " + triggerID);

	}

}
