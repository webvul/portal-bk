package com.kii.thingif.virtualthing;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.context.TokenBindToolResolver;

@Component
public class VirtualThingService {


	@Autowired
	private AppBindToolResolver  appBindTool;


	@Autowired
	private GlobalThingSpringDao thingDao;

	@Autowired
	private IndustryTemplateDao  templateDao;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ThingService  thService;

	@Autowired
	private TokenBindToolResolver bindToolResolver;

	@Autowired
	private MqttConnectPool mqttTool;

	@Autowired
	private ThingInfoStore store;


	public void addThing(long  thingID,String password) throws IOException {

		GlobalThingInfo thingInfo=thingDao.findByID(thingID);

		ThingInfoStore.ThingInfo info=new ThingInfoStore.ThingInfo();

		info.setThingInfo(thingInfo);
		String template=templateDao.getTemplateByThingID(thingID).getContent();

		ThingSchema schema=mapper.readValue(template,ThingSchema.class);

		info.setThingSchema(schema);

		if(StringUtils.isBlank(password)){

			info.setMqttEndPoint(thService.onBoardingByThingID(thingInfo.getKiiThingID(),password));
		}else{
			info.setMqttEndPoint(thService.onBoarding(thingInfo.getKiiThingID()));
		}

		store.storeInfo(info);

		mqttTool.connectToMQTT(info.getThingID(),info.getMqttEndPoint());

	}



}
