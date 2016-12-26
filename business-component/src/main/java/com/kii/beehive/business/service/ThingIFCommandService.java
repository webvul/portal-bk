package com.kii.beehive.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kii.beehive.business.entity.ExecuteTarget;
import com.kii.beehive.business.entity.TagSelector;
import com.kii.beehive.business.entity.TargetAction;
import com.kii.beehive.business.manager.AppInfoManager;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.service.thing.CommandsDao;
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
    private CommandsDao commandDetailDao;

    @Autowired
    private GlobalThingSpringDao globalThingDao;


    /**
     * send command to thing list or tag list
     * @param targets
     * @param userID
     * @return list of map between thing id and command id
     */
    public List<Map<Long, String>> doCommand(List<ExecuteTarget> targets, String userID) {

        List<Map<Long, String>> commandIDList = new ArrayList<>();

        targets.forEach(target->{

            Map<Long, String> commandIDMap = doCommand(target, userID);
            commandIDList.add(commandIDMap);

        });

        return commandIDList;
    }

    /**
     * send command to thing list or tag list
     * @param target
     * @param userID
     * @return map between thing id and command id
     */
    public Map<Long, String> doCommand(ExecuteTarget target, String userID) {

        Map<Long, String> commandIDMap = new HashMap<>();

        TargetAction action=target.getCommand();

        Set<GlobalThingInfo> thingList = this.getThingInfos(target.getSelector());

//        String thingType = task.getThingType();

        for (GlobalThingInfo thing : thingList) {

            ThingCommand command = action.getCommand();

            // skip empty command
            if(command == null) {
                log.debug("empty command of thing: " + thing);
                continue;
            }

            // skip thing without onboarding
            if(Strings.isBlank(thing.getFullKiiThingID())) {
                log.debug("non-boarded thing: " + thing);
                continue;
            }

            // send command
            String commandID = sendCmd(command, thing, userID);

            // add command id to map
            commandIDMap.put(thing.getId(), commandID);
        }

        return commandIDMap;
    }

    private String sendCmd(ThingCommand command, GlobalThingInfo thingInfo, String userID) {

        String appID=thingInfo.getKiiAppID();
        command.setUserID(appInfoManager.getDefaultOwer(appID).getUserID());

        return thingIFService.sendCommand(command,thingInfo.getFullKiiThingID());
    }

    public List<ThingCommand> queryCommand(GlobalThingInfo thingInfo, Long startDateTime, Long endDateTime) {

        String kiiAppID = thingInfo.getKiiAppID();
        String kiiThingID = thingInfo.getKiiThingID();

        List<ThingCommand> list = commandDetailDao.queryCommand(kiiAppID, kiiThingID, startDateTime, endDateTime);

        return list.stream().map((e) -> (ThingCommand)e).collect(Collectors.toList());

    }

    public ThingCommand readCommand(GlobalThingInfo thingInfo, String commandID) {

        return thingIFService.readCommand(thingInfo.getFullKiiThingID(), commandID);

    }


    private Set<GlobalThingInfo> getThingInfos(TagSelector source) {
        Set<GlobalThingInfo> things = new HashSet<>();

        if(!source.getThingList().isEmpty()) {
            things.addAll(globalThingDao.findByIDs(source.getThingList()));
            //carlos
            //可以同时 包括 thing list 和 tag list
//			return things;
        }

        if(!source.getTagList().isEmpty()) {
            if(StringUtils.isEmpty(source.getType())) {

                if (source.isAndExpress()) {
                    things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
                } else {
                    things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
                }
            }else{

                if (source.isAndExpress()) {
                    things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList(),source.getType()));
                } else {
                    things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList(),source.getType()));
                }
            }
        }
        return things;
    }

}
