package com.kii.beehive.portal.web.controller;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.manager.ThingManager;
import com.kii.beehive.portal.service.TagIndexDao;
import com.kii.beehive.portal.store.entity.GlobalThingInfo;
import com.kii.beehive.portal.store.entity.TagIndex;
import com.kii.beehive.portal.store.entity.TagType;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags",  consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class TagController {
	
	@Autowired
	private TagIndexDao tagIndexDao;
	
	@Autowired
	private ThingManager thingManager;
	/**
	 * 列出所有tag
	 * GET /tags/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
     */
	@RequestMapping(path="/all",method={RequestMethod.GET})
	public List<TagIndex> getAllTag(){
		List<TagIndex> list = tagIndexDao.getAllTag();
		return list;
	}

	/**
	 * 创建tag
	 * POST /tags
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Create/Update Tag (创建/更新tag)" for more details
	 *
	 * @param tagName
     */
	@RequestMapping(path="/custom/{tagName}",method={RequestMethod.PUT})
	public Map<String,String> createTag(@RequestBody TagIndex tag,@PathVariable("tagName") String tagName){

		tag.setDisplayName(tagName);
		tag.setTagType(TagType.Custom);

		String  id=tagIndexDao.addTagIndex(tag);
		Map<String,String> map=new HashMap<>();
		map.put("tagName",id);
		return map;
	}

	/**
	 * 移除tag
	 * DELETE /tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Delete Tag (移除tag)" for more details
	 *
	 * @param tagName
     */
	@RequestMapping(path="/custom/{tagName}",method={RequestMethod.DELETE})
	public void removeTag(@PathVariable("tagName") String tagName){
		
		TagIndex orig =  tagIndexDao.getTagIndexByID(TagType.Custom.getTagName(tagName));
		
		thingManager.removeTag(orig);
		return ;
	}

	/**
	 * 查询tag
	 * GET /tags/tag/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 * refer to doc "Tech Design - Beehive API", section "Inquire Tag (查询tag)" for more details
	 *
	 * @param tagName
	 * @return
     */

	@RequestMapping(path = "/{tagName}", method = {RequestMethod.GET})
	public List<TagIndex> getThingsByTagArray(@PathVariable("tagName") String tagName) {
		
		List<TagIndex> list = tagIndexDao.findTagIndexByTagNameArray(tagName.split(","));
		return list;

	}

	@RequestMapping(path = "/{type}/{tagName}", method = {RequestMethod.GET})
	public List<TagIndex> getThingsByTag(@PathVariable("type")String type,@PathVariable("tagName") String tagName) {

		String[] tags=tagName.split(",");
		TagType t=TagType.valueOf(StringUtils.capitalize(type));

		for(int i=0;i<tags.length;i++){
			tags[i]=t.getTagName(tags[i]);

		}
		List<TagIndex> list = tagIndexDao.findTagIndexByTagNameArray(tags);
		return list;

	}

	@RequestMapping(path = "/{tagName}/operation/{operation}", method = {RequestMethod.GET})
	public List<GlobalThingInfo> getThingsByTagExpress(@PathVariable("tagName") String tagName, @PathVariable("operation") String operation) {

		List<GlobalThingInfo> list = this.thingManager.findThingByTagName(tagName.split(","), operation);

		return list;
	}

}
