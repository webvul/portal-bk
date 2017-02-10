package com.kii.beehive.portal.sysmonitor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SysMonitorQueue {
	
	private ScheduledExecutorService executorService= Executors.newScheduledThreadPool(1);
	
//	private ExecutorService threadPool=Executors.newCachedThreadPool();
	
	private AtomicReference<SysMonitorMsg> queue=new AtomicReference<SysMonitorMsg>();
 	
	private Map<Integer,List<SysMonitorMsg>> historyCycleMap=new ConcurrentHashMap<>();

	private AtomicInteger index=new AtomicInteger(0);
	
	private Logger log= LoggerFactory.getLogger(SysMonitorQueue.class);
	
//	private CyclicBarrier barrier=new CyclicBarrier(1);
//
//	private AtomicInteger count=new AtomicInteger(0);
	
	private ExecutorService pool= Executors.newCachedThreadPool();
	
	private SysMonitorQueue(){
		
		
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				int old=index.getAndIncrement();
				
				historyCycleMap.remove(old-2);
			}
		},1,30, TimeUnit.MINUTES);
	}
	
	private static final SysMonitorQueue instance=new SysMonitorQueue();
	
	public static SysMonitorQueue getInstance(){
		return instance;
	}
	
	public void addNotice(SysMonitorMsg notice){
		
		
		historyCycleMap.computeIfAbsent(index.get(),(i)-> new ArrayList<>()).add(notice);
		
		queue.set(notice);
		
		try {
			pool.invokeAll(taskMap.values());
		} catch (InterruptedException e) {
			return;
		}
		
	}
	
	private Map<String,Callable<Boolean>> taskMap=new ConcurrentHashMap<>();
	
	public void registerFire(String id,Function<SysMonitorMsg,Boolean> callback){
		
		taskMap.put(id,() -> {
			SysMonitorMsg msg =queue.get();
			return callback.apply(msg);
		});
	}
	
	public List<SysMonitorMsg> getMsgHistory(){
		List<SysMonitorMsg> msgList=new ArrayList<>();
		
		msgList.addAll(historyCycleMap.computeIfAbsent(index.get()-1,(i)-> new ArrayList<>()));
		
		msgList.addAll(historyCycleMap.computeIfAbsent(index.get(),(i)-> new ArrayList<>()));
		return msgList;
	}
	
	public void unRegisterFire(String id) {
		
		taskMap.remove(id);
	}
}
