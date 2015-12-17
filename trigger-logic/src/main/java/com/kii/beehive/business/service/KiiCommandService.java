package com.kii.beehive.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.AppInfoDao;
import com.kii.beehive.portal.service.ThingTagService;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.service.ThingIFService;

@Component
public class KiiCommandService {


	@Autowired
	private ThingIFService  service;


	@Autowired
	private AppInfoDao appInfoDao;

	@Autowired
	private AppBindToolResolver resolver;

	@Autowired
	private ThingTagService thingTagService;

	public void sendCmdToThing(String globalThingID,ThingCommand command){


		GlobalThingInfo thingInfo=thingTagService.getThingByVendorThingID(globalThingID);

		sendCmd(command, thingInfo);

	}

	private void sendCmd(ThingCommand command, GlobalThingInfo thingInfo) {
		String appID=thingInfo.getKiiAppID();

		resolver.setAppInfoDirectly(appInfoDao.getAppInfoByID(appID).getAppInfo());

		service.sendCommand(thingInfo.getKiiThingID(),command);
	}

	public void sendCmdToTag(String  tagName,ThingCommand command){

		List<GlobalThingInfo>  thingList=thingTagService.getThingsByTag(tagName);

		thingList.forEach(thing->{
			sendCmd(command,thing);
		});


	}

	public void sendCmdToTagExpress(boolean isAnd, List<String>  tagCollect, ThingCommand command){


		List<GlobalThingInfo>  thingList=thingTagService.queryThingByTagExpress(isAnd,tagCollect);

		thingList.forEach(thing->{
			sendCmd(command,thing);
		});
	}
}
