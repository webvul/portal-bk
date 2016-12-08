package com.kii.beehive.business.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.UserNotice;

@Component
public class NoticeMsgQueue {
	
	private ExecutorService executorService= Executors.newCachedThreadPool();

	private Map<Long,TransferQueue<UserNotice>> queueMap=new ConcurrentHashMap<>();
	
	private Map<Long,UserNotice>  currMsgMap=new ConcurrentHashMap<>();
	
	
	public void addNotice(UserNotice notice){
		
		currMsgMap.put(notice.getUserID(),notice);
		
		TransferQueue<UserNotice> queue=queueMap.putIfAbsent(notice.getUserID(),new LinkedTransferQueue<>());
		
		if(queue.hasWaitingConsumer()){
			queue.tryTransfer(notice);
		}
		
	}
	
	
	public void regist(long userID, Function<UserNotice,Boolean> function){
		
		
		function.apply(currMsgMap.get(userID));
		
		executorService.submit(() -> {
			
			boolean sign=true;
			while(sign) {
				TransferQueue<UserNotice> queue = queueMap.putIfAbsent(userID, new LinkedTransferQueue<>());
				if(queue==null){
					continue;
				}
				try {
					UserNotice notice = queue.take();
				
					sign=function.apply(notice);
				
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					break;
				} catch(Exception e){
					e.printStackTrace();
					break;
				}
			}
		});
		
	}
	
	

}
