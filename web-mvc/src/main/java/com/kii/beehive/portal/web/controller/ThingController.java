package com.kii.beehive.portal.web.controller;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.GlobalThingDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.web.entity.ThingInput;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path="/things",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class ThingController {


	@Autowired
	private ThingManager thingManager;
	
	@Autowired
	private GlobalThingDao globalThingDao;

	/**
	 * Beehive API - Thing API
	 * 查询设备
	 * GET /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @return
	 */
	@RequestMapping(path = "/{globalThingID}", method = {RequestMethod.GET})
	public GlobalThingInfo getThingByGlobalID(@PathVariable("globalThingID") String globalThingID) {
		// TODO

		return null;
	}

	/**
	 * Beehive API - Thing API
	 * 列出所有设备
	 * GET /things/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
     */
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<GlobalThingInfo> getThing(){
		return globalThingDao.getAllThing();
	}

	/**
	 * Beehive API - Thing API
	 * 创建/更新设备信息
	 * POST /things/
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param input
     */
	@RequestMapping(path="/",method={RequestMethod.POST})
	public void createThing(@RequestBody ThingInput input){
		if(input == null){
			throw new PortalException();//no body
		}
		
		if(Strings.isBlank(input.getVendorThingID())){
			throw new PortalException();//paramter missing
		}
		
		if(Strings.isBlank(input.getGlobalThingID())){
			throw new PortalException();//paramter missing
		}
		
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID(input.getVendorThingID());
		thingInfo.setGlobalThingID(input.getGlobalThingID());
		thingInfo.setType(input.getType());
		thingInfo.setStatus(input.getStatus());
		thingInfo.setStatusUpdatetime(new Date());
		
		thingManager.createThing(thingInfo,input.getTags());
	}

	/**
	 * Beehive API - Thing API
	 * 移除设备
	 * DELETE /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
     */
	@RequestMapping(path="/{globalThingID}",method={RequestMethod.DELETE})
	public void removeThing(@PathVariable("globalThingID") String globalThingID){
		
		if(Strings.isBlank(globalThingID)){
			throw new PortalException();//paramter missing
		}
		
		GlobalThingInfo orig =  globalThingDao.getThingInfoByID(globalThingID);
		
		if(orig == null){
			throw new PortalException();//not found object
		}
		
		globalThingDao.removeGlobalThingByID(orig.getId());
	}

	/**
	 * Beehive API - Thing API
	 * 绑定设备及tag
	 * PUT /things/{globalThingID...}/tags/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/{tagName}",method={RequestMethod.PUT})
	public void addThingTag(@PathVariable("globalThingID") String globalThingID,@PathVariable("tagName") String tagName){

		if(globalThingID.indexOf(",") < 0 && tagName.indexOf(",") < 0) {
			// in the case of binding one thing and one tag
			thingManager.bindTagToThing(tagName,globalThingID);
		} else {
			// in the case of binding multiple things or multiple tags
			String[] thingIDs = globalThingID.split(",");
			String[] tagIDs = tagName.split(",");
			thingManager.bindTagToThing(tagIDs, thingIDs);
		}
	}

	/**
	 * Beehive API - Thing API
	 * 解除绑定设备及tag
	 * DELETE /things/{globalThingID}/tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/{tagName}/",method={RequestMethod.DELETE})
	public void removeThingTag(@PathVariable("globalThingID") String globalThingID,@PathVariable("tagName") String tagName){
		thingManager.unbindTagToThing(tagName,globalThingID);
	}

	/**
	 * Beehive API - Thing API
	 * 查询tag下的设备
	 * GET /things/tag/{tagName...}/{operation}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param tagName
	 * @param operation
     * @return
     */
	@RequestMapping(path = "/tag/{tagName}/{operation}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingsByTag(@PathVariable("tagName") String tagName, @PathVariable("operation") String operation) {
		if(Strings.isBlank(tagName)){
			throw new PortalException();//paramter missing
		}
		
		if(Strings.isBlank(operation)){
			throw new PortalException();//paramter missing
		}
		List<GlobalThingInfo> list = this.thingManager.findThingByTagName(tagName.split(","), operation);
		
		return list;
	}
}