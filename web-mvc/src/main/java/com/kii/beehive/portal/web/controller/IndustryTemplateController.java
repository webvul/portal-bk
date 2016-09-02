package com.kii.beehive.portal.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kii.beehive.portal.web.exception.ErrorCode;
import com.kii.beehive.portal.web.entity.IndustryTemplateRestBean;
import com.kii.beehive.portal.web.exception.PortalException;

/**
 * this class provides the web url access to industry template related functions
 */
@RestController
@RequestMapping(path = "/schema", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class IndustryTemplateController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IndustryTemplateManager industryTemplateManager;


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
    @RequestMapping(path = "/manage/{schemaType}", method = {RequestMethod.POST})
    public Map<String, Object> insert(@PathVariable IndustryTemplate.SchemaType schemaType,
                                      @RequestBody IndustryTemplateRestBean industryTemplateRestBean) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        industryTemplateRestBean.getIndustryTemplate().setSchemaType( schemaType );
        industryTemplateRestBean.verifyInput();
        // check DUPLICATE_OBJECT
        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate(
                industryTemplateRestBean.getIndustryTemplate().getSchemaType(),
                industryTemplateRestBean.getIndustryTemplate().getThingType(),
                industryTemplateRestBean.getIndustryTemplate().getName(),
                industryTemplateRestBean.getIndustryTemplate().getVersion());

        IndustryTemplate industryTemplate = CollectUtils.getFirst(list);
        if(industryTemplate != null) {
            throw new PortalException(ErrorCode.DUPLICATE_OBJECT,"type","industryTemplate","objectID",industryTemplateRestBean.getIndustryTemplate().getName());
        }
        //
        String strContent = objectMapper.writeValueAsString(industryTemplateRestBean.getContent());
        industryTemplateRestBean.getIndustryTemplate().setContent(strContent);

        industryTemplateManager.insertIndustryTemplate(industryTemplateRestBean.getIndustryTemplate());
        return result;
    }

    @RequestMapping(path = "/manage/{schemaType}/{id}", method = {RequestMethod.PUT})
    public Map<String, Object> update(@PathVariable IndustryTemplate.SchemaType schemaType,
                                      @PathVariable Long id,
                                      @RequestBody IndustryTemplateRestBean industryTemplateRestBean) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        IndustryTemplate oldIndustryTemplate = industryTemplateManager.findByID(id);
        if( oldIndustryTemplate == null) {
            throw new PortalException(ErrorCode.NOT_FOUND,"type","industryTemplate","objectID",String.valueOf(id));
        }
        industryTemplateRestBean.verifyInput();
        /*//check
        Map<String, Object> contentMap = industryTemplateRestBean.getContent();
        if(  ! ( oldIndustryTemplate.getName().equals(contentMap.get("name"))
                && oldIndustryTemplate.getThingType().equals(contentMap.get("thingType"))
                && oldIndustryTemplate.getVersion().equals(contentMap.get("version")) )
                ){
            throw new PortalException(ErrorCode.REQUIRED_FIELDS_MISSING,"field","industryTemplate");
        }*/
        //
        String strContent = objectMapper.writeValueAsString(industryTemplateRestBean.getContent());
        oldIndustryTemplate.setContent(strContent);

        industryTemplateManager.updateIndustryTemplate(oldIndustryTemplate);
        return result;
    }


    @RequestMapping(path = "/query/{schemaType}", method = {RequestMethod.GET}, consumes = { "*" })
    public IndustryTemplateRestBean query(@PathVariable IndustryTemplate.SchemaType schemaType,
                                          @RequestParam("thingType") String thingType,
                                          @RequestParam("name") String name,
                                          @RequestParam("version") String version) throws IOException {

        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate( schemaType, thingType, name, version);

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

    @RequestMapping(path = "/query/{schemaType}/list", method = {RequestMethod.GET}, consumes = { "*" })
    public List<IndustryTemplateRestBean> queryList(@PathVariable IndustryTemplate.SchemaType schemaType,
                                                    @RequestParam("thingType") String thingType,
                                                    @RequestParam("name") String name) throws IOException {

        List<IndustryTemplate> list = industryTemplateManager.getIndustryTemplate( schemaType, thingType, name, null );
        List<IndustryTemplateRestBean> restBeanList = new ArrayList<>();
        list.forEach(industryTemplate -> {
            String strContent = industryTemplate.getContent();
            Map<String, Object> content = null;
            try {
                content = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            IndustryTemplateRestBean restBean = new IndustryTemplateRestBean();
            restBean.setIndustryTemplate(industryTemplate);
            restBean.setContent(content);
            restBeanList.add(restBean);
        });



        return restBeanList;
    }

    @RequestMapping(path = "/query/{schemaType}/{id}", method = {RequestMethod.GET}, consumes = { "*" })
    public IndustryTemplateRestBean getById(@PathVariable IndustryTemplate.SchemaType schemaType,
                                            @PathVariable("id") Long id) throws IOException {

        IndustryTemplate industryTemplate = industryTemplateManager.findByID(id);
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


    @RequestMapping(path = "/queryMaxVersion", method = {RequestMethod.GET}, consumes = { "*" })
    public List<IndustryTemplateRestBean> getMaxVersionList() throws IOException {

        List<IndustryTemplate> list = industryTemplateManager.getMaxVersionList();
        List<IndustryTemplateRestBean> restBeanList = new ArrayList<>();
        list.forEach(industryTemplate -> {
            String strContent = industryTemplate.getContent();
            Map<String, Object> content = null;
            try {
                content = (Map<String, Object>)objectMapper.readValue(strContent, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            IndustryTemplateRestBean restBean = new IndustryTemplateRestBean();
            restBean.setIndustryTemplate(industryTemplate);
            restBean.setContent(content);
            restBeanList.add(restBean);
        });

        return restBeanList;
    }


}