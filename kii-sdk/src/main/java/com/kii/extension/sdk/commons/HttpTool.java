package com.kii.extension.sdk.commons;

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
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicCookieStore;
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
import org.springframework.stereotype.Component;


@Component
public class HttpTool implements Closeable {


	private Logger log= LoggerFactory.getLogger(HttpTool.class);

	private CloseableHttpAsyncClient httpClient;


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


	private HttpClientContext context;

	private CookieStore cookieStore=new BasicCookieStore();


	private ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();

	@PostConstruct
	public void init() throws IOReactorException {

		ConnectingIOReactor ioReactor= new DefaultConnectingIOReactor(IOReactorConfig.custom()
				.setIoThreadCount(32)
				.setSoKeepAlive(true)
				.setConnectTimeout(30)
				//.setTcpNoDelay(true)
				.build());

		final PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
				ioReactor);
		connManager.setDefaultMaxPerRoute(20);
		connManager.setMaxTotal(1000);

		log.info("connManager.getDefaultMaxPerRoute: " + connManager.getDefaultMaxPerRoute());
		log.info("connManager.getMaxTotal: " + connManager.getMaxTotal());

		executorService.scheduleAtFixedRate(() -> {
			connManager.closeExpiredConnections();
			connManager.closeIdleConnections(60, TimeUnit.SECONDS);
		}, 0, 30, TimeUnit.SECONDS);

		context = HttpClientContext.create();
		context.setCookieStore(cookieStore);

		RequestConfig globalConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.STANDARD)
				.build();
		context.setRequestConfig(globalConfig);


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


	public HttpContext getContext(){
		return context;
	}

	public CookieStore getCookieStore(){
		return cookieStore;
	}

	public HttpResponse doRequest(HttpUriRequest request){
		return doRequest(request,false);

	}


	public HttpResponse doRequest(HttpUriRequest request,boolean withCookie){
		try{
			Future<HttpResponse> future=null;
			log.debug("start do request to " + request.getMethod()  + " " + request.getURI().toASCIIString());
			if(withCookie) {
				future = httpClient.execute(request, context, callback);
			}else{
				future = httpClient.execute(request,callback);
			}
			HttpResponse response=future.get();

			return response;
		}  catch (InterruptedException|ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}



	public Future<HttpResponse> asyncExecuteRequest(HttpUriRequest request, FutureCallback<HttpResponse>  callback){

		return  httpClient.execute(request,callback);

	}
}
