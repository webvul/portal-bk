package com.kii.extension.ruleengine;

import javax.annotation.PostConstruct;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.demo.Message;

@Component
public class DemoRuleLoader {


	private KieSession kieSession;


	private KieServices  ks;

	@PostConstruct
	public void init(){


		ks = KieServices.Factory.get();

	}

	private KieSession getSession(){


		return kieSession;
	}

	public void initCondition(String rule){
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write("src/main/resources/com/kii/extension/ruleengine/demo.drl", rule);

		KieBuilder kb = ks.newKieBuilder(kfs);

		KieContainer kieContainer = ks.newKieContainer(kb.getKieModule().getReleaseId());
		kieSession = kieContainer.getKieBase().newKieSession();


		kb.buildAll();
	}

	public FactHandle addData(Message message){

		return getSession().insert(message);
	}

	public void fireCondition(){

		getSession().fireAllRules();

	}

}
