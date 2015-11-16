package com.kii.beehive.portal.web.controller;

import java.util.Date;
import java.util.List;

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
	 * 查询设备
	 * GET /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @return
	 */
	@RequestMapping(path = "/{globalThingID}", method = {RequestMethod.GET})
	public ResponseEntity<GlobalThingInfo> getThingByGlobalID(@PathVariable("globalThingID") String globalThingID) {
		
		GlobalThingInfo thing =  globalThingDao.getThingInfoByID(globalThingID);
		return new ResponseEntity<>(thing, HttpStatus.OK);
	}

	/**
	 * 列出所有设备
	 * GET /things/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
     */
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public ResponseEntity<List<GlobalThingInfo>> getAllThing(){
		List<GlobalThingInfo> list =  globalThingDao.getAllThing();
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	/**
	 * 创建/更新设备信息
	 * POST /things/
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param input
     */
	@RequestMapping(path="/",method={RequestMethod.POST})
	public ResponseEntity<String> createThing(@RequestBody ThingInput input){
		if(input == null){
			throw new PortalException();//no body
		}
		
		if(Strings.isBlank(input.getVendorThingID())){
			throw new PortalException();//paramter missing
		}
		
		if(Strings.isBlank(input.getKiiAppID())){
			throw new PortalException();//paramter missing
		}
		
		GlobalThingInfo thingInfo = new GlobalThingInfo();
		thingInfo.setVendorThingID(input.getVendorThingID());
		thingInfo.setGlobalThingID(input.getGlobalThingID());
		thingInfo.setKiiAppID(input.getKiiAppID());
		thingInfo.setType(input.getType());
		thingInfo.setStatus(input.getStatus());
		thingInfo.setStatusUpdatetime(new Date());
		
		thingManager.createThing(thingInfo,input.getTags());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 移除设备
	 * DELETE /things/{globalThingID}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
     */
	@RequestMapping(path="/{globalThingID}",method={RequestMethod.DELETE})
	public ResponseEntity<String> removeThing(@PathVariable("globalThingID") String globalThingID){
		
		GlobalThingInfo orig =  globalThingDao.getThingInfoByID(globalThingID);
		
		if(orig == null){
			throw new PortalException();//not found object
		}
		
		globalThingDao.removeGlobalThingByID(orig.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 绑定设备及tag
	 * PUT /things/{globalThingID...}/tags/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/{tagName}",method={RequestMethod.PUT})
	public ResponseEntity<String> addThingTag(@PathVariable("globalThingID") String globalThingID,@PathVariable("tagName") String tagName){
		
		String[] thingIDs = globalThingID.split(",");
		String[] tagIDs = tagName.split(",");
		thingManager.bindTagToThing(tagIDs, thingIDs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 解除绑定设备及tag
	 * DELETE /things/{globalThingID}/tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param globalThingID
	 * @param tagName
     */
	@RequestMapping(path="/{globalThingID}/tags/{tagName}/",method={RequestMethod.DELETE})
	public ResponseEntity<String> removeThingTag(@PathVariable("globalThingID") String globalThingID,@PathVariable("tagName") String tagName){
		thingManager.unbindTagToThing(tagName,globalThingID);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 查询tag下的设备
	 * GET /things/tag/{tagName...}/operation/{operation}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @param tagName
	 * @param operation
     * @return
     */
	@RequestMapping(path = "/tag/{tagName}/operation/{operation}", method = {RequestMethod.GET})
	public ResponseEntity<List<GlobalThingInfo>> getThingsByTag(@PathVariable("tagName") String tagName, @PathVariable("operation") String operation) {
		
		List<GlobalThingInfo> list = this.thingManager.findThingByTagName(tagName.split(","), operation);
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}