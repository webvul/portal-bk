package com.kii.extension.ruleengine;

import javax.annotation.PostConstruct;

import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.kii.extension.ruleengine.drools.CommandExec;
import com.kii.extension.ruleengine.drools.DroolsService;
import com.kii.extension.ruleengine.drools.entity.MatchResult;

@Configuration
public class RuleEngineFactory {



//
	@Autowired
	private CommandExec exec;

	@Autowired
	private ExtendFunAdapter  extFun;


	@Autowired
	protected ResourceLoader loader;


	private Consumer<List<MatchResult>>  consumer;

	@PostConstruct
	public void init(){

		consumer= (List<MatchResult> list)-> list.forEach(r->exec.doExecute(r.getTriggerID(),r));

	}



	private String getDrlContent(String fileName) {

		try {
			return StreamUtils.copyToString(loader.getResource("classpath:com/kii/extension/ruleengine/"+fileName+".drl").getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}



	@Bean(name="cloudDroolsService")
	public DroolsService getCloudService(){


		DroolsService droolsService= new DroolsService(consumer,false,
				getDrlContent("triggerComm"),
				getDrlContent("multipleSummary")
		);

		droolsService.bindWithInstance("exec",exec);

		droolsService.bindWithGlobal("ExtFun",extFun);


		return droolsService;

	}

//	@Bean(name="streamDroolsService")
//	public DroolsRuleService  getStreamService(){
//
//
//		DroolsRuleService droolsService= new DroolsRuleService(consumer,true,
//				getDrlContent("triggerComm"),
//				getDrlContent("multipleSummary")
//		)
//				;
//
//		droolsService.bindWithInstance("exec",exec);
//
//		return droolsService;
//
//	}



}
