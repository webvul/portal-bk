package com.kii.extension.ruleengine;

import javax.annotation.PostConstruct;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoRuleLoader {


	@Autowired
	private TrackingAgendaEventListener listener;

	private KieSession kieSession;


	private KieContainer kieContainer;

	private KieServices  ks;

	private KieFileSystem kfs;


	@PostConstruct
	public void init(){


		ks = KieServices.Factory.get();

	}

	private KieSession getSession(){


		return kieSession;
	}

	public void initCondition(String... rules){
		 kfs= ks.newKieFileSystem();


		int i=0;
		for(String rule:rules) {
			kfs.write("src/main/resources/rule"+i+".drl", rule);
			i++;
		}

		KieBuilder kb = ks.newKieBuilder(kfs);

		kb.buildAll();

		kieContainer= ks.newKieContainer(kb.getKieModule().getReleaseId());

		kieSession = kieContainer.getKieBase().newKieSession();
		kieSession.addEventListener(listener);

	}

	public void addCondition(String name,String rule){

		kfs.write("src/main/resources/"+name+".drl", rule);

		KieBuilder kb=ks.newKieBuilder(kfs);
		kb.buildAll();

		kieContainer.updateToVersion(kb.getKieModule().getReleaseId());
	}

	public FactHandle addData(Object message){

		return getSession().insert(message);
	}



	public void fireCondition(){

		getSession().fireAllRules();

	}
	
	public void removeData(FactHandle holder) {
		getSession().delete(holder);

	}
}
