package com.kii.extension.sdk.test.thingif;

import javax.annotation.PostConstruct;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.ThingTokenBindTool;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.AppChoice;
import com.kii.extension.sdk.entity.thingif.InstallationID;
import com.kii.extension.sdk.entity.thingif.InstallationInfo;
import com.kii.extension.sdk.entity.thingif.MqttEndPoint;
import com.kii.extension.sdk.service.ThingIFService;
import com.kii.extension.sdk.test.TestTemplate;

public class TestMqtt extends TestTemplate {

	@Autowired
	private ThingIFService service;

	@Autowired
	private AppBindToolResolver bindTool;

	@Autowired
	private TokenBindToolResolver tokenResolver;

	@Autowired
	private ThingTokenBindTool tokenBindTool;


	@PostConstruct
	public void init(){

		AppChoice choice=new AppChoice();
		choice.setAppName("thingif");
		choice.setTokenBindName("thing");

		bindTool.pushAppChoice(choice);


	}

	@Test
	public void testMqttGet(){

		String thingID="th.f83120e36100-a1a9-6e11-7f49-0470aa4e";
		String pwd="qwerty";

		tokenBindTool.bindThing(thingID,pwd);

		List<InstallationInfo> infos=service.getInstallationInfosByThingID(thingID);

		String infoID=null;

		if(!infos.isEmpty()){

			for(InstallationInfo info:infos){
				if("MQTT".equals(info.getInstallationType())){
					infoID=info.getInstallationID();
				}
			}
		}

		if(infoID==null){
			InstallationID id = service.registerInstallaction();
			infoID=id.getInstallationID();
		}

		MqttEndPoint mqtt=service.getMQTTByInstallationID(infoID);

	}

}
