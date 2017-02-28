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
	
	private final static int NUM = 5;
	private static final SysMonitorQueue instance = new SysMonitorQueue();
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	private final AtomicReference<SysMonitorMsg> queue = new AtomicReference<SysMonitorMsg>();
	private final Map<Integer, List<SysMonitorMsg>> historyCycleMap = new ConcurrentHashMap<>(NUM);
	private final AtomicInteger index = new AtomicInteger(0);
	private final Logger log = LoggerFactory.getLogger(SysMonitorQueue.class);
	private final ExecutorService pool = Executors.newCachedThreadPool();
	private final Map<String, Callable<Boolean>> taskMap = new ConcurrentHashMap<>();
	
	private SysMonitorQueue(){
		
		
		for (int i = 0; i < NUM; i++) {
			historyCycleMap.put(i, new ArrayList<>());
		}
		
		executorService.scheduleAtFixedRate(() -> {
			int oldIdx = index.accumulateAndGet(1, (oldV, newV) -> (oldV + newV) % NUM);
			List<SysMonitorMsg> list = historyCycleMap.get(oldIdx - 1);
			
			list.clear();
			
		},1,30, TimeUnit.MINUTES);
	}
	
	public static SysMonitorQueue getInstance(){
		return instance;
	}
	
	public void addNotice(SysMonitorMsg notice){
		
		
		historyCycleMap.get(index.get()).add(notice);
		
		queue.set(notice);
		
		try {
			pool.invokeAll(taskMap.values());
		} catch (InterruptedException e) {
			return;
		}
		
	}
	
	public void registerFire(String id,Function<SysMonitorMsg,Boolean> callback){
		
		taskMap.put(id,() -> {
			SysMonitorMsg msg =queue.get();
			return callback.apply(msg);
		});
	}
	
	public List<SysMonitorMsg> getMsgHistory(){
		List<SysMonitorMsg> msgList=new ArrayList<>();
		
		historyCycleMap.forEach((k, v) -> {
			
		});
		int idx = index.get();
		
		for (int i = NUM; i > 0; i--) {
			msgList.addAll(historyCycleMap.get((idx + i) % NUM));
		}
		return msgList;
	}
	
	public void unRegisterFire(String id) {
		
		taskMap.remove(id);
	}
}
