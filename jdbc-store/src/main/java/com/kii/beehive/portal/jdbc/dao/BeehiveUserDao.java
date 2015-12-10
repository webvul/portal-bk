package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;

@Repository
public class BeehiveUserDao extends BaseDao<BeehiveUser> {


    public static final String TABLE_NAME = "beehive_user";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    @Override
    public String getKey() {
        return BeehiveUser.USER_ID;
    }


    @Override
    public List<BeehiveUser> mapToList(List<Map<String, Object>> rows) {
        List<BeehiveUser> list = new ArrayList<BeehiveUser>();
        for (Map<String, Object> row : rows) {

            BeehiveUser beehiveUser = new BeehiveUser();
            beehiveUser.setUserID((String)row.get(BeehiveUser.USER_ID));
            beehiveUser.setKiiUserID((String)row.get(BeehiveUser.KII_USER_ID));
            beehiveUser.setKiiLoginName((String)row.get(BeehiveUser.KII_LOGIN_NAME));
            beehiveUser.setUserName((String)row.get(BeehiveUser.USER_NAME));
            beehiveUser.setPhone((String)row.get(BeehiveUser.PHONE));
            beehiveUser.setMail((String)row.get(BeehiveUser.MAIL));
            beehiveUser.setCompany((String)row.get(BeehiveUser.COMPANY));
            beehiveUser.setRole((String)row.get(BeehiveUser.ROLE));

            mapToListForDBEntity(beehiveUser, row);

            list.add(beehiveUser);
        }
        return list;
    }


    public String createUser(BeehiveUser user) {

        String userID = user.getUserID();
        boolean isExist = super.IsIdExist(userID);
        if (isExist) {
            throw new StoreException(userID);
        } else {
            super.saveOrUpdate(user);
        }

        return userID;
    }


    public void updateUser(BeehiveUser user, String userID) {

        boolean isExist = super.IsIdExist(userID);
        if (isExist) {
            super.saveOrUpdate(user);
        } else {
            throw new StoreException(userID);
        }
    }

}
