package com.kii.beehive.portal.jdbc.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kii.beehive.portal.jdbc.entity.TagType;


public class LocationTagIndexDao extends TagIndexDao {

    public List<String> findLocation(String parentLocation) {

        String sql = "SELECT display_name FROM " + this.getTableName()
                + " WHERE tag_type='" + TagType.Location + "' AND display_name like '?%'";

        Object[] params = new String[] {parentLocation};
        List<String> rows = jdbcTemplate.queryForList(sql, params, String.class);

        return rows;
    }

}
