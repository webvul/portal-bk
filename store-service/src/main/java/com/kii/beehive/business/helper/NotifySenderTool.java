//package com.kii.beehive.business.helper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.concurrent.FutureCallback;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.kii.beehive.portal.service.UserSyncMsgDao;
//import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
//import com.kii.beehive.portal.store.entity.usersync.UserSyncMsg;
//import com.kii.extension.sdk.impl.KiiCloudClient;
//
//@Component
//public class NotifySenderTool {
//
//	private Logger log= LoggerFactory.getLogger(NotifySenderTool.class);
//
//	@Autowired
//	private KiiCloudClient client;
//
//	@Autowired
//	private UserSyncMsgDao  msgDao;
//
//	@Autowired
//	private ObjectMapper mapper;
//
//
//	private static final int[] delayArray=new int[6];
//
//
//	static{
//
//		int[] array=new int[]{0,30,60,120,300,600};
//		int sum=0;
//		for(int i=0;i<array.length;i++){
//			sum+=array[i];
//			delayArray[array.length-i-1]=sum;
//		}
//
//	}
//
//
//
//	@Async
//	public void doMsgSendTask(SupplierPushMsgTask msgTask, final Map<String,String> urlMap){
//
//
//		final Map<String,Integer> retryRecord=msgTask.getRetryRecord();
//
//
//		List<String> supplierList=urlMap.keySet().stream()
//				.filter((supplierID) -> {
//					Integer val=retryRecord.get(supplierID);
//					if(val==null){
//						return true;
//					}
//					return val<100&&val>0&&!(supplierID.equals(msgTask.getSourceSupplier()));
//				})
//				.collect(Collectors.toCollection(ArrayList<String>::new));
//
//		final CountDownLatch latch = new CountDownLatch(supplierList.size());
//
//		supplierList.parallelStream().forEach((name) -> {
//
//			SyncTask  task=new SyncTask(urlMap.get(name),name,latch,msgTask);
//
//			int retry=retryRecord.getOrDefault(name, UserSyncMsgDao.RETRY_NUM);
//			int delay=delayArray[retry];
//			executeService.schedule(() -> task.doRequest(retry), delay, TimeUnit.SECONDS);
//		});
//
//		try {
//			boolean sign=latch.await(20, TimeUnit.MINUTES);
//			msgDao.updateTaskStatus(msgTask.getId(),sign);
//		} catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
//
//
//	}
//
//
//	private ScheduledExecutorService  executeService= Executors.newScheduledThreadPool(20);
//
//
//	private class SyncTask {
//
//
//		private final SupplierPushMsgTask task;
//
//		private final String url;
//
//		private final CountDownLatch latch;
//
//		private final String supplierID;
//
//		public SyncTask(String url,String supplierID,CountDownLatch latch,SupplierPushMsgTask task){
//
//			this.task=task;
//			this.url=url;
//			this.latch=latch;
//			this.supplierID=supplierID;
//
//		}
//
//		private void onSuccess(int retry) {
//
//			log.debug("notify success: " + supplierID + " task id: " + task.getId() + " url: " + url);
//
//			msgDao.successSupplier(supplierID, retry, task.getId());
//
//			latch.countDown();
//
//		}
//
//
//		private void onFail(int retry) {
//
//			log.debug("notify success: " + supplierID + " task id: " + task.getId() + " url: " + url);
//
//			final int newRetry = retry - 1;
//
//			msgDao.recordRetrySupplier(supplierID, newRetry, task.getId());
//
//			if (newRetry > 0) {
//				executeService.schedule(
//						() -> doRequest(newRetry), delayArray[newRetry], TimeUnit.SECONDS);
//			}
//			return;
//		}
//
//
//		private void doRequest(int retryCount) {
//
//			HttpPost request = new HttpPost(url);
//
//			String json= null;
//			try {
//				UserSyncMsg msg=task.getMsgContent();
//				msg.setRetryCount(UserSyncMsgDao.RETRY_NUM-retryCount);
//				json = mapper.writeValueAsString(task.getMsgContent());
//			} catch (JsonProcessingException e) {
//				throw new IllegalArgumentException(e);
//			}
//
//			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
//
//
//
//			client.asyncExecuteRequest(request, new FutureCallback<HttpResponse>() {
//				@Override
//				public void completed(HttpResponse httpResponse) {
//					int status = httpResponse.getStatusLine().getStatusCode();
//					if (status >= 200 && status < 300) {
//						onSuccess(retryCount);
//					} else {
//						onFail(retryCount);
//					}
//
//				}
//
//				@Override
//				public void failed(Exception e) {
//					onFail(retryCount);
//				}
//
//				@Override
//				public void cancelled() {
//					onFail(retryCount);
//				}
//			});
//
//		}
//	}
//}
