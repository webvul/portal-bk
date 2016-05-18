package com.kii.beehive.portal.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.manager.IndustryTemplateManager;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * this class provides the web url access to industry template related functions
 */
@RestController
@RequestMapping(path = "/industrytemplate", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class IndustryTemplateController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IndustryTemplateManager industryTemplateManager;

    /**
     * get industry template by below url params:
     * - thing type
     * - industry template name
     * - industry template version
     *
     * @param thingType
     * @param name
     * @param version
     * @return
     * @throws IOException
     */
    @RequestMapping(path = "", method = {RequestMethod.GET})
    public Map<String, Object> query(@RequestParam("thingType") String thingType, @RequestParam("name") String name, @RequestParam("version") String version) throws IOException {

        List<Map<String, Object>> list = industryTemplateManager.getIndustryTemplate(thingType, name, version);

        Map<String, Object> map = CollectUtils.getFirst(list);
        if(map == null) {
            return null;
        }

        String strContent = (String)map.get("content");
        Map<String, Object> result = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);

        return result;
    }

    /**
     * add industry template, below fields are required in the json request body:
     * - thing type
     * - industry template name
     * - industry template version
     * - content : for the format, refer to "./web-mvc/src/main/resources/com/kii/demohelper/web/industrytemplate/demo.json"
     *
     * @param requestBody
     * @throws JsonProcessingException
     */
    @RequestMapping(path = "", method = {RequestMethod.POST})
    public Map<String, Object> insert(@RequestBody Map<String, Object> requestBody) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");

        String thingType = (String)requestBody.get("thingType");
        String name = (String)requestBody.get("name");
        String version = (String)requestBody.get("version");
        Map<String, Object> content = (Map<String, Object>)requestBody.get("content");

        if(Strings.isBlank(thingType) || Strings.isBlank(name) || Strings.isBlank(version) || content == null) {
            throw new PortalException(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST);
        }

        String strContent = objectMapper.writeValueAsString(content);

        industryTemplateManager.insertIndustryTemplate(thingType, name, version, strContent);
        return result;
    }

    /**
     * get industry template sample, the industry template sample is as below:
     * - thing type : demoThingType
     * - industry template name : demoName
     * - industry template version : demoVer
     * - content : refer to "./web-mvc/src/main/resources/com/kii/demohelper/web/industrytemplate/demo.json"
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(path = "/sample", method = {RequestMethod.GET})
    public Map<String, Object> querySample() throws IOException {

        List<Map<String, Object>> list = industryTemplateManager.getIndustryTemplate("demoThingType", "demoName", "demoVer");

        Map<String, Object> map = CollectUtils.getFirst(list);
        if(map == null) {
            return null;
        }

        String strContent = (String)map.get("content");
        Map<String, Object> result = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);

        return result;
    }

}