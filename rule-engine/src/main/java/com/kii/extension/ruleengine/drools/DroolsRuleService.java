package com.kii.extension.ruleengine.drools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

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
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.conf.TimedRuleExectionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kii.extension.ruleengine.drools.entity.CanUpdate;
import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.ExternalCollect;
import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.RuntimeEntry;
import com.kii.extension.ruleengine.drools.entity.ScheduleFire;
import com.kii.extension.ruleengine.drools.entity.ThingStatusInRule;
import com.kii.extension.ruleengine.drools.entity.WithTrigger;

@Component
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DroolsRuleService {

	private Logger log= LoggerFactory.getLogger(DroolsRuleService.class);


	private final KieSession kieSession;

	private final KieContainer kieContainer;

	private final KieServices  ks;

	private final KieFileSystem kfs;

	private final Map<String,FactHandle> handleMap=new ConcurrentHashMap<>();
	private final Set<String> pathSet=new HashSet<>();

	private final FactHandle  currThingHandler;
	private final FactHandle  externalHandler;


	private final CurrThing currThing=new CurrThing();

	private final ExternalCollect  external=new ExternalCollect();


	private final Consumer<List<MatchResult>> consumer;



	public void enterInit(){

		this.currThing.setStatus(CurrThing.Status.inInit);
		this.currThing.setCurrThing(CurrThing.NONE);

		kieSession.update(currThingHandler, currThing);

		kieSession.fireAllRules();
	}

	public void leaveInit(){


		this.currThing.setStatus(CurrThing.Status.inIdle);
		this.currThing.setCurrThing(CurrThing.NONE);

		kieSession.update(currThingHandler, currThing);

		kieSession.fireAllRules();

	}

	public void toIdle(){
		settingCurrThing(CurrThing.NONE, CurrThing.Status.inIdle);
	}




	public void inThing(String thingID){

		settingCurrThing(thingID, CurrThing.Status.inThing);
	}

	public void inExt(String extName){

		settingCurrThing(CurrThing.NONE, CurrThing.Status.inExt);
	}


	public  void updateScheduleData(ScheduleFire fire){

		synchronized (currThing) {

			CurrThing.Status  oldStatus=currThing.getStatus();
			if(oldStatus==CurrThing.Status.inThing) {

				this.currThing.setStatus(CurrThing.Status.inIdle);
				this.currThing.setCurrThing(CurrThing.NONE);
				kieSession.update(currThingHandler, currThing);

			}

			FactHandle  handler=kieSession.insert(fire);
			kieSession.fireAllRules();
			kieSession.delete(handler);
			kieSession.fireAllRules();
		}
	}

	private void settingCurrThing(String thingID,CurrThing.Status  status){

		synchronized (currThing) {

			CurrThing.Status  oldStatus=currThing.getStatus();

			if(oldStatus==CurrThing.Status.inInit){
				kieSession.fireAllRules();

				return;
			}

			this.currThing.setStatus(status);
			this.currThing.setCurrThing(thingID);

			kieSession.update(currThingHandler, currThing);

			kieSession.fireAllRules();
			if(status== CurrThing.Status.inThing||status== CurrThing.Status.inExt) {
				List<MatchResult> lists = doQuery("get Match Result by TriggerID");
				consumer.accept(lists);
			}


			ExternalValues entity=new ExternalValues("sys");
			entity.addValue("currThing",currThing);
			external.updateEntity(entity);

			kieSession.update(externalHandler,external);
			kieSession.fireAllRules();
		}
	}


	public Map<String,Object>  getEngineEntitys(String triggerID){


		Map<String,Object>  map=new HashMap<>();


		Collection<? extends Object> objs=kieSession.getObjects((ObjectFilter) object -> {
			if(triggerID==null){
				return true;
			}
			if(object instanceof WithTrigger){
					return ((WithTrigger)object).getTriggerID().equals(triggerID);
			}
			if(object instanceof ThingStatusInRule){
					return false;
			}
			return true;
		});

		Map<String,Object>  entityMap=new HashMap<>();

		for(Object obj:objs){
			entityMap.put(this.getEntityKey(obj),obj);
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
			if(triggerID!=null&&!name.contains(triggerID)){
				continue;
			}
			String drlCtx=new String(kfs.read(drlPath), Charsets.UTF_8);
			drlMap.put(name,drlCtx);
		}
		map.put("drls",drlMap);

		return map;
	}



	public DroolsRuleService(Consumer<List<MatchResult>>  consumer , boolean isStream,String...  rules){

		this.consumer=consumer;

		ks = KieServices.Factory.get();


		kfs= ks.newKieFileSystem();

		int i=0;
		for(String rule:rules) {

			String drlName="src/main/resources/comm_"+i+".drl";

//			byte[]  bytes=kfs.read(drlName);

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


		if(!isStream) {
			kieSession.addEventListener(new DebugAgendaEventListener());
			kieSession.addEventListener(new DebugRuleRuntimeEventListener());
		}
		currThingHandler=kieSession.insert(currThing);

		externalHandler=kieSession.insert(external);

		handleMap.clear();
	}


	public void bindWithGlobal(String name,Object instance){
		kieSession.setGlobal(name,instance);
	}

	public void bindWithInstance(String name,Object instance){

		kieSession.getEnvironment().set(name,instance);
	}



	public  void addCondition(String name,String rule){


		log.debug("add rule:"+rule);

		String drlName="src/main/resources/user_"+name+".drl";


		kfs.write(drlName, rule);

		pathSet.add(drlName);

		KieBuilder kb=ks.newKieBuilder(kfs);

		kb.buildAll();

		kieContainer.updateToVersion(kb.getKieModule().getReleaseId());

		handleMap.clear();
		kieSession.getFactHandles().forEach((handle)->{

			Object obj=kieSession.getObject(handle);

			handleMap.put(getEntityKey(obj),handle);

		});


		toIdle();

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
		pathSet.remove(path);
		kieSession.getObjects().forEach((obj)->{

			FactHandle handle=kieSession.getFactHandle(obj);

			handleMap.put(getEntityKey(obj),handle);

		});

		toIdle();


	}




	public void addOrUpdateExternal(ExternalValues entity){


		external.updateEntity(entity);

		kieSession.update(externalHandler,external);

		inExt(entity.getName());

	}


	public void addOrUpdateData(Object entity,boolean replace){


		handleMap.compute(getEntityKey(entity),(k,v)->{
			    if(v==null) {
					return kieSession.insert(entity);
				}else{
					if(!replace&&entity instanceof CanUpdate){
						CanUpdate  oldEntity=(CanUpdate)kieSession.getObject(v);
						oldEntity.update(entity);
						kieSession.update(v,oldEntity);
					}else {
						kieSession.update(v, entity);
					}
					return v;
				}
			}
		);

	}



	private String getEntityKey(Object entity) {
		if(entity instanceof RuntimeEntry) {

			return entity.getClass().getName()+":"+((RuntimeEntry)entity).getID();
		}else{
			return entity.getClass().getName()+":"+entity.hashCode();
		}
	}

	private <T extends MatchResult> List<T> doQuery(String queryName, Object... params){



		QueryResults results = kieSession.getQueryResults( queryName,params );

		List<T>  list=new ArrayList<>();

		for ( QueryResultsRow row : results ) {
			T result = ( T ) row.get( "results" );
			list.add(result);
		}

		return list;
	}


	
	public void removeData(Object obj) {
		String entityKey = getEntityKey(obj);
		FactHandle handler=handleMap.get(entityKey);
		if(handler!=null) {
			kieSession.delete(handler);
			handleMap.remove(entityKey);
		}
	}


	public void removeFact(Function<Object,Boolean> function) {

		Collection<FactHandle>  handles=kieSession.getFactHandles(function::apply);

		handles.forEach((handle)->{
			kieSession.delete(handle);
		});

	}
}
