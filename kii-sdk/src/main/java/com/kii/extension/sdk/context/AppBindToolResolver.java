package com.kii.extension.sdk.context;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class AppBindToolResolver {

	@Autowired
	private ApplicationContext context;


	private ThreadLocal<AppChoice>  appChoiceLocal;

	private String[] getBeanNameArray(){

		return context.getBeanNamesForType(AppBindTool.class);
	}

	@PostConstruct
	public void initBindList(){
		appChoiceLocal=ThreadLocal.withInitial(()->{


			AppChoice choice=new AppChoice();

			choice.setAppName(null);
			choice.setBindName(getBeanNameArray()[0]);
			choice.setSupportDefault(true);
			return choice;
		});

	}

	public void setAppChoice(AppChoice choice){

		this.appChoiceLocal.set(choice);
	}

	public void setAppName(String appName){
		this.setAppName(appName, true);
	}

	public void setAppName(String appName,boolean usingDefault){
		AppChoice choice=new AppChoice();

		choice.setAppName(appName);
		choice.setSupportDefault(usingDefault);

		appChoiceLocal.set(choice);

	}

	public AppInfo getAppInfoByName(String appName){

		for (String bean : getBeanNameArray()) {
			AppBindTool bindTool = context.getBean(bean, AppBindTool.class);

			AppInfo info = bindTool.getAppInfo(appName);

			if(info!=null){
				return info;
			}

		}
		return null;

	}

	public AppInfo getAppInfo(){

		AppChoice choice=appChoiceLocal.get();

		if(choice.getBindName()!=null) {

			AppBindTool bindTool=context.getBean(choice.getBindName(), AppBindTool.class);

			return searchAppInfo(bindTool, choice);

		}

		for (String name : getBeanNameArray()) {
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
