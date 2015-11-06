package com.kii.extension.sdk.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.kii.extension.sdk.service.AdminTokenBindTool;
import com.kii.extension.sdk.service.AppBindTool;
import com.kii.extension.sdk.service.TokenBindTool;

public class LocalKiiCloudFactory {

	@Bean
	public AppBindTool getAppBindTool(){
		return new LocalPropertyBindTool();
	}


}
