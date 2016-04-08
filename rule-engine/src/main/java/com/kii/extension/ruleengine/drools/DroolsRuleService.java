package com.kii.extension.ruleengine.drools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.Charsets;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimedRuleExectionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DroolsRuleService {


	private final KieSession kieSession;

	private final KieContainer kieContainer;

	private final KieServices  ks;

	private final KieFileSystem kfs;


	private final Map<String,FactHandle> handleMap=new ConcurrentHashMap<>();


	public Map<String,Object>  getEngineEntitys(){


		Map<String,Object>  map=new HashMap<>();


		Collection<? extends Object> objs=getSession().getObjects();

		Map<String,Object>  entityMap=new HashMap<>();

		for(Object obj:objs){
			entityMap.put(getEntityKey(obj),obj);
		}

		map.put("entitys",entityMap);

		Map<String,Object> globalMap=new HashMap<>();
		for(String g:kieSession.getGlobals().getGlobalKeys()){
			globalMap.put(g,kieSession.getGlobal(g));
		}
		map.put("globals",globalMap);


		Map<String,String> drlMap=new HashMap<>();
		for(String drlPath:pathSet){
			String name=drlPath.substring(drlPath.lastIndexOf("/"),drlPath.length());
			if(name.startsWith("/comm")){
				continue;
			}
			String drlCtx=new String(kfs.read(drlPath), Charsets.UTF_8);
			drlMap.put(name,drlCtx);
		}
		map.put("drls",drlMap);

		return map;
	}


	private final Set<String> pathSet=new HashSet<>();

	public DroolsRuleService(boolean isStream,String...  rules){


		ks = KieServices.Factory.get();


		kfs= ks.newKieFileSystem();

		int i=0;
		for(String rule:rules) {

			String drlName="src/main/resources/comm_"+i+".drl";

			byte[]  bytes=kfs.read(drlName);

			kfs.write(drlName, rule);
			pathSet.add(drlName);
			i++;
		}

		KieBuilder kb = ks.newKieBuilder(kfs);

		kb.buildAll();

		kieContainer= ks.newKieContainer(kb.getKieModule().getReleaseId());

		KieBase kieBase=null;

		if(isStream) {
			KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
			config.setOption(EventProcessingOption.STREAM);
			kieBase=kieContainer.newKieBase(config);
		}else{
			kieBase = kieContainer.getKieBase();
		}


		KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
		ksconf.setOption( TimedRuleExectionOption.YES );

		kieSession = kieBase.newKieSession(ksconf,null);

		kieSession.addEventListener(new DebugAgendaEventListener());
		kieSession.addEventListener(new DebugRuleRuntimeEventListener());

		handleMap.clear();
	}



	private KieSession getSession(){


		return kieSession;
	}

	public  void initCondition(String... rules){


	}

	public void bindWithInstance(String name,Object instance){

		kieSession.getEnvironment().set(name,instance);
	}



	public void addCondition(String name,String rule){

		String drlName="src/main/resources/user_"+name+".drl";
		kfs.write(drlName, rule);

		pathSet.add(drlName);

		KieBuilder kb=ks.newKieBuilder(kfs);

		kb.buildAll();
		kieContainer.updateToVersion(kb.getKieModule().getReleaseId());

		getSession().getObjects().forEach((obj)->{

			FactHandle handle=getSession().getFactHandle(obj);

			handleMap.put(getEntityKey(obj),handle);

		});

	}


	public void removeCondition(String name){
		String path="src/main/resources/user_"+name+".drl";


		if(!pathSet.contains(path)) {
			throw new IllegalArgumentException("the deleted drl not found");
		}

		kfs.delete(path);
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


		handleMap.compute(getEntityKey(entity),(k,v)->{
			    if(v==null) {
					return getSession().insert(entity);
				}else{
					getSession().update(v,entity);
					return v;
				}
			}
		);

	}

	private String getEntityKey(Object entity) {
		return entity.getClass().getName()+":"+entity.hashCode();
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
	};

	
	public void removeData(Object obj) {

		FactHandle handler=handleMap.get(getEntityKey(obj));

		if(handler!=null) {
			getSession().delete(handler);
		}
	}
}
