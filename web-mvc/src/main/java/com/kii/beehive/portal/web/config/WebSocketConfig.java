package com.kii.beehive.portal.web.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.portal.web.aop.WebSocketAspect;
import com.kii.beehive.portal.web.controller.STOMPMessageController;
import com.kii.beehive.portal.web.socket.EchoHandler;
import com.kii.beehive.portal.web.stomp.MessageManager;

/**
 * Created by hdchen on 6/27/16.
 */
@Configuration
@EnableAsync
@EnableWebMvc
@EnableWebSocket
@EnableWebSocketMessageBroker
@EnableAspectJAutoProxy
@Import({WebSocketMessageBrokerConfig.class, PropertySourcesPlaceholderConfig.class})
@ComponentScan(basePackages = {"com.kii.beehive.portal.web.controller", "com.kii.beehive.portal.web.stomp",
		"com.kii.beehive.portal.web.aop", "org.springframework.web.socket.config.annotation"},
		useDefaultFilters = false, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				value = {STOMPMessageController.class, MessageManager.class, WebSocketAspect.class,
						WebSocketMessageBrokerConfigurationSupport.class})})
public class WebSocketConfig implements WebSocketConfigurer, AsyncConfigurer {
	@Autowired
	@Qualifier("defaultAsyncUncaughtExceptionHandler")
	private AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler;

	@Autowired
	@Qualifier("myExecutor")
	private Executor executor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new EchoHandler(), "/echo").setAllowedOrigins("*");
	}

	@Override
	public Executor getAsyncExecutor() {
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return asyncUncaughtExceptionHandler;
	}

	@Bean(name = "defaultAsyncUncaughtExceptionHandler")
	public AsyncUncaughtExceptionHandler getDefaultAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {
			private Logger LOG = LoggerFactory.getLogger(AsyncUncaughtExceptionHandler.class);

			private ObjectMapper mapper = new ObjectMapper();

			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
				LOG.info(new StringBuilder("Method: ").append(method.getName()).append("(")
						.append(paramToString(params)).append(")").toString(), ex);
			}

			private String paramToString(Object... params) {
				if (null == params) {
					return "";
				}
				try {
					return mapper.writeValueAsString(params);
				} catch (JsonProcessingException e) {
					return params.toString();
				}
			}
		};
	}
}
