package com.kii.extension.sdk.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class AppBindToolResolver {

	@Autowired
	private ApplicationContext context;


	String[] bindNames;

	@PostConstruct
	public void initBindList(){

		bindNames=context.getBeanNamesForType(AppBindTool.class);

		if(bindNames.length==0){
			throw new IllegalArgumentException("not found app bind service");
		}


		setAppName(null);
	}

	private ThreadLocal<AppChoice>  appChoiceLocal=new ThreadLocal<>();

	public void setAppChoice(AppChoice choice){
		this.appChoiceLocal.set(choice);
	}

	public void setAppName(String appName){
		AppChoice choice=new AppChoice();

		choice.setAppName(appName);
		choice.setBindName(bindNames[0]);
		choice.setSupportDefault(true);

		appChoiceLocal.set(choice);

	}


	public AppInfo getAppInfo(){

		AppChoice choice=appChoiceLocal.get();

		if(choice.getBindName()!=null) {

			AppBindTool bindTool=context.getBean(choice.getBindName(), AppBindTool.class);

			return searchAppInfo(bindTool, choice);

		}

		for (String name : bindNames) {
			AppBindTool bindTool = context.getBean(name, AppBindTool.class);

			AppInfo info=searchAppInfo(bindTool,choice);
			if(info!=null){
				return info;
			}
		}

		return null;

	}


	private AppInfo searchAppInfo(AppBindTool bindTool,AppChoice choice){


		AppInfo info = null;

		if(choice.getAppName()!=null) {
			info = bindTool.getAppInfo(choice.getAppName());
		}
		if (info == null && choice.isSupportDefault()) {
			info = bindTool.getDefaultAppInfo();
		}

		return info;

	}
}
