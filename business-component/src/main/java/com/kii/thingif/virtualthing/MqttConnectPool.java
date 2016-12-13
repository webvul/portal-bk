package com.kii.thingif.virtualthing;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.extension.sdk.entity.thingif.MqttEndPoint;
import com.kii.extension.sdk.entity.thingif.ThingCommand;

@Component
public class MqttConnectPool {

	private Logger log = LoggerFactory.getLogger(MqttConnectPool.class);

	private ExecutorService executeLocal = Executors.newFixedThreadPool(20);

	@Autowired
	private CommandOperate operate;

	@Autowired
	private ObjectMapper mapper;

	
	
	public void connectToMQTT(String thingID, MqttEndPoint endPoint) {
		RunTask task = new RunTask(thingID, endPoint);

		executeLocal.submit(task);
	}

	private class RunTask implements Runnable {


		private String thingID;

		private MqttEndPoint mqttPoint;

		public RunTask(String thingID, MqttEndPoint mqttEndPoint) {
			this.thingID = thingID;

			this.mqttPoint = mqttEndPoint;
		}

		@Override
		public void run() {

			registerToMQTT(mqttPoint);

		}

		private void registerToMQTT(MqttEndPoint info) {


			MqttConnectOptions connOpt = new MqttConnectOptions();


			connOpt.setUserName(info.getUserName());
			connOpt.setPassword(info.getPassword().toCharArray());
			connOpt.setCleanSession(false);

			connOpt.setKeepAliveInterval(30);

			String brokerUrl = "tcp://" + info.getHost() + ":" + info.getPortTCP();

			try {
				MqttClient client = new MqttClient(brokerUrl, info.getMqttTopic());

				client.setCallback(new MqttCallback() {
					@Override
					public void connectionLost(Throwable throwable) {
						log.info("connect lost:" + throwable.getMessage());
					}

					@Override
					public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

						log.info("s:" + s);

						String json = mqttMessage.toString();

						ThingCommand cmd = mapper.readValue(json, ThingCommand.class);

						operate.onCommandReceive(cmd, thingID);

					}

					@Override
					public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
						log.info("delivery complete");
					}
				});

				client.connect(connOpt);

				client.subscribe(info.getMqttTopic());

			} catch (Exception e) {
				log.error(e.getMessage());
			}

		}

	}


}
