package com.kii.beehive.portal.web.help;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.entitys.ThingStatusMsg;

@Component
public class ThingStatusQueue {
	
	private Logger log= LoggerFactory.getLogger(ThingStatusQueue.class);
	
	
	private ExecutorService executorService= Executors.newFixedThreadPool(10);
	
	
	private BlockingQueue<ThingStatusMsg> msgQueue=new LinkedBlockingQueue<>(32687);
	
	private final Set<Task> taskSet=new HashSet<>();
	
	
	//TODO:add monitor
	public boolean pushInfo(ThingStatusMsg info){
		
		try {
			return msgQueue.offer(new ThingStatusMsg(info),1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			return false;
		}
		
	}
	
	private class Task implements  Runnable{
		
		
		private final int threadNum;
		
		private final Consumer<ThingStatusMsg>  task;
		
		private  BlockingQueue<ThingStatusMsg> queue;
		
		public Task(Consumer<ThingStatusMsg> task,int num){
			this.task=task;
			this.threadNum=num;
			this.queue=new LinkedBlockingQueue<>(10000*num);
		}
		
		@Override
		public void run() {
			
			while(true){
				
				try {
					ThingStatusMsg msg=queue.take();
					
					task.accept(msg);
					
					msg=null;
				} catch (InterruptedException e) {
					break;
				} catch(Exception e){
					log.error(e.getMessage());
				}
			}
			
		}
		
		//TODO:add monitor
		public boolean addMsg(ThingStatusMsg msg)  {
			try {
				return queue.offer(msg,1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
				return false;
			}
		}
		
	}
	
	public void registerConosumer(Consumer<ThingStatusMsg> thingFun,int num){
		
		taskSet.add(new Task(thingFun,num));
		
	}
	

	
	public   void handlerThingMsg(){
		
		taskSet.forEach(t->{
			for(int i=0;i<t.threadNum;i++) {
				executorService.submit(t);
			}
		});
		
		executorService.submit(() -> {
			
			while(true) {

				try {
					
					ThingStatusMsg th = msgQueue.take();
					
					for(Task t:taskSet){
						t.addMsg(new ThingStatusMsg(th));
					};
					
					th=null;
					
				} catch (InterruptedException e) {
					break;
				} catch(Exception e){
					log.error(e.getMessage());
					
				}
			}
		});
		
	}

}
