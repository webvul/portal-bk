package com.kii.beehive.portal.web.controller;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.entity.ThingInput;
import com.kii.beehive.portal.web.help.PortalException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
     * 设置onboarding信息
     * POST /onboardinghelper
     *
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param input
     */
    @RequestMapping(path="/",method={RequestMethod.POST})
    public ResponseEntity<String> setOnboardingInfo(@RequestBody ThingInput input){
        if(input == null){
            throw new PortalException();//no body
        }

        if(Strings.isBlank(input.getVendorThingID())){
            throw new PortalException();//paramter missing
        }

        if(Strings.isBlank(input.getKiiAppID())){
            throw new PortalException();//paramter missing
        }

        // get global thing ID
        String globalThingID = input.getGlobalThingID();
        if(globalThingID == null) {
            globalThingID = this.generateGlobalThingID(input);
        }

        GlobalThingInfo thingInfo = new GlobalThingInfo();
        thingInfo.setVendorThingID(input.getVendorThingID());
        thingInfo.setGlobalThingID(globalThingID);
        thingInfo.setKiiAppID(input.getKiiAppID());
        thingInfo.setType(input.getType());
        thingInfo.setStatus(input.getStatus());
        thingInfo.setStatusUpdatetime(new Date());

        thingManager.createThing(thingInfo,input.getTags());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 获取onboarding信息
     * GET /onboardinghelper/{vendorThingID}
     *
     * refer to doc "Beehive API - Thing API" for request/response details
     *
     * @param vendorThingID
     */
    @RequestMapping(path="/{vendorThingID}",method={RequestMethod.GET})
    public ResponseEntity<GlobalThingInfo> getOnboardingInfo(@PathVariable("vendorThingID") String vendorThingID){

        GlobalThingInfo globalThingInfo = thingManager.findThingByVendorThingID(vendorThingID);

        return new ResponseEntity<>(globalThingInfo, HttpStatus.OK);
    }

    /**
     * get the default owner ID of the thing
     * @return
     */
    private String getDefaultOwnerID(String vendorThingID) {
        // TODO

        return null;
    }

    private String generateGlobalThingID(ThingInput input) {

        String globalThingID = input.getKiiAppID() + input.getVendorThingID();

        return globalThingID;
    }

}
