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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.sysmonitor.SysMonitorMsg;
import com.kii.beehive.portal.sysmonitor.SysMonitorQueue;
import com.kii.extension.ruleengine.drools.entity.AtomicCurrThing;
import com.kii.extension.ruleengine.drools.entity.BusinessObjInRule;
import com.kii.extension.ruleengine.drools.entity.CanUpdate;
import com.kii.extension.ruleengine.drools.entity.CurrThing;
import com.kii.extension.ruleengine.drools.entity.ExternalCollect;
import com.kii.extension.ruleengine.drools.entity.ExternalValues;
import com.kii.extension.ruleengine.drools.entity.MatchResult;
import com.kii.extension.ruleengine.drools.entity.MultiplesValueMap;
import com.kii.extension.ruleengine.drools.entity.RuntimeEntry;
import com.kii.extension.ruleengine.drools.entity.WithTrigger;

@Component
@Scope(scopeName= ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DroolsService {

	private Logger log= LoggerFactory.getLogger(DroolsService.class);


	@Value("${spring.profile:test}")
	private String profile;
	
	
	private final KieSession kieSession;

	private final KieContainer kieContainer;

	private final KieServices  ks;

	private final KieFileSystem kfs;

	private final Map<String,Object> handleMap=new ConcurrentHashMap<>();
	
	private final Set<String> pathSet=new HashSet<>();

	private final FactHandle  currThingHandler;
	private final FactHandle  externalHandler;


	private final AtomicCurrThing currThing=new AtomicCurrThing();

	private final ExternalCollect  external=new ExternalCollect();


	private final Consumer<List<MatchResult>> consumer;



	public void enterInit(){

		currThing.updateAndGet((th)-> {
					th.setStatus(CurrThing.Status.inInit);
					th.cleanThings();
					return th;
				});
		kieSession.update(currThingHandler,currThing);
		kieSession.fireAllRules();
	}

	public void leaveInit(){

		
		currThing.updateAndGet((th)->{
			th.setStatus(CurrThing.Status.inIdle);
			th.cleanThings();
			return th;
		});
		kieSession.update(currThingHandler,currThing);
		kieSession.fireAllRules();

	}

	public void toIdle(){
		
		
		currThing.updateAndGet((th)->{
			if(th.getStatus()== CurrThing.Status.inInit){
				return th;
			}
			th.cleanThings();
			th.setStatus(CurrThing.Status.inIdle);
			return th;
		});
		kieSession.update(currThingHandler,currThing);
		
	}
	
	public void inFireTrigger(String triggerID) {
		
		
		CurrThing thing=currThing.updateAndGet((th)->{
			
			
			CurrThing.Status oldStatus = th.getStatus();
			if(oldStatus==CurrThing.Status.inInit){
				return th;
			}
			
			th.setStatus(CurrThing.Status.singleTrigger);
			th.setTriggerID(triggerID);
			return th;
		});
		
		if(thing.getStatus()== CurrThing.Status.inInit){
			return;
		}
		
		synchronized (kieSession) {
			kieSession.update(currThingHandler, currThing);
			fireDrools();
			currThing.compareAndSet(thing, CurrThing.Status.inIdle);
			kieSession.update(currThingHandler, currThing);
		}
	}


	public void inThing(Set<String> thingIDs,ExternalValues newValues){

		settingCurrThing(thingIDs,newValues, CurrThing.Status.inThing);
	}


//	public  void updateScheduleData(ScheduleFire fire){
//
//		CurrThing thing=currThing.updateAndGet((th)->{
//
//
//			CurrThing.Status oldStatus = th.getStatus();
//			if(oldStatus==CurrThing.Status.inInit){
//				return th;
//			}
//
//			if(oldStatus==CurrThing.Status.inThing) {
//
//				th.setStatus(CurrThing.Status.inIdle);
//			}
//			return th;
//		});
//
//		if(thing.getStatus()==CurrThing.Status.inInit){
//			return;
//		}
//
//		synchronized (kieSession) {
//			FactHandle handler = kieSession.insert(fire);
//			fireDrools();
//			kieSession.delete(handler);
//		}
//
//	}
	
	
	private void fireDrools(){
		try{
			
			kieSession.fireAllRules();
			
			
		}catch(Exception e){
			log.error(e.getMessage());
			SysMonitorMsg notice=new SysMonitorMsg();
			notice.setFrom(SysMonitorMsg.FromType.RuleEngine);
			notice.setErrMessage(e.getMessage());
			notice.setErrorType("fireDrools");
			SysMonitorQueue.getInstance().addNotice(notice);
		}
	}

	private void settingCurrThing(Set<String> thingIDs,ExternalValues newValues,CurrThing.Status  status){
		
		
		CurrThing thing=currThing.updateAndGet((th)->{
			
			
			CurrThing.Status oldStatus = th.getStatus();
			if(oldStatus==CurrThing.Status.inInit){
				return th;
			}

			th.setStatus(status);
			th.setCurrThings(thingIDs);
			
			return th;
		});
		
		if(thing.getStatus()==CurrThing.Status.inInit){
			return;
		}
		
		synchronized (kieSession) {
			external.updateEntity(newValues);
			
			kieSession.update(externalHandler,external);
			kieSession.update(currThingHandler, currThing);
			fireDrools();
		}
		ExternalValues entity=new ExternalValues("sys");
		entity.addValue("curr",thing);
		external.updateEntity(entity);

	}

	
	public void moveHistory(String thingID){
		List<MultiplesValueMap>  mapResult=doQuery("get multiples Result by TriggerID",thingID);
		mapResult.forEach((result)->{
			result.copyToHistory();
		});
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
			if(object instanceof BusinessObjInRule){
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



	public DroolsService(Consumer<List<MatchResult>>  consumer , boolean isStream, String...  rules){

		this.consumer=consumer;

		ks = KieServices.Factory.get();


		kfs= ks.newKieFileSystem();

		int i=0;
		for(String rule:rules) {

			String drlName="src/main/resources/comm_"+i+".drl";

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


		if(!isStream&&"local".equals(profile)) {
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
		
		try {
			
			kieContainer.updateToVersion(kb.getKieModule().getReleaseId());
		}catch(RuntimeException e){
			kfs.delete(drlName);
			pathSet.remove(drlName);
//			kb=ks.newKieBuilder(kfs);
//			kb.buildAll();
			log.error(e.getMessage());
			
			throw e;
		}

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


		toIdle();


	}


	public void addOrUpdateData(Object entity,boolean replace){


		handleMap.compute(getEntityKey(entity),(k,v)->{
			    if(v==null) {
					kieSession.insert(entity);
					return entity;
				}else{
					if(!replace&&v instanceof CanUpdate){
						
						FactHandle handle=kieSession.getFactHandle(v);
						if(handle!=null) {
							((CanUpdate) v).update(entity);
							kieSession.update(handle, v);
						}else{
							kieSession.insert(entity);
						}
						return v;
					}else {
						
						FactHandle handle=kieSession.getFactHandle(v);
						if(handle!=null) {
							kieSession.delete(handle);
						}
						kieSession.insert(entity);
						return entity;
					}
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

	private <T> List<T> doQuery(String queryName,Object... params){



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
		
		FactHandle handler=getHandle(entityKey);
		
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
	
	private FactHandle getHandle(String key){
		Object obj=handleMap.get(key);
		
		return kieSession.getFactHandle(obj);
	}
	

}
