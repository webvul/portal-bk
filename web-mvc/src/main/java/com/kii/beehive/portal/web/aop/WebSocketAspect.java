//package com.kii.beehive.portal.web.aop;
//
//import java.lang.reflect.Field;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.BeanFactoryAware;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.SubscribableChannel;
//import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
//import com.kii.beehive.portal.web.stomp.StompDecoderDecorator;
//
///**
// * Created by hdchen on 7/18/16.
// */
//@Aspect
//public class WebSocketAspect implements BeanFactoryAware {
//	private BeanFactory beanFactory;
//
//	@Pointcut("execution(* subProtocolWebSocketHandler(..)) && within (org.springframework.web.socket.config" +
//			".annotation.WebSocketMessageBrokerConfigurationSupport+))")
//	public void subProtocolWebSocketHandler() {
//	}
//
//	@Around("subProtocolWebSocketHandler()")
//	public com.kii.beehive.portal.web.socket.SubProtocolWebSocketHandler subProtocolWebSocketHandler(
//			ProceedingJoinPoint joinPoint) throws Throwable {
//		SubProtocolWebSocketHandler handler = (SubProtocolWebSocketHandler) joinPoint.proceed();
//		SubscribableChannel subscribableChannel = null;
//		MessageChannel messageChannel = null;
//		Field[] fields = SubProtocolWebSocketHandler.class.getDeclaredFields();
//
//		for (Field field : fields) {
//			if (MessageChannel.class.isAssignableFrom(field.getType())) {
//				field.setAccessible(true);
//				if (SubscribableChannel.class.isAssignableFrom(field.getType())) {
//					subscribableChannel = (SubscribableChannel) field.get(handler);
//				} else {
//					messageChannel = (MessageChannel) field.get(handler);
//				}
//			}
//		}
//		return new com.kii.beehive.portal.web.socket.SubProtocolWebSocketHandler(messageChannel, subscribableChannel,
//				(StompDecoderDecorator) beanFactory.getBean(StompDecoderDecorator.class));
//	}
//
//	@Override
//	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//		this.beanFactory = beanFactory;
//	}
//}
