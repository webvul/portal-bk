//package com.kii.beehive.portal.jdbc.dao;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Repository;
//
//import com.kii.beehive.portal.entitys.AuthInfo;
//
//@Repository
//public class AuthInfoDao extends SpringBaseDao<AuthInfo> {
//
//    private Logger log= LoggerFactory.getLogger(AuthInfoDao.class);
//
//    public static final String TABLE_NAME = "auth_info";
//    public static final String KEY = AuthInfo.ID;
//
//
//    @Override
//    public String getTableName() {
//        return TABLE_NAME;
//    }
//
//    @Override
//    public String getKey() {
//        return KEY;
//    }
//
//
//
//    public void deleteByUserID(String userID) {
//        String sql = "DELETE FROM " + this.getTableName() + " WHERE " + AuthInfo.USER_ID + "=?";
//
//        jdbcTemplate.update(sql, userID);
//    }
//
//
//	public AuthInfo getAuthInfoByToken(String token){
//
//		String sql="select * from auth_info where token = ? ";
//
//		List<AuthInfo> infos=jdbcTemplate.query(sql,new Object[]{token},getRowMapper());
//
//		if(infos.size()==0){
//			return null;
//		}else if(infos.size()>1){
//			throw new IllegalArgumentException("the data error,more than one record with same token");
//		}else{
//
//			return infos.get(0);
//		}
//	}
//
//	public void deleteByToken(String token) {
//
//		String sql = "DELETE FROM " + this.getTableName() + " WHERE " + AuthInfo.TOKEN + "=?";
//
//		jdbcTemplate.update(sql, token);
//	}
//}
//
