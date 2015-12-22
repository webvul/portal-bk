//package com.kii.beehive.portal.activemq;
//
//
//import org.apache.activemq.command.ActiveMQQueue;
//import org.apache.activemq.pool.PooledConnectionFactory;
//import org.apache.activemq.spring.ActiveMQConnectionFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jms.annotation.EnableJms;
//import org.springframework.jms.core.JmsTemplate;
//import org.springframework.jms.listener.DefaultMessageListenerContainer;
//import org.springframework.jms.listener.MessageListenerContainer;
//
//
////@Configuration
//@EnableJms
//public class ActionMsgQueueFactory {
//
//	public static final String MQ_NAME="msgQueue";
//
//	@Bean
//	public ActiveMQConnectionFactory  getActionMqConnectionFactory(){
//		ActiveMQConnectionFactory factory=new ActiveMQConnectionFactory();
//
//		factory.setBrokerURL("tcp://localhost:61617");
//
//		return factory;
//	}
//
//	@Bean(destroyMethod = "stop" )
//	public PooledConnectionFactory getQueueConnection(ActiveMQConnectionFactory  mqConnectionFactory){
//
//		PooledConnectionFactory connectionFactory=new PooledConnectionFactory(mqConnectionFactory);
//
//		return connectionFactory;
//	}
//
//
//	@Bean
//	public ActiveMQQueue getQueueDestination(){
//
//		return new ActiveMQQueue(MQ_NAME);
//	}
//
//	@Bean
//	public JmsTemplate getJmsQueueTemplate(PooledConnectionFactory queueConnection){
//
//		JmsTemplate jmsTemplate=new JmsTemplate(queueConnection);
//
//		return jmsTemplate;
//
//	}
//
//	/*
//
//    <bean id="messageQueuelistenerContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
//        <property name="connectionFactory" ref="pooledJmsQueueConnectionFactory" />
//        <property name="destination" ref="QueueDestination" />
//        <property name="messageListener" ref="testMessageListener" />
//        <property name="concurrentConsumers" value="5" />
//        <property name="acceptMessagesWhileStopping" value="false" />
//        <property name="recoveryInterval" value="10000" />
//        <property name="cacheLevelName" value="CACHE_CONSUMER" />
//    </bean>
//
//	 */
//	@Bean
//	public MessageListenerContainer getMessageQueuelistenerContainer(ActiveMQConnectionFactory  mqConnectionFactory,ActiveMQQueue queueDestination){
//
//		DefaultMessageListenerContainer container=new DefaultMessageListenerContainer();
//
//		container.setConnectionFactory(mqConnectionFactory);
//		container.setDestination(queueDestination);
//
//		container.setConcurrentConsumers(5);
//		container.setCacheLevelName("CACHE_CONSUMER");
//
//		return container;
//
//	}
//
//
//
//
//}
