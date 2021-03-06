package com.kii.extension.sdk.context;

import javax.annotation.PostConstruct;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.portal.common.utils.SafeThreadLocal;
import com.kii.beehive.portal.common.utils.SafeThreadTool;
import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.AppInfo;

@Component
public class AppBindToolResolver {

	@Autowired
	private ApplicationContext context;

	//
	@Autowired
	private TokenBindToolResolver tokenResolver;

	private SafeThreadLocal<AppChoice> appChoiceLocal=SafeThreadLocal.getInstance();

	private SafeThreadLocal<String> tokenDirectLocal = SafeThreadLocal.getInstance();
	
//	private SafeThreadLocal<AppInfo> appInfoDirectly = SafeThreadLocal.getInstance();

	private SafeThreadLocal<LinkedList<OldInfos>>  oldInfosThreadLocal=SafeThreadLocal.getInstance();


	private String[] getBeanNameArray() {

		return context.getBeanNamesForType(AppBindTool.class);
	}

	@PostConstruct
	public void initBindList() {
		appChoiceLocal = SafeThreadLocal.withInitial(() -> {

			AppChoice choice = new AppChoice();
			choice.setAppName(null);
			return choice;
		});

		oldInfosThreadLocal=SafeThreadLocal.withInitial(()->{
			LinkedList<OldInfos> infos=new LinkedList<>();
			return infos;
		});

	}


	private void offerInfo(OldInfos infos){

		if( (infos.oldAppChoice==null||infos.oldAppChoice.getAppName()==null) ){
			return;
		}

		oldInfosThreadLocal.get().addFirst(infos);
	}
	
	public void refreshToken() {
		
		tokenResolver.refreshToken();
	}
	
	
	private class OldInfos {

//		 AppInfo oldAppInfo=null;

		 AppChoice oldAppChoice =new AppChoice();

		 String token;

	}
//
	public void pushAppNameDirectly(String appName,String token) {

		AppChoice choice=new AppChoice();
		choice.setAppName(appName);

		pushAppChoice(choice);
		
		tokenDirectLocal.set(token);
	}
	
	public void pushAppNameDirectly(String appName) {
		
		AppChoice choice=new AppChoice();
		choice.setAppName(appName);
		choice.setTokenBindName(TokenBindTool.BindType.admin.name());
		
		pushAppChoice(choice);
		
	}
//
//	public void pushAppNameDirectly(String appName) {
//
//
//		AppInfo appInfo=queryAppInfoByName(appName,null);
//
//		pushAppInfoDirectly(appInfo);
//	}

//	public void pushAppInfoDirectly(AppInfo appInfo){
//
//
//		OldInfos oldInfos=new OldInfos();
//
////		oldInfos.oldAppInfo=appInfoDirectly.get();
//		oldInfos.token=tokenDirectLocal.get();
//
//		offerInfo(oldInfos);
//
////		appInfoDirectly.set(appInfo);
//		tokenDirectLocal.remove();
//	}

	public void pushAppChoice(AppChoice choice){

		OldInfos oldInfos=new OldInfos();

		oldInfos.oldAppChoice =appChoiceLocal.get();
//		oldInfos.oldAppInfo=appInfoDirectly.get();
		oldInfos.token=tokenDirectLocal.get();

		offerInfo(oldInfos);

//		this.appInfoDirectly.remove();

		this.tokenDirectLocal.remove();

		this.appChoiceLocal.set(choice);
	}


	public void pushAppName(String appName){


		AppChoice choice=new AppChoice();

		choice.setAppName(appName);

		pushAppChoice(choice);

	}


	public void setToken(String token) {


		this.tokenDirectLocal.set(token);
	}


	public String getToken(){

		String token=tokenDirectLocal.get();
		if(!StringUtils.isEmpty(token)){

			return token;
		}
//		if(appInfoDirectly.get()!=null){
//			tokenResolver.bindByType(TokenBindTool.BindType.admin.name());
//
//		}else {
		tokenResolver.bindByType(appChoiceLocal.get().getTokenBindName());
		
//		}
		token = tokenResolver.getToken();

		return token;

	}


	public AppInfo getAppInfo(){

//		AppInfo appInfo=appInfoDirectly.get();
//		if(appInfo!=null){
//
//		  	return appInfo;
//		}

		AppChoice choice=appChoiceLocal.get();

		AppInfo newAppInfo= queryAppInfoByName(choice.getAppName(),choice.getBindName());

//		appInfoDirectly.set(newAppInfo);

		return newAppInfo;
	}

	public void pop(){


		LinkedList<OldInfos> infosQueue=oldInfosThreadLocal.get();
		if(infosQueue.isEmpty()){
			return;
		}

		OldInfos infos=infosQueue.removeLast();
		if(infosQueue.isEmpty()){
			oldInfosThreadLocal.remove();
		}

//		appInfoDirectly.set(infos.oldAppInfo);
		appChoiceLocal.set(infos.oldAppChoice);
		tokenDirectLocal.set(infos.token);

	}

	public void clearAll(){

		SafeThreadTool.removeLocalInfo();

	}



	private AppInfo queryAppInfoByName(String appName,String bindName){


		if(bindName!=null) {

			AppBindTool bindTool=context.getBean(bindName, AppBindTool.class);
			AppInfo appInfo = bindTool.getAppInfo(appName);

			return appInfo;

		}

		for (String name : getBeanNameArray()) {
			AppBindTool bindTool = context.getBean(name, AppBindTool.class);

			AppInfo info = bindTool.getAppInfo(appName);
			if(info!=null){

				return info;
			}
		}

		throw new NullPointerException("app "+appName+" not found. bind name:"+bindName);
	}

}
