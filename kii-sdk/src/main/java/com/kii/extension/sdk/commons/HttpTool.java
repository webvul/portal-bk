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
import java.util.function.Consumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
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
				.build());

		final PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(
				ioReactor);
		connManager.setDefaultMaxPerRoute(50);
		connManager.setMaxTotal(500);

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
			if(e instanceof  ExecutionException) {
				if (e.getCause().getCause() instanceof ClientProtocolException) {
					throw new IllegalStateException(e.getCause().getCause());
				}
			}
			
			throw new IllegalStateException(e);
		}
	}



	public Future<HttpResponse> asyncExecuteRequest(HttpUriRequest request, FutureCallback<HttpResponse>  callback){

		return  httpClient.execute(request,callback);

	}
	
	private static final int[] delayArray=new int[]{0,2,5,10,20,60};
	
	public HttpResponse doRequstWithRetry(RequestBuilder builder) throws Throwable {
		
		return doRequstWithRetry(builder,(b)->{
			
		});
	}
	
	public HttpResponse doRequstWithRetry(RequestBuilder builder, Consumer<RequestBuilder> modify) throws Throwable {
		
		
		int retryNum=-1;
		
		Throwable exception=null;
		while(retryNum<5) {
			
			retryNum++;
			
			modify.accept(builder);
			
			try {
				
				Future<HttpResponse> future = executorService.schedule(() -> doRequest(builder.build()), delayArray[retryNum], TimeUnit.SECONDS);
				
				HttpResponse response = future.get();
				
				int code = response.getStatusLine().getStatusCode();
				
				if (code >= 500) {
					continue;
				}
				return response;
			} catch (InterruptedException e) {
				exception=e;
			}catch(ExecutionException ex ){
				if(ex.getCause() instanceof ExecutionException) {
					if (ex.getCause().getCause() instanceof ClientProtocolException) {
						exception = ex.getCause().getCause();
						continue;
					}
				}
				exception=ex.getCause();
				break;
			}
			
		}
		
		if(exception!=null){
			throw exception;
		}else{
			throw new IllegalArgumentException();
		}
		
	}
	
}
