package com.kii.beehive.portal.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.service.UserSyncMsgDao;
import com.kii.beehive.portal.store.entity.usersync.SupplierPushMsgTask;
import com.kii.extension.sdk.impl.KiiCloudClient;

@Component
public class NotifySenderTool {


	@Autowired
	private KiiCloudClient client;

	@Autowired
	private UserSyncMsgDao  msgDao;

	private final Integer RETRY_NUM=6;

	@Async
	public void doSyncTask(SupplierPushMsgTask msg,final Map<String,String> urlMap){


		final Map<String,Integer> retryRecord=msg.getRetryRecord();


		List<String> supplierList=urlMap.keySet().stream()
				.filter((supplierID) -> {
					Integer val=retryRecord.get(supplierID);
					if(val==null){
						return true;
					}
					return val<100&&val>0&&!(supplierID.equals(msg.getSourceSupplier()));
				})
				.collect(Collectors.toCollection(ArrayList<String>::new));

		final CountDownLatch latch = new CountDownLatch(supplierList.size());

		String context=msg.getMsgContent();

		supplierList.parallelStream().forEach((name) -> {

			HttpPost request = new HttpPost(urlMap.get(name));

			request.setEntity(new StringEntity(context, ContentType.APPLICATION_JSON));

			client.syncExecuteRequest(request, new FutureCallback<HttpResponse>() {
				@Override
				public void completed(HttpResponse httpResponse) {
					int status=httpResponse.getStatusLine().getStatusCode();
					if(status>=200&&status<300){
						retryRecord.put(name,100);
					}else{
						retryRecord.merge(name,RETRY_NUM, (k, v) -> v--);

					}
					latch.countDown();
				}

				@Override
				public void failed(Exception e) {
					retryRecord.merge(name, RETRY_NUM, (k, v) -> v--);
					latch.countDown();

				}

				@Override
				public void cancelled() {
					retryRecord.merge(name, RETRY_NUM, (k, v) -> v--);

					latch.countDown();
				}
			});

		});

		try {
			boolean sign=latch.await(1, TimeUnit.MINUTES);
			if(!sign){
				retryRecord.replaceAll((k,v)->v==100?100:v-- );
			}

			msgDao.updateTaskStatus(retryRecord,msg.getId(),msg.getVersion());
		} catch (InterruptedException e) {

			e.printStackTrace();
		}


	}
}
