package com.kii.extension.sdk.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.commons.HttpUtils;
import com.kii.extension.sdk.exception.ExceptionFactory;


@Component
public class KiiCloudClient implements Closeable{

	private Logger log= LoggerFactory.getLogger(KiiCloudClient.class);

	private CloseableHttpAsyncClient httpClient;

	@Autowired
	private ExceptionFactory factory;

	@Autowired
	private ObjectMapper mapper;

	private ThreadLocal<Boolean> exceptionSign=ThreadLocal.withInitial(()->true);

	public void shutdownExceptionFactory(){
		exceptionSign.set(false);
	}




	FutureCallback callback=new FutureCallback<HttpResponse>() {
		@Override
		public void completed(HttpResponse httpResponse) {

			log.debug("http request completed");

		}

		@Override
		public void failed(Exception e) {
			log.error("http request failed",e);

		}

		@Override
		public void cancelled() {
			log.debug("http request cancelled");

		}
	};

//	private Consumer<HttpResponse> exceptionFactory;

	private ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();

	@PostConstruct
	public void init() throws IOReactorException {

		ConnectingIOReactor ioReactor= new DefaultConnectingIOReactor(IOReactorConfig.custom()
				.setIoThreadCount(32)
				.setSoKeepAlive(true)
				.setConnectTimeout(30)
				.build());

		final PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
				ioReactor);

		executorService.scheduleAtFixedRate(() -> {
			connManager.closeExpiredConnections();
			connManager.closeIdleConnections(60, TimeUnit.SECONDS);
		}, 0, 30, TimeUnit.SECONDS);


		httpClient = HttpAsyncClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setConnectionManager(connManager).build();

		httpClient.start();


	}

	@PreDestroy
	public void close() throws IOException {

		httpClient.close();

		executorService.shutdown();
	}



	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls) {

		return executeRequestWithCls(request,cls,null);

	}

	public <T> T executeRequestWithCls(HttpUriRequest request,Class<T> cls,HttpContext context){


		String result=executeRequest(request,context);

		try {
			return  mapper.readValue(result, cls);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}

	public HttpResponse doRequest(HttpUriRequest request,HttpContext context){
		try{
			Future<HttpResponse> future=null;
			log.debug("start do request to " + request.getMethod()  + " " + request.getURI().toASCIIString());
			if(context==null){
				future = httpClient.execute(request,callback);
			}else {
				future = httpClient.execute(request, context,callback);
			}

			HttpResponse response=future.get();

			if(exceptionSign.get()) {
				factory.checkResponse(response, request.getURI());
			}
			exceptionSign.set(true);
			return response;
		}  catch (InterruptedException|ExecutionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public HttpResponse doRequest(HttpUriRequest request) {


		return doRequest(request, null);

	}


	public String executeRequest(HttpUriRequest request) {
		return executeRequest(request, null);
	}


	private String executeRequest(HttpUriRequest request,HttpContext context){


			HttpResponse response=doRequest(request, context);


			if(request.getMethod().equals("DELETE")){
				return "";
			}

			return HttpUtils.getResponseBody(response);
	}


	public Future<HttpResponse> asyncExecuteRequest(HttpUriRequest request, FutureCallback<HttpResponse>  callback){

		return  httpClient.execute(request,callback);

	}





}
