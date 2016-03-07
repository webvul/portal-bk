package com.kii.extension.ruleengine;

import javax.annotation.PostConstruct;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.stereotype.Component;

@Component
public class StatelessRuleExecute {



	private KieServices  ks;

	private StatelessKieSession kieSession;

	@PostConstruct
	public void init(){

		ks = KieServices.Factory.get();

	}


	public void initCondition(String... rules){
		KieFileSystem kfs = ks.newKieFileSystem();

		for(String rule:rules) {
			kfs.write("src/main/resources/stateless.drl", rule);
		}

		KieBuilder kb = ks.newKieBuilder(kfs);

		KieContainer kieContainer = ks.newKieContainer(kb.getKieModule().getReleaseId());

		kieSession = kieContainer.getKieBase().newStatelessKieSession();

		kb.buildAll();
	}

	public <T>  T doExecute(T  input){

		kieSession.execute(input);

		return input;
	}


}
