package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.service.ThingTagService;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.help.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Tech Design - Beehive API" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class TagController {

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private ThingTagService thingTagService;

	/**
	 * 列出所有tag GET /tags/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */

	/*@RequestMapping(path = "/all", method = { RequestMethod.GET })
	public List<TagIndex> getAllTag() {
		List<TagIndex> list = tagIndexDao.findAll();
		return list;
	}*/

	/**
	 * 创建tag POST /tags/custom
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section
	 * "Create/Update Tag (创建/更新tag)" for more details
	 *
	 */
	@RequestMapping(path = "/custom", method = { RequestMethod.POST })
	public Map<String, Long> createTag(@RequestBody TagIndex tag) {

		if (!StringUtils.hasText(tag.getDisplayName())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "DisplayName is empty",
					HttpStatus.BAD_REQUEST);
		}

		tag.setTagType(TagType.Custom);

		long tagID = tagIndexDao.saveOrUpdate(tag);
		Map<String, Long> map = new HashMap<>();
		map.put("id", tagID);
		return map;
	}

	/**
	 * 移除tag DELETE /tags/{tagName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section "Delete Tag (移除tag)" for more
	 * details
	 *
	 * @param tagName
	 */
	@RequestMapping(path = "/custom/{displayName}", method = { RequestMethod.DELETE }, consumes = { "*" })
	public void removeTag(@PathVariable("displayName") String displayName) {

		if (Strings.isBlank(displayName)) {
			throw new PortalException("RequiredFieldsMissing", "tagName is empty", HttpStatus.BAD_REQUEST);
		}

		List<TagIndex> orig = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), displayName);
		
		if(orig.size() == 0){
			throw new PortalException("no body", "no body", HttpStatus.BAD_REQUEST);
		}

		thingTagService.removeTag(orig.get(0));
	}

	/**
	 * 查询tag GET /tags/tag/{tagName ...}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section "Inquire Tag (查询tag)" for
	 * more details
	 *
	 * @param tagName
	 * @return
	 */

	/*@RequestMapping(path = "/{tagName}", method = { RequestMethod.GET })
	public List<TagIndex> getThingsByTagArray(@PathVariable("tagName") String tagName) {

		List<TagIndex> list = tagIndexDao.findTagIndexByTagNameArray(tagName.split(","));
		return list;

	}*/

	@RequestMapping(path = "/{type}/{displayName}", method = { RequestMethod.GET })
	public List<TagIndex> getThingsByTag(@PathVariable("type") String type, @PathVariable("displayName") String displayName) {
		if(type.equals("*")){
			type = null;
		}
		
		if(displayName.equals("*")){
			displayName = null;
		}
		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(StringUtils.capitalize(type), displayName);
		return list;

	}

	/*@RequestMapping(path = "/{tagName}/operation/{operation}", method = { RequestMethod.GET })
	public List<GlobalThingInfo> getThingsByTagExpress(@PathVariable("tagName") String tagName,
			@PathVariable("operation") String operation) {

		List<GlobalThingInfo> list = this.thingManager.findThingByTagName(tagName.split(","), operation);

		return list;
	}*/

}
