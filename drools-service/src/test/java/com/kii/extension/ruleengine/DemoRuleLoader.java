package com.kii.extension.ruleengine;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.demo.Message;

@Component
public class DemoRuleLoader {


	@Autowired
	private KieSession  session;


	public void initCondition(){


	}

	public void addData(Message message){

		session.insert(message);
	}

	public void fireCondition(){

		session.fireAllRules();

	}



	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			KieSession kSession = kContainer.newKieSession("ksession-rules");


		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
