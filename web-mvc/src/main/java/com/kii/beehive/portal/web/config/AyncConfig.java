//package com.kii.beehive.portal.web.config;
//
//import java.lang.reflect.Method;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * Created by hdchen on 1/20/17.
// */
//@EnableAsync
//@Configuration
//public class AyncConfig implements AsyncConfigurer {
//	@Autowired
//	@Qualifier("defaultAsyncUncaughtExceptionHandler")
//	private AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;
//
//	@Override
//	@Bean(name = "myExecutor")
//	public Executor getAsyncExecutor() {
//		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//		executor.setCorePoolSize(10);
//		executor.setMaxPoolSize(50);
//		executor.setQueueCapacity(10000);
//		executor.setThreadNamePrefix("DefaultAsyncExecutor-");
//		executor.setKeepAliveSeconds(600);
//		executor.setDaemon(true);
//		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//		executor.setThreadGroupName("DefaultAsyncExecutorGroup");
//		return executor;
//	}
//
//	@Override
//	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//		return asyncUncaughtExceptionHandler;
//	}
//
//	@Bean(name = "defaultAsyncUncaughtExceptionHandler")
//	public AsyncUncaughtExceptionHandler getDefaultAsyncUncaughtExceptionHandler() {
//		return new AsyncUncaughtExceptionHandler() {
//			private Logger LOG = LoggerFactory.getLogger(AsyncUncaughtExceptionHandler.class);
//
//			private ObjectMapper mapper = new ObjectMapper();
//
//			@Override
//			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
//				LOG.info(new StringBuilder("Method: ").append(method.getName()).append("(")
//						.append(paramToString(params)).append(")").toString(), ex);
//			}
//
//			private String paramToString(Object... params) {
//				if (null == params) {
//					return "";
//				}
//				try {
//					return mapper.writeValueAsString(params);
//				} catch (JsonProcessingException e) {
//					return params.toString();
//				}
//			}
//		};
//	}
//}
