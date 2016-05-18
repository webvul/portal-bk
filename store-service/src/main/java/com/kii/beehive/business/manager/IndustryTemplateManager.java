package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.jdbc.dao.map.IndustryTemplateMapDao;
import com.kii.beehive.portal.jdbc.entity.map.IndustryTemplateMap;

/**
 * this class provides the industry template related functions
 */
@Component
public class IndustryTemplateManager {

    @Autowired
    private IndustryTemplateMapDao dao;

    /**
     * get industry template
     *
     * @param thingType
     * @param name
     * @param verison
     * @return
     */
    public List<Map<String, Object>> getIndustryTemplate(String thingType, String name, String verison) {

        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        if(!Strings.isBlank(thingType)) {
            fields.add(IndustryTemplateMap.THING_TYPE);
            values.add(thingType);
        }

        if(!Strings.isBlank(name)) {
            fields.add(IndustryTemplateMap.NAME);
            values.add(name);
        }

        if(!Strings.isBlank(verison)) {
            fields.add(IndustryTemplateMap.VERSION);
            values.add(verison);
        }

        return dao.findByField(IndustryTemplateMap.TABLE_NAME, fields, values);

    }

    /**
     * add industry template
     *
     * @param thingType
     * @param name
     * @param verison
     * @param content
     */
    public void insertIndustryTemplate(String thingType, String name, String verison, String content) {

        String[] fields = new String[] {
                IndustryTemplateMap.THING_TYPE,
                IndustryTemplateMap.NAME,
                IndustryTemplateMap.VERSION,
                IndustryTemplateMap.CONTENT
        };

        Object[] values = new Object[] {
                thingType, name, verison, content
        };

        dao.insert(IndustryTemplateMap.TABLE_NAME, fields, values);

    }

}
