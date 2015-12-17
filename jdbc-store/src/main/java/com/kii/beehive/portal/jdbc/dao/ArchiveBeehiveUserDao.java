package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.ArchiveBeehiveUser;
import com.kii.beehive.portal.jdbc.entity.BeehiveUser;

@Repository
public class ArchiveBeehiveUserDao extends BaseDao<ArchiveBeehiveUser> {


    public static final String TABLE_NAME = "archive_beehive_user";

	@Override
	protected Class<ArchiveBeehiveUser> getEntityCls() {
		return ArchiveBeehiveUser.class;
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
    public List<ArchiveBeehiveUser> mapToList(List<Map<String, Object>> rows) {
        List<ArchiveBeehiveUser> list = new ArrayList<ArchiveBeehiveUser>();
        for (Map<String, Object> row : rows) {

            ArchiveBeehiveUser beehiveUser = new ArchiveBeehiveUser();
            beehiveUser.setId((long)row.get(BeehiveUser.USER_ID));
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


    public void archive(BeehiveUser user){

        // TODO

    }

    public BeehiveUser queryInArchive(BeehiveUser user){

        // TODO
        return null;
    }

    public void removeArchive(long userID){
        // TODO
    }


	@Override
	public long update(ArchiveBeehiveUser entity) {
		// TODO Auto-generated method stub
		return 0;
	}

}
