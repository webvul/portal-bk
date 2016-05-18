package com.kii.beehive.portal.jdbc.dao.map;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class IndustryTemplateMapDao extends MapDao {

    @Autowired
    public void setDataSource(DataSource dataSourceBeehive) {

        super.setDataSource(dataSourceBeehive);
    }

}
