package com.kii.beehive.business.service;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.business.manager.ThingTagManager;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.thing.CommandsDao;
import com.kii.beehive.portal.store.entity.thing.Commands;
import com.kii.extension.sdk.entity.thingif.CommandDetail;
import com.kii.extension.sdk.entity.thingif.ThingCommand;


/**
 * this class provides the function to send command to multiple things (or things under multiple tags)
 * most of the logic of this class is copied from CommandExecuteService.java
 */
@Component
public class ThingIFCommandService {

    private Logger log= LoggerFactory.getLogger(ThingIFCommandService.class);

    @Autowired
    private AppInfoManager appInfoManager;

    @Autowired
    private ThingIFInAppService thingIFService;

    @Autowired
    private ThingTagManager thingTagService;

    @Autowired
    private CommandsDao commandDetailDao;



    private String sendCmd(ThingCommand command, GlobalThingInfo thingInfo, String userID) {

        String appID=thingInfo.getKiiAppID();
        command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());

        return thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
    }

    public List<CommandDetail> queryCommand(GlobalThingInfo thingInfo, Long startDateTime, Long endDateTime) {

        String kiiAppID = thingInfo.getKiiAppID();
        String kiiThingID = thingInfo.getKiiThingID();

        List<Commands> list = commandDetailDao.queryCommand(kiiAppID, kiiThingID, startDateTime, endDateTime);

        return list.stream().map((e) -> (CommandDetail)e).collect(Collectors.toList());

    }

    public CommandDetail readCommand(GlobalThingInfo thingInfo, String commandID) {

        return thingIFService.readCommand(thingInfo.getFullKiiThingID(), commandID);

    }

}
