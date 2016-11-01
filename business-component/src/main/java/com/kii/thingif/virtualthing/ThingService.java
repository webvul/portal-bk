package com.kii.thingif.virtualthing;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.context.TokenBindToolResolver;
import com.kii.extension.sdk.entity.thingif.ActionResult;
import com.kii.extension.sdk.entity.thingif.MqttEndPoint;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.OnBoardingResult;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.service.ThingIFService;


@Component
public class ThingService {



	@Autowired
	private ThingIFService thingService;


	@Autowired
	private TokenBindToolResolver resolver;



	public MqttEndPoint  onBoardingByThingID(String thingID, String pwd){

		OnBoardingParam param=new OnBoardingParam();
		param.setThingID(thingID);
		param.setThingPassword(pwd);

		OnBoardingResult result= thingService.thingOnBoarding(param);

		resolver.bindThing(result.getAccessToken());

		return result.getMqttEndpoint();
	}

	public MqttEndPoint  onBoarding(String thingID){

		OnBoardingParam param=new OnBoardingParam();
		param.setThingID(thingID);

		OnBoardingResult result= thingService.onBoarding(param);

		resolver.bindThing(result.getAccessToken());
		return result.getMqttEndpoint();
	}

	public void sendCommandResponse(@AppBindParam String appID,String thingID, String commandID,List<Map<String,ActionResult>> results){


		thingService.submitActionResult(thingID,commandID,results);
	}

	public void setStatus( @AppBindParam String appID,String thingID, ThingStatus status){

		thingService.putStatus(thingID,status);
	}


}
