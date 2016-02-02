package com.kii.extension.ruleengine.factory;

import javax.annotation.PostConstruct;

import java.io.IOException;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class KIEFactory {


	private KieServices  kieServices;

	private KieContainer kieContainer;

	@Autowired
	private ResourceLoader  loader;

	@PostConstruct
	public void init() throws IOException {

		kieServices = KieServices.Factory.get();

//		kieContainer = kieServices
//				.newKieBuilder(loader.getResource("classpath:kmodule.xml").getFile());

		kieContainer=kieServices.getKieClasspathContainer();
	}

	@Bean
	public KieSession getSession(){

		KieSession kSession = kieContainer.newKieSession("ksession-rules");

		return kSession;
	}
}


