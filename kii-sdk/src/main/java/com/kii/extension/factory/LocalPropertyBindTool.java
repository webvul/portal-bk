package com.kii.extension.factory;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.kii.extension.sdk.entity.AppInfo;
import com.kii.extension.sdk.entity.SiteType;
import com.kii.extension.sdk.context.AppBindTool;

public class LocalPropertyBindTool implements AppBindTool {


	@Autowired
	private ResourceLoader loader;


	private Map<String,AppInfo> appInfoMap;


	private String propName;

	public void setPropFileName(String propName){
		this.propName=propName;
	}


	private String defaultApp;

	public void setDefaultApp(String defaultApp){
		this.defaultApp=defaultApp;
	}

	@PostConstruct
	public void initAppInfo() {

		Properties prop = new Properties();

		try {
			prop.load(loader.getResource(propName).getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		Map<String,AppInfo> map=new HashMap<>();

		prop.stringPropertyNames().stream().forEach(key -> {

			if(!key.startsWith("kiicloud.")){
				return;
			}

			int idx = key.indexOf(".");

			int idx2= key.indexOf(".",idx+1);

			String appName = key.substring(idx+1, idx2).trim().toLowerCase();
			String type = key.substring(idx2 + 1).trim().toLowerCase();

			AppInfo info = map.getOrDefault(appName, new AppInfo());

			String val = prop.getProperty(key).trim().toLowerCase();
			if (type.equals("appkey")) {

				info.setAppKey(val);

			} else if (type.equals("appid")) {
				info.setAppID(val);
			} else if (type.equals("clientid")) {
				info.setClientID(val);
			} else if (type.equals("clientsecret")) {
				info.setClientSecret(val);
			} else if (type.equals("site")) {
				info.setSite(SiteType.valueOf(val.toUpperCase()));
			} else if (type.equals("siteurl")) {
				info.setSiteUrl(val);
			}

			map.put(appName, info);
		});

		appInfoMap= Collections.unmodifiableMap(map);

	}


	@Override
	public AppInfo getAppInfo(String appName) {
		return appInfoMap.get(appName);
	}

	@Override
	public AppInfo getDefaultAppInfo() {
		if(defaultApp!=null) {
			return appInfoMap.get(defaultApp);
		}else{
			return null;
		}
	}
}
