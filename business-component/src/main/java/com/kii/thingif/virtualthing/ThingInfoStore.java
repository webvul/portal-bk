package com.kii.thingif.virtualthing;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.MqttEndPoint;

@Component
public class ThingInfoStore {


	private Map<String,ThingInfo> map=new HashMap<>();


	public void storeInfo(ThingInfo info){
		map.put(info.getThingID(),info);
	}

	public ThingInfo getInfo(String thingID){
		return map.get(thingID);
	}


	public  static class ThingInfo{

		private String thingID;

		private ThingSchema thingSchema;

		private MqttEndPoint mqttEndPoint;

		private String appInfo;

		public String getThingID() {
			return thingID;
		}

		public void setThingInfo(GlobalThingInfo  thingInfo) {
			this.thingID = thingInfo.getKiiThingID();
			this.appInfo=thingInfo.getKiiAppID();
		}

		public ThingSchema getThingSchema() {
			return thingSchema;
		}

		public void setThingSchema(ThingSchema thingSchema) {
			this.thingSchema = thingSchema;
		}

		public MqttEndPoint getMqttEndPoint() {
			return mqttEndPoint;
		}

		public void setMqttEndPoint(MqttEndPoint mqttEndPoint) {
			this.mqttEndPoint = mqttEndPoint;
		}

		public String getAppInfo() {
			return appInfo;
		}

	}
}
