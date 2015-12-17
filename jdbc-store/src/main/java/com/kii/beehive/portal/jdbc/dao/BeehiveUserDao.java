package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.exception.StoreException;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;

@Repository
public class BeehiveUserDao extends BaseDao<BeehiveUser> {


    public static final String TABLE_NAME = "beehive_user";


	@Override
	protected Class<BeehiveUser> getEntityCls() {
		return BeehiveUser.class;
	}

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
            beehiveUser.setId((Integer)row.get(BeehiveUser.USER_ID));
            beehiveUser.setKiiUserID((String)row.get(BeehiveUser.KII_USER_ID));
            beehiveUser.setKiiLoginName((String)row.get(BeehiveUser.KII_LOGIN_NAME));
            beehiveUser.setUserName((String)row.get(BeehiveUser.USER_NAME));
            beehiveUser.setPhone((String)row.get(BeehiveUser.PHONE));
            beehiveUser.setMail((String)row.get(BeehiveUser.MAIL));
            beehiveUser.setCompany((String)row.get(BeehiveUser.COMPANY));
            beehiveUser.setRole((String)row.get(BeehiveUser.ROLE));


            // TODO need to parse string to map
//            String customProps = (String)row.get(BeehiveUser.CUSTOM);
//            mapper.readValue(customProps, Map.class);
//            beehiveUser.setCustomFields();

            mapToListForDBEntity(beehiveUser, row);

            list.add(beehiveUser);
        }
        return list;
    }


    public long createUser(BeehiveUser user) {

        long userID = super.saveOrUpdate(user);
        user.setId(userID);

        return userID;
    }


    public void updateUser(BeehiveUser user, long userID) {

        boolean isExist = super.IsIdExist(userID);
        if (isExist) {
            super.saveOrUpdate(user);
        } else {
            throw new StoreException(userID + "doesn't exist");
        }
    }


	@Override
	public long update(BeehiveUser entity) {
		// TODO Auto-generated method stub
		return 0;
	}

}
