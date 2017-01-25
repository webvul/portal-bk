package com.kii.beehive.portal.web.help;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.entitys.ThingStatusMsg;

@Component
public class ThingStatusQueue {
	
	private Logger log= LoggerFactory.getLogger(ThingStatusQueue.class);
	
	
	private ExecutorService executorService= Executors.newCachedThreadPool();
	
	
	private BlockingQueue<ThingStatusMsg> queue=new LinkedBlockingQueue<>();
	
	@Async
	public void pushInfo(ThingStatusMsg info){
		
		try {
			queue.put(info);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
//	private List<Consumer<ThingStatusMsg>> funList=new CopyOnWriteArrayList<>();
	
	private Map<String,Task> taskMap=new HashMap<>();
	
	
	private class Task implements  Runnable{
		
		
		private int threadNum=1;
		
		private Consumer<ThingStatusMsg>  task;
		
		private TransferQueue<ThingStatusMsg> queue=new LinkedTransferQueue<>();
		
		public Task(Consumer<ThingStatusMsg> task,int num){
			this.task=task;
			this.threadNum=num;
		}
		
		@Override
		public void run() {
			
			
			
		}
		
	}
	
	public void registerConosumer(String name,Consumer<ThingStatusMsg> thingFun,int num){
		
		taskMap.put(name,new Task(thingFun,num));
		
	}
	
	public void handlerThingMsg(){

		
		funList.forEach((fun)->{
			
			executorService.submit(()->{
				
				while(true)
				
			});
			
		});
		executorService.submit(() -> {
			
			while(true) {

				try {
					
					ThingStatusMsg thing = queue.take();
					
					funList.parallelStream().forEach(f->{
						f.accept(thing);
					});
					
				} catch (InterruptedException e) {
					break;
				} catch(Exception e){
					log.error(e.getMessage());
					
				}
			}
		});
		
		
	}

}
