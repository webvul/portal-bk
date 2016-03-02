package com.kii.beehive.portal.jdbc.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.AuthInfo;

@Repository
public class AuthInfoDao extends SpringBaseDao<AuthInfo> {

    private Logger log= LoggerFactory.getLogger(AuthInfoDao.class);

    public static final String TABLE_NAME = "auth_info";
    public static final String KEY = AuthInfo.ID;


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public void deleteByUserID(String userID) {
        String sql = "DELETE FROM " + this.getTableName() + " WHERE " + AuthInfo.USER_ID + "=?";

        jdbcTemplate.update(sql, userID);
    }

}

