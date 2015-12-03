package com.kii.extension.sdk.context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class AppBindToolResolver  {

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

		appChoiceLocal.set(choice);

	}

	public AppInfo getAppInfo(){

//		AppInfo directInfo=directAppInfoLocal.get();
//
//		if(directInfo!=null){
//			return directInfo;
//		}

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

	public void clean(){

		appChoiceLocal.remove();

//		directAppInfoLocal.remove();

	}


	private AppInfo searchAppInfo(AppBindTool bindTool,AppChoice choice){


		AppInfo info = null;

		if(choice.getAppName()!=null) {
			info = bindTool.getAppInfo(choice.getAppName());
		}
		this.setAppChoice(choice);

		return info;

	}
}
