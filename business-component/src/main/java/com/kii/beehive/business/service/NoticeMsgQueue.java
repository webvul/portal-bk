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
	
	public void addNotice(UserNotice notice){
		
		TransferQueue<UserNotice> queue=queueMap.putIfAbsent(notice.getUserID(),new LinkedTransferQueue<>());
		
		if(queue.hasWaitingConsumer()){
			queue.tryTransfer(notice);
		}
		
	}
	
	
	public void regist(long userID, Function<UserNotice,Boolean> function){
		
		
		executorService.submit(() -> {
			
			boolean sign=true;
			while(sign) {
				TransferQueue<UserNotice> queue = queueMap.putIfAbsent(userID, new LinkedTransferQueue<>());
				
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
	
	
//	private Map<Long,Consumer<UserNotice>> funMap=new ConcurrentHashMap<>();
//
//	public void regist(long userID, Consumer<UserNotice>  consumer){
//
//		funMap.put(userID,consumer);
//	}
//
//	private static final Consumer<UserNotice> defaultFun=new Consumer<UserNotice>() {
//		@Override
//		public void accept(UserNotice notice) {
//			return;
//		}
//	};
//
//	public void fireNotice(UserNotice notice){
//
//		Consumer<UserNotice>  consumer=funMap.putIfAbsent(notice.getUserID(),defaultFun);
//
//		consumer.accept(notice);
//	}

}
