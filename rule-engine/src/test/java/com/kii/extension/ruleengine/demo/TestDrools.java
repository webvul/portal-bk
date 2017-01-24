package com.kii.extension.ruleengine.demo;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.TimedRuleExectionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.util.StreamUtils;

public class TestDrools {
	
	
	private  KieSession kieSession;
	
	private  KieContainer kieContainer;
	
	private  KieServices ks;
	
	private  KieFileSystem kfs;

	@Before
	public void init() throws IOException {
		ks = KieServices.Factory.get();
		
		
		kfs= ks.newKieFileSystem();
		
		String[] names={"demo"};
		
		int i=0;
		for(String name:names) {
			
			String rule= StreamUtils.copyToString(this.getClass().getClassLoader().getResourceAsStream("com/kii/extension/ruleengine/"+name+".drl"), Charsets.UTF_8);
			
			String drlName="src/main/resources/"+name+".drl";

//			byte[]  bytes=kfs.read(drlName);
			
			kfs.write(drlName, rule);
			i++;
		}
		
		KieBuilder kb = ks.newKieBuilder(kfs);
		
		kb.buildAll();
		
		kieContainer= ks.newKieContainer(kb.getKieModule().getReleaseId());
		
		KieBase kieBase = kieContainer.getKieBase();
		
		KieSessionConfiguration ksconf = KieServices.Factory.get().newKieSessionConfiguration();
		ksconf.setOption( TimedRuleExectionOption.YES );
		
		kieSession = kieBase.newKieSession(ksconf,null);
		
//		kieSession.addEventListener(new DebugAgendaEventListener());
//		kieSession.addEventListener(new DebugRuleRuntimeEventListener());
		
		msg.setMsg("world");
		msg.setStatus(Message.HELLO);
		
		room.setName("a");
		
		
		handler= kieSession.insert(msg);
		roomHandler=kieSession.insert(room);
		
	}
	
	Message msg=new Message();
	
	Fire fire=new Fire();
	
	Room room=new Room();
	
	FactHandle handler;
	FactHandle roomHandler;
			
	@Test
	public void testObjModify(){
		
		kieSession.fireAllRules();
		
		msg.setStatus(Message.GOODBYE);
		kieSession.update(handler,msg);
//		kieSession.fireAllRules();
		
		msg.setStatus(Message.HELLO);
		msg.setMsg("drools");
		kieSession.update(handler,msg);
		
		
		kieSession.fireAllRules();
	}
	
	ScheduledExecutorService service= Executors.newScheduledThreadPool(10);
	
	
	@Test
	public void testConcurrent() throws IOException {
		
		
		service.scheduleAtFixedRate(()->{
			room.setName("b");
			kieSession.update(roomHandler,room);
	
			System.out.println("this is B");
			
			try {
				Thread.sleep(1000* RandomUtils.nextInt(0,3));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			kieSession.fireAllRules();
		},0,3, TimeUnit.SECONDS);
		
		service.scheduleAtFixedRate(()->{
			room.setName("c");
			kieSession.update(roomHandler,room);
			System.out.println("this is C");
			
			try {
				Thread.sleep(1000* RandomUtils.nextInt(0,3));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			kieSession.fireAllRules();
		},0,2, TimeUnit.SECONDS);
		
		System.in.read();
		
	}
	
	

}
