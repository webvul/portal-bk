package com.kii.beehive.portal.helper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class HttpClient implements Closeable {

	private Logger log = LoggerFactory.getLogger(HttpClient.class);

	private CloseableHttpClient httpClient;


	@Autowired
	private ObjectMapper mapper;


	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	@PostConstruct
	public void init() throws IOReactorException {

		httpClient = HttpClients.custom()
				.setConnectionTimeToLive(10, TimeUnit.SECONDS)
				.build();
		

	}

	@PreDestroy
	public void close() throws IOException {

		httpClient.close();

	}


	public <T> T executeRequestWithCls(HttpUriRequest request, Class<T> cls) {

		return executeRequestWithCls(request, cls, null);

	}

	public <T> T executeRequestWithCls(HttpUriRequest request, Class<T> cls, HttpContext context) {


		String result = executeRequest(request, context);

		try {
			return mapper.readValue(result, cls);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	public HttpResponse doRequest(HttpUriRequest request, HttpContext context) {

		try {
			HttpResponse response = null;
			log.debug("start do request to " + request.getMethod() + " " + request.getURI().toASCIIString());
			if (context == null) {
				response = httpClient.execute(request);
			} else {
				response = httpClient.execute(request, context);
			}


			return response;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public HttpResponse doRequest(HttpUriRequest request) {


		return doRequest(request, null);

	}


	public String executeRequest(HttpUriRequest request) {
		return executeRequest(request, null);
	}


	public String executeRequest(HttpUriRequest request, HttpContext context) {


		HttpResponse response = doRequest(request, context);


		if (request.getMethod().equals("DELETE")) {
			return "";
		}

		return HttpUtils.getResponseBody(response);
	}



}
