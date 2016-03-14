package com.kii.beehive.portal.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.service.ThingIFCommandService;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.web.entity.ThingCommandRestBean;
import com.kii.extension.ruleengine.store.trigger.ExecuteTarget;

@RestController
@RequestMapping(path = "/thing-if", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
        MediaType.APPLICATION_JSON_UTF8_VALUE })
public class ThingIFController {

    @Autowired
    private ThingIFCommandService thingIFCommandService;

    /**
     * send commands to thing list or tag list
     *
     * @param restBeanList
     * @return
     */
    @RequestMapping(path = "/command", method = { RequestMethod.POST })
    public List<List<Map<String, Object>>> sendCommand(@RequestBody List<ThingCommandRestBean> restBeanList) {

        // construct command request
        List<ExecuteTarget> targets = new ArrayList<>();
        for (ThingCommandRestBean restBean : restBeanList) {
            targets.add(restBean);
        }

        String userID = AuthInfoStore.getUserID();

        // send command request
        List<Map<Long, String>> commandResultList = thingIFCommandService.doCommand(targets, userID);

        // format command response
        List<List<Map<String, Object>>> responseList = new ArrayList<>();

        for(Map<Long, String> commandResult : commandResultList) {

            List<Map<String, Object>> subResponseList = new ArrayList<>();

            Set<Map.Entry<Long, String>> entrySet = commandResult.entrySet();
            for (Map.Entry<Long, String> entry : entrySet) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("globalThingID", entry.getKey());
                map.put("commandID", entry.getValue());

                subResponseList.add(map);
            }

            responseList.add(subResponseList);
        }

        return responseList;
    }


}
