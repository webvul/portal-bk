package com.kii.beehive.business.manager;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;

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
     */
    public List<IndustryTemplate> getMaxVersionList() {
        return dao.getMaxVersionList();
    }
    public List<IndustryTemplate> getIndustryTemplate(IndustryTemplate.SchemaType schemaType, String thingType, String name, String verison) {

        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        if(!Strings.isBlank(thingType)) {
            fields.add(IndustryTemplate.SCHEMA_TYPE);
            values.add(schemaType.name());
        }
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
    public IndustryTemplate findByID(Long id) {
        return dao.findByID(id);
    }

    public void updateIndustryTemplate(IndustryTemplate industryTemplate) {
        dao.updateEntityAllByID(industryTemplate);
    }
    /**
     * add industry template
     *
     */
    public void insertIndustryTemplate(IndustryTemplate industryTemplate) {

        dao.insert(industryTemplate);

    }

}
