package com.kii.beehive.portal.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kii.beehive.business.manager.IndustryTemplateManager;
import com.kii.beehive.portal.common.utils.CollectUtils;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;
import com.kii.beehive.portal.web.constant.ErrorCode;
import com.kii.beehive.portal.web.entity.IndustryTemplateRestBean;
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
//    @RequestMapping(path = "", method = {RequestMethod.GET})
//    public Map<String, Object> query(@RequestParam("thingType") String thingType, @RequestParam("name") String name, @RequestParam("version") String version) throws IOException {
//
//        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate(thingType, name, version);
//
//        IndustryTemplate industryTemplate = CollectUtils.getFirst(list);
//        if(industryTemplate == null) {
//            return null;
//        }
//
//        String strContent = industryTemplate.getContent();
//        Map<String, Object> result = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);
//
//        return result;
//    }
    @RequestMapping(path = "", method = {RequestMethod.GET}, consumes = { "*" })
    public IndustryTemplateRestBean query(@RequestParam("thingType") String thingType, @RequestParam("name") String name, @RequestParam("version") String version) throws IOException {

        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate(thingType, name, version);

        IndustryTemplate industryTemplate = CollectUtils.getFirst(list);
        if(industryTemplate == null) {
            return null;
        }

        String strContent = industryTemplate.getContent();
        Map<String, Object> content = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);
        //
        IndustryTemplateRestBean restBean = new IndustryTemplateRestBean();
        restBean.setIndustryTemplate(industryTemplate);
        restBean.setContent(content);
        return restBean;
    }

    /**
     * add industry template, below fields are required in the json request body:
     * - thing type
     * - industry template name
     * - industry template version
     * - content : for the format, refer to "./web-mvc/src/main/resources/com/kii/demohelper/web/industrytemplate/demo.json"
     *
     * @param industryTemplateRestBean
     * @throws JsonProcessingException
     */
    @RequestMapping(path = "", method = {RequestMethod.POST})
    public Map<String, Object> insert(@RequestBody IndustryTemplateRestBean industryTemplateRestBean) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");

        industryTemplateRestBean.verifyInput();
        // check DUPLICATE_OBJECT
        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate(
                industryTemplateRestBean.getIndustryTemplate().getThingType(),
                industryTemplateRestBean.getIndustryTemplate().getName(),
                industryTemplateRestBean.getIndustryTemplate().getVersion());

        IndustryTemplate industryTemplate = CollectUtils.getFirst(list);
        if(industryTemplate != null) {
			PortalException excep= new PortalException(ErrorCode.DUPLICATE_OBJECT, HttpStatus.BAD_REQUEST);
			excep.addParam("type","industryTemplate");
			excep.addParam("objectID",industryTemplate.getName());
			throw excep;
        }
        //
        String strContent = objectMapper.writeValueAsString(industryTemplateRestBean.getContent());
        industryTemplateRestBean.getIndustryTemplate().setContent(strContent);

        industryTemplateManager.insertIndustryTemplate(industryTemplateRestBean.getIndustryTemplate());
        return result;
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT})
    public Map<String, Object> update(@PathVariable Long id, @RequestBody IndustryTemplateRestBean industryTemplateRestBean) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        IndustryTemplate oldIndustryTemplate = industryTemplateManager.findByID(id);
        if(id == null || oldIndustryTemplate == null) {
			PortalException excep= new PortalException(ErrorCode.NOT_FOUND, HttpStatus.BAD_REQUEST);
			excep.addParam("type","industryTemplate");
			excep.addParam("objectID",String.valueOf(id));
			throw excep;
        }
        industryTemplateRestBean.verifyInput();
        //
        String strContent = objectMapper.writeValueAsString(industryTemplateRestBean.getContent());
        oldIndustryTemplate.setContent(strContent);

        industryTemplateManager.updateIndustryTemplate(oldIndustryTemplate);
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
    @RequestMapping(path = "/sample", method = {RequestMethod.GET}, consumes = { "*" })
    public Map<String, Object> querySample() throws IOException {

        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate("demoThingType", "demoName",
                "demoVer");

        IndustryTemplate industryTemplate = CollectUtils.getFirst(list);
        if(industryTemplate == null) {
            return null;
        }

        String strContent = industryTemplate.getContent();
        Map<String, Object> result = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);

        return result;
    }

}