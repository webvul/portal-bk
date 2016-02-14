package com.kii.extension.ruleengine;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
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

	@Autowired
	private CommandExec  exec;


	@PostConstruct
	public void init(){


		ks = KieServices.Factory.get();
//
//
//		Environment  env=ks.newEnvironment();
//
//		env.set("exec",exec);



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

		kieSession.getEnvironment().set("exec",exec);

//		kieSession.addEventListener(listener);

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

	public <T> List<T> doQuery(String queryName){

		QueryResults results = getSession().getQueryResults( queryName );

		List<T>  list=new ArrayList<>();

		for ( QueryResultsRow row : results ) {
			T result = ( T ) row.get( "results" );
			list.add(result);
		}

		return list;
	}


	public void fireCondition(){

		getSession().fireAllRules();

	}
	
	public void removeData(FactHandle holder) {
		getSession().delete(holder);

	}
}
