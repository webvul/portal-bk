package com.kii.beehive.portal.web.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kii.beehive.business.event.BusinessEventBus;
import com.kii.beehive.portal.manager.TagThingManager;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * Beehive API - Thing API
 *
 * refer to doc "Beehive API - Tech Design" section "Thing API" for details
 */
@RestController
@RequestMapping(path = "/tags", consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class TagController {

	@Autowired
	private TagIndexDao tagIndexDao;

	@Autowired
	private TagThingManager thingTagManager;

	@Autowired
	private BusinessEventBus eventBus;


	/**
	 * @deprecated this method should not be exposed to external;<br>
	 *     if required to get all tags, "查询tag" API should be used to inquiry all the tags under certain tag type
	 *
	 * 列出所有tag
	 * GET /tags/all
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/all", method = { RequestMethod.GET })
	public List<TagIndex> getAllTag() {
		List<TagIndex> list = tagIndexDao.findAll();
		return list;
	}

	/**
	 * 创建tag
	 * POST /tags/custom
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section
	 * "Create/Update Tag (创建/更新tag)" for more details
	 *
	 */
	@RequestMapping(path = "/custom", method = { RequestMethod.POST })
	public Map<String, Object> createTag(@RequestBody TagIndex tag) {

		if (Strings.isBlank(tag.getDisplayName())) {
			throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING, "DisplayName is empty",
					HttpStatus.BAD_REQUEST);
		}

		tag.setTagType(TagType.Custom);
		List<TagIndex> tagList = tagIndexDao.findTagByTagTypeAndName(tag.getTagType().name(), tag.getDisplayName());
		if(tagList.size() > 0){//update
			TagIndex old = tagList.get(0);
			old.setDescription(tag.getDescription());
			tag = old;
		}

		long tagID = tagIndexDao.saveOrUpdate(tag);
		Map<String, Object> map = new HashMap<>();
		map.put("id", tagID);
		map.put("tagName", TagType.Custom.getTagName(tag.getDisplayName()));
		return map;
	}

	/**
	 * 移除tag
	 * DELETE /tags/custom/{displayName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section "Delete Tag (移除tag)" for more
	 * details
	 *
	 */
	@RequestMapping(path = "/custom/{displayName}", method = { RequestMethod.DELETE }, consumes = { "*" })
	public void removeTag(@PathVariable("displayName") String displayName) {

		if (Strings.isBlank(displayName)) {
			throw new PortalException("RequiredFieldsMissing", "displayName is empty", HttpStatus.BAD_REQUEST);
		}

		List<TagIndex> orig = tagIndexDao.findTagByTagTypeAndName(TagType.Custom.toString(), displayName);
		
		if(orig.size() == 0){
			throw new PortalException("Tag Not Found", "Tag with displayName:" + displayName + " Not Found", HttpStatus.NOT_FOUND);
		}

		thingTagManager.removeTag(orig.get(0));

//		eventBus.onTagChangeFire();
	}

	/**
	 * 查询tag
	 * GET /tags/search?tagType={tagType}&displayName={displayName}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details refer
	 * to doc "Tech Design - Beehive API", section "Inquire Tag (查询tag)" for
	 * more details
	 *
	 * @return
	 */
	@RequestMapping(path = "/search", method = { RequestMethod.GET })
	public List<TagIndex> findTags(@RequestParam(value="tagType", required = false) String tagType,
										@RequestParam(value="displayName", required = false) String displayName) {
		
		List<TagIndex> list = tagIndexDao.findTagByTagTypeAndName(StringUtils.capitalize(tagType), displayName);
		return list;

	}

	/*@RequestMapping(path = "/{tagName}/operation/{operation}", method = { RequestMethod.GET })
	public List<GlobalThingInfo> getThingsByTagExpress(@PathVariable("tagName") String tagName,
			@PathVariable("operation") String operation) {

		List<GlobalThingInfo> list = this.thingManager.findThingByTagName(tagName.split(","), operation);

		return list;
	}*/

	/**
	 * 查询位置信息
	 * GET /tags/locations/{parentLocation}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/locations/{parentLocation}", method = { RequestMethod.GET }, consumes = { "*" })
	public ResponseEntity<List<String>> findLocations(@PathVariable("parentLocation") String parentLocation) {

		List<String> locations = thingTagManager.findLocations(parentLocation);

		return new ResponseEntity<>(locations, HttpStatus.OK);
	}

	/**
	 * 查询位置信息(所有)
	 * GET /tags/locations/{parentLocation}
	 *
	 * refer to doc "Beehive API - Thing API" for request/response details
	 *
	 * @return
	 */
	@RequestMapping(path = "/locations", method = { RequestMethod.GET }, consumes = { "*" })
	public ResponseEntity<List<String>> findAllLocations() {
		return findLocations("");
	}

}
