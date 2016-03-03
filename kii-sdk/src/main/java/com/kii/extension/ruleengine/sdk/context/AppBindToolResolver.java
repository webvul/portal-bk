package com.kii.extension.ruleengine.sdk.context;

import javax.annotation.PostConstruct;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.extension.ruleengine.sdk.entity.AppChoice;
import com.kii.extension.ruleengine.sdk.entity.AppInfo;

@Component
public class AppBindToolResolver {

	@Autowired
	private ApplicationContext context;

	//
	@Autowired
	private TokenBindToolResolver tokenResolver;

	private ThreadLocal<AppChoice> appChoiceLocal;

	private ThreadLocal<String> tokenDirectLocal = new ThreadLocal<>();



	private ThreadLocal<AppInfo> appInfoDirectly = new ThreadLocal<>();

	private ThreadLocal<LinkedList<OldInfos>>  oldInfosThreadLocal;


	private String[] getBeanNameArray() {

		return context.getBeanNamesForType(AppBindTool.class);
	}

	@PostConstruct
	public void initBindList() {
		appChoiceLocal = ThreadLocal.withInitial(() -> {


			AppChoice choice = new AppChoice();

			choice.setAppName(null);
			return choice;
		});

		oldInfosThreadLocal=ThreadLocal.withInitial(()->{
			LinkedList<OldInfos> infos=new LinkedList<>();
			return infos;
		});

	}


	private void offerInfo(OldInfos infos){

		if( (infos.oldAppInfo==null||infos.oldAppInfo.getAppID()==null)&&
				(infos.oldAppChoice==null||infos.oldAppChoice.getAppName()==null) ){
			return;
		}

		oldInfosThreadLocal.get().addFirst(infos);
	}
	

	
	
	private class OldInfos {

		 AppInfo oldAppInfo=null;

		 AppChoice oldAppChoice =new AppChoice();

		 String token;

	}

	public void setAppInfoDirectly(AppInfo appInfo,String token){

		setAppInfoDirectly(appInfo);
		tokenDirectLocal.set(token);

	}


	public void setAppInfoDirectly(String appName,String token) {
		setAppInfoDirectly(appName);
		tokenDirectLocal.set(token);
	}

	public void setAppInfoDirectly(String appName) {

		AppInfo appInfo=queryAppInfoByName(appName,null);

		setAppInfoDirectly(appInfo);
	}

	public void setAppInfoDirectly(AppInfo appInfo){

		OldInfos oldInfos=new OldInfos();

		oldInfos.oldAppInfo=appInfoDirectly.get();
		oldInfos.token=tokenDirectLocal.get();

		offerInfo(oldInfos);

		appInfoDirectly.set(appInfo);
		tokenDirectLocal.remove();
	}


	public void setAppChoice(AppChoice choice,String token){

		setAppChoice(choice);

		tokenDirectLocal.set(token);
	}


	public void setAppChoice(AppChoice choice){


		OldInfos oldInfos=new OldInfos();

		oldInfos.oldAppChoice =appChoiceLocal.get();
		oldInfos.oldAppInfo=appInfoDirectly.get();
		oldInfos.token=tokenDirectLocal.get();

		offerInfo(oldInfos);

		this.appInfoDirectly.remove();

		this.tokenDirectLocal.remove();

		this.appChoiceLocal.set(choice);
	}

	public void setAppName(String appName,String token){

		AppChoice choice=new AppChoice();

		choice.setAppName(appName);

		setAppChoice(choice,token);

	}

	public void setAppName(String appName){

		AppChoice choice=new AppChoice();

		choice.setAppName(appName);

		setAppChoice(choice);

	}

	public void setToken(String token) {
		this.tokenDirectLocal.set(token);
	}


	public String getToken(){


		String token=tokenDirectLocal.get();
		if(!StringUtils.isEmpty(token)){
			return token;
		}

		return tokenResolver.getToken();

	}

	public AppInfo getAppInfo(){

		AppInfo appInfo=appInfoDirectly.get();
		if(appInfo!=null){

		  	return appInfo;
		}

		AppChoice choice=appChoiceLocal.get();

		AppInfo newAppInfo= queryAppInfoByName(choice.getAppName(),choice.getBindName());

		if(newAppInfo!=null) {
			appInfoDirectly.set(newAppInfo);
		}
		return newAppInfo;
	}

	public void clean(){


		LinkedList<OldInfos> infosQueue=oldInfosThreadLocal.get();
		if(infosQueue.isEmpty()){
			return;
		}

		OldInfos infos=infosQueue.removeLast();

		appInfoDirectly.set(infos.oldAppInfo);
		appChoiceLocal.set(infos.oldAppChoice);

		tokenDirectLocal.set(infos.token);

	}



	private AppInfo queryAppInfoByName(String appName,String bindName){

		if(bindName!=null) {

			AppBindTool bindTool=context.getBean(bindName, AppBindTool.class);

			return bindTool.getAppInfo(appName);

		}

		for (String name : getBeanNameArray()) {
			AppBindTool bindTool = context.getBean(name, AppBindTool.class);

			AppInfo info = bindTool.getAppInfo(appName);
			if(info!=null){

				return info;
			}
		}

		return null;
	}

}
