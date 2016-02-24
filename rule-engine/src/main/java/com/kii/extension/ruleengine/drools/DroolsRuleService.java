package com.kii.extension.ruleengine.drools;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DroolsRuleService {


	@Autowired
	private TrackingAgendaEventListener listener;

	private KieSession kieSession;


	private KieContainer kieContainer;

	private KieServices  ks;

	private KieFileSystem kfs;



	private Map<String,FactHandle> handleMap=new ConcurrentHashMap<>();



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
			kfs.write("src/main/resources/comm_"+i+".drl", rule);
			i++;
		}

		KieBuilder kb = ks.newKieBuilder(kfs);

		kb.buildAll();

		kieContainer= ks.newKieContainer(kb.getKieModule().getReleaseId());

		kieSession = kieContainer.getKieBase().newKieSession();
		kieSession.addEventListener(new DebugAgendaEventListener());
		kieSession.addEventListener(new DebugRuleRuntimeEventListener());

		handleMap.clear();
	}

	public void bindWithInstance(String name,Object instance){

		kieSession.getEnvironment().set(name,instance);
	}



	public void addCondition(String name,String rule){

		kfs.write("src/main/resources/user_"+name+".drl", rule);

		KieBuilder kb=ks.newKieBuilder(kfs);
		kb.buildAll();

		kieContainer.updateToVersion(kb.getKieModule().getReleaseId());

		getSession().getObjects().forEach((obj)->{

			FactHandle handle=getSession().getFactHandle(obj);

			handleMap.put(getEntityKey(obj),handle);

		});

	}


	public void removeCondition(String name){
		kfs.delete("src/main/resources/user_"+name+".drl");

		KieBuilder kb=ks.newKieBuilder(kfs);
		kb.buildAll();

		kieContainer.updateToVersion(kb.getKieModule().getReleaseId());
		getSession().getObjects().forEach((obj)->{

			FactHandle handle=getSession().getFactHandle(obj);

			handleMap.put(getEntityKey(obj),handle);

		});
	}



	public void setGlobal(String name,Object key){
		getSession().setGlobal(name,key);
	}

	public void addOrUpdateData(Object entity){


		FactHandle handler=handleMap.computeIfAbsent(getEntityKey(entity),(k)-> getSession().insert(entity));

		getSession().update(handler, entity);
	}

	private String getEntityKey(Object entity) {
		return entity.getClass().getName()+entity.hashCode();
	}

	public <T> List<T> doQuery(String queryName,Object... params){



		QueryResults results = getSession().getQueryResults( queryName,params );

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
	
	public void removeData(Object obj) {

		FactHandle handler=handleMap.get(getEntityKey(obj));

		if(handler!=null) {
			getSession().delete(handler);
		}
	}
}
