package com.kii.extension.sdk.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.common.utils.SafeThreadLocal;
import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class ThingTokenBindTool implements TokenBindTool{



	@Autowired
	private ThingIFService service;

	@Autowired
	private AppBindToolResolver bindToolResolver;


	private SafeThreadLocal<Map<String,ThingInfo>> userLocal=SafeThreadLocal.withInitial(()->new ConcurrentHashMap<>());


	private void setResult(ThingInfo result){

		AppInfo appInfo =  bindToolResolver.getAppInfo();

		userLocal.get().put(appInfo.getAppID(), result);

	}

	public void bindThing(String thingID,String password){

		ThingInfo param=new ThingInfo();
		param.setThingID(thingID);
		param.setPassword(password);

		setResult(param);

	}

	public void bindVendorThing(String vendorThingID,String password){

		ThingInfo param=new ThingInfo();
		param.setVendorThingID(vendorThingID);
		param.setPassword(password);

		setResult(param);
	}

	void bindToken(String token){
		ThingInfo param=new ThingInfo();
		param.setToken(token);

		setResult(param);
	}


	@Override
	public String getToken() {

		AppInfo appInfo = bindToolResolver.getAppInfo();

		ThingInfo info=userLocal.get().get(appInfo.getAppID());
		if(info==null){
			return null;
		}

		if(StringUtils.isEmpty(info.getToken())){

			OnBoardingParam param=info.getRequest();
			if(param!=null){
				OnBoardingResult result=service.thingOnBoarding(param);
				info.setToken(result.getAccessToken());
				userLocal.get().put(appInfo.getAppID(),info);
			}else{
				return null;
			}
		}
		return info.getToken();
	}

	private static class ThingInfo{

		private String thingID;

		private String vendorThingID;

		private String token;

		private String password;


		public OnBoardingParam getRequest(){
			OnBoardingParam  param=new OnBoardingParam();
			if(StringUtils.isNotBlank(thingID)){
				param.setThingID(thingID);
			}else if(StringUtils.isNoneBlank(vendorThingID)){
				param.setVendorThingID(vendorThingID);
			}else{
				return null;
			}

			param.setThingPassword(password);

			return param;
		}

		public String getThingID() {
			return thingID;
		}

		public void setThingID(String thingID) {
			this.thingID = thingID;
		}

		public String getVendorThingID() {
			return vendorThingID;
		}

		public void setVendorThingID(String vendorThingID) {
			this.vendorThingID = vendorThingID;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
