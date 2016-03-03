package com.kii.extension.ruleengine;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.drools.CommandExec;
import com.kii.extension.ruleengine.drools.DroolsRuleService;


@Configuration
public class MockEventBus  {


	@Autowired
	private CommandExec exec;


	@Autowired
	protected ResourceLoader loader;



	private String getDrlContent(String fileName) {

		try {
			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}


	@Bean(name="cloudDroolsService")
	public DroolsRuleService getCloudService(){

		DroolsRuleService droolsService= new DroolsRuleService(false,
				getDrlContent("triggerComm"),
				getDrlContent("groupPolicy"),
				getDrlContent("summaryCompute"));

		droolsService.bindWithInstance("exec",exec);

		return droolsService;

	}



}
