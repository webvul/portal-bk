package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.entity.ThingInput;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/onboardinghelper",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class OnboardingHelperController {

    @Autowired
    private ThingManager thingManager;

    /**
     * 创建更新设备信息
     * POST /onboardinghelper
     *
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param input
     */
    @RequestMapping(path="/",method={RequestMethod.POST})
    public Map<String,String> setOnboardingInfo(@RequestBody ThingInput input){

        if(input == null){
            throw new PortalException("RequiredFieldsMissing","not data input", HttpStatus.BAD_REQUEST);//no body
        }

        if(Strings.isBlank(input.getVendorThingID())){
            throw new PortalException("RequiredFieldsMissing","vendorThingID cannot been null", HttpStatus.BAD_REQUEST);//paramter missing
        }

        if(Strings.isBlank(input.getKiiAppID())){
            throw new PortalException("RequiredFieldsMissing","KiiAppID cannot been null", HttpStatus.BAD_REQUEST);//paramter missing
        }

        if(Strings.isBlank(input.getPassword())){
            throw new PortalException("RequiredFieldsMissing","password cannot been null", HttpStatus.BAD_REQUEST);//paramter missing
        }

        GlobalThingInfo thingInfo = new GlobalThingInfo();
        thingInfo.setVendorThingID(input.getVendorThingID());
        thingInfo.setGlobalThingID(input.getGlobalThingID());
        thingInfo.setKiiAppID(input.getKiiAppID());
        thingInfo.setPassword(input.getPassword());
        thingInfo.setType(input.getType());
        thingInfo.setCustom(input.getCustom());
        thingInfo.setStatus(input.getStatus());

        String globalThingID = thingManager.createThing(thingInfo,input.getTags());

        Map<String,String> map=new HashMap<>();
        map.put("globalThingID", globalThingID);
        return map;
    }

    /**
     * 查询设备（vendorThingID）
     * GET /onboardinghelper/{vendorThingID}
     *
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param vendorThingID
     */
    @RequestMapping(path="/{vendorThingID}",method={RequestMethod.GET})
    public ResponseEntity<GlobalThingInfo> getOnboardingInfo(@PathVariable("vendorThingID") String vendorThingID){

        // 1.	根据URL参数vendorThingID查询table GlobalThing并返回相应的设备信息
        //        ●	如果table GlobalThing中此vendorThingID不存在，返回相应的错误信息
        GlobalThingInfo globalThingInfo = thingManager.findThingByVendorThingID(vendorThingID);

        if(globalThingInfo == null) {
            throw new PortalException("DataNotFound", "vendorThingID not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(globalThingInfo, HttpStatus.OK);
    }

}
