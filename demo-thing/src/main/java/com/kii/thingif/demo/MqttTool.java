package com.kii.thingif.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kii.extension.sdk.entity.thingif.MqttEndPoint;

public class MqttTool {

	private Logger log=LoggerFactory.getLogger(MqttTool.class);

	private ExecutorService executeLocal = Executors.newFixedThreadPool(20);


	private MqttClient client;



	public void closeMqtt() {
		try {
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void registerToMQTT(BiConsumer<String, MqttMessage> callback, MqttEndPoint info) {
		MqttConnectOptions connOpt = new MqttConnectOptions();


		connOpt.setUserName(info.getUserName());
		connOpt.setPassword(info.getPassword().toCharArray());
		connOpt.setCleanSession(false);

		connOpt.setKeepAliveInterval(30);

		String brokerUrl = "tcp://" + info.getHost() + ":" + info.getPortTCP();

		try {
			client = new MqttClient(brokerUrl, info.getMqttTopic());


			client.connect(connOpt);

			client.subscribe(info.getMqttTopic());

			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable throwable) {
					log.info("connect lost:" + throwable.getMessage());
				}

				@Override
				public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

					log.info("s:" + s);
					log.info("msg:" + mqttMessage.toString());

					executeLocal.submit(() -> callback.accept(s, mqttMessage));

				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
					log.info("delivery complete");
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
