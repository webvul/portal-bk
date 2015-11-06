package com.kii.beehive.portal.helper;


import org.springframework.stereotype.Component;

@Component
public class AppChoice {

	private ThreadLocal<String> appIDLocal=new ThreadLocal<>();


	public void choiceAppID(String appID){

		appIDLocal.set(appID);

	}

	public void choicePortal(){

	}

	public void choiceMaster(){

	}

	public String getCurrAppID(){
		return appIDLocal.get();
	}
}
