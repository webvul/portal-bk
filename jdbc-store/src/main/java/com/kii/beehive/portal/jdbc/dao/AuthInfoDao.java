package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.jdbc.entity.AuthInfo;

@Repository
public class AuthInfoDao extends BaseDao<AuthInfo> {

	private Logger log= LoggerFactory.getLogger(AuthInfoDao.class);
	
	public static final String TABLE_NAME = "auth_info";
	public static final String KEY = AuthInfo.ID;

	/**
	 * important:
	 * the return value of this method is not primary key but the update count
	 *
	 * @param userInfo
	 * @return
     */
	public long update(AuthInfo userInfo) {

		String[] columns = new String[]{
				AuthInfo.ID,
				AuthInfo.USER_ID,
				AuthInfo.TOKEN,
				AuthInfo.EXPIRE_TIME
		};

        return super.update(userInfo, columns);
    }


	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public List<AuthInfo> mapToList(List<Map<String, Object>> rows) {
		List<AuthInfo> list = new ArrayList<AuthInfo>();
		for (Map<String, Object> row : rows) {
			AuthInfo userInfo = new AuthInfo();
			userInfo.setId((Integer)row.get(AuthInfo.ID));
			userInfo.setUserID((String)row.get(AuthInfo.USER_ID));
			userInfo.setToken((String)row.get(AuthInfo.TOKEN));
			userInfo.setExpireTime((Date) row.get(AuthInfo.EXPIRE_TIME));
			mapToListForDBEntity(userInfo, row);
			list.add(userInfo);
		}
		return list;
	}

}
