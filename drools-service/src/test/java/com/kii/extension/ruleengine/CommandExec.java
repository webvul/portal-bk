package com.kii.extension.ruleengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandExec {


	@Autowired
	private TrackingAgendaEventListener  listener;

	public void doExecute(int triggerID){

		if(listener!=null) {
			System.out.println("execute trigger " + triggerID);
		}else{
			throw new IllegalArgumentException();
		}
	}

}
