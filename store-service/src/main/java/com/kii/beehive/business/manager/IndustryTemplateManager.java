package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;
import com.kii.beehive.portal.jdbc.entity.map.IndustryTemplateMap;

/**
 * this class provides the industry template related functions
 */
@Component
public class IndustryTemplateManager {

    @Autowired
    private IndustryTemplateDao dao;

    /**
     * get industry template
     *
     * @param thingType
     * @param name
     * @param verison
     * @return
     */
    public List<IndustryTemplate> getIndustryTemplate(String thingType, String name, String verison) {

        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        if(!Strings.isBlank(thingType)) {
            fields.add(IndustryTemplate.THING_TYPE);
            values.add(thingType);
        }

        if(!Strings.isBlank(name)) {
            fields.add(IndustryTemplate.NAME);
            values.add(name);
        }

        if(!Strings.isBlank(verison)) {
            fields.add(IndustryTemplate.VERSION);
            values.add(verison);
        }

        return dao.findByField(fields, values);

    }

    /**
     * add industry template
     *
     */
    public void insertIndustryTemplate(IndustryTemplate industryTemplate) {

        String[] fields = new String[] {
                IndustryTemplateMap.THING_TYPE,
                IndustryTemplateMap.NAME,
                IndustryTemplateMap.VERSION,
                IndustryTemplateMap.CONTENT
        };

        dao.insert(industryTemplate);

    }

}
