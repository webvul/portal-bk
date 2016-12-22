package com.kii.beehive.portal.jdbc.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.UserNotice;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

@Repository
public class UserNoticeDao extends SpringSimpleBaseDao<UserNotice> {
	
	
	private static final String TABLE_NAME = "user_notice";
	
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}
	
	@Override
	protected String getKey() {
		return UserNotice.NOTICE_ID;
	}
	
	
	
	public int queryByNumber(BindClsRowMapper.SqlParam sqlParam) {
		
		String fullSql=sqlParam.getFullSql();
		
		return super.jdbcTemplate.queryForObject(fullSql,sqlParam.getParamArray(),Integer.class);
		
	}
	
	public List<UserNotice>  queryNoticeList(BindClsRowMapper.SqlParam sqlParam){
		
		sqlParam.addDescOrder("createTime");
		String fullSql=sqlParam.getFullSql();
		
		List<UserNotice>  list=super.jdbcTemplate.query(fullSql,sqlParam.getParamArray(),super.getRowMapper());
		
		return list;
	}
	
	public int readed(Long id){
		
		Map<String,Object> paramMap= Collections.singletonMap("readed",true);
		
		return super.updateEntityByID(paramMap,id);
	}
	
	private static final String sqlByID= StrTemplate.gener("select * from ${0} where ${1} = ? and ${2} = ? ",TABLE_NAME,UserNotice.USER_ID,UserNotice.NOTICE_ID);
	
	public UserNotice getNoticeByID(Long noticeID, Long userID) {
	
		List<UserNotice>  list=super.jdbcTemplate.query(sqlByID,new Object[]{userID,noticeID},super.getRowMapper());
		
		if(list.isEmpty()){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	private static final String updateUnRead= StrTemplate.gener("update ${0} set ${1} = ?,${4}= ?  where ${2} = ? and ${3} = ? ",TABLE_NAME,UserNotice.READED,UserNotice.USER_ID,UserNotice.NOTICE_ID,UserNotice.READED_TIME);
	
	public int updateSign(Long noticeID, Long userID) {
		
		
		return super.jdbcTemplate.update(updateUnRead,true,new Date(),userID,noticeID);
	
	}
	
	
	private static final String updateAllUnRead= StrTemplate.gener("update ${0} set ${1} = ? ,${3}= ?  where ${2} = ?  ",TABLE_NAME,UserNotice.READED,UserNotice.USER_ID,UserNotice.READED_TIME);
	
	public void updateAllSign(Long userID) {
		
		super.jdbcTemplate.update(updateAllUnRead,true,new Date(),userID);
		
	}
	
	private static final String updateAllUnReadByIds= StrTemplate.gener("update ${0} set ${1} = :readed ,${4}= :date  where ${2} = :user_id and ${3} in (:ids) ",TABLE_NAME,UserNotice.READED,UserNotice.USER_ID,UserNotice.NOTICE_ID,UserNotice.READED_TIME);
	
	public int updateAllSign(Long userID,Collection<Long> ids) {
		
		Map<String,Object> params=new HashMap();
		params.put("user_id",userID);
		params.put("ids",ids);
		params.put("readed",true);
		params.put("date",new Date());
		
		return super.namedJdbcTemplate.update(updateAllUnReadByIds,params);
		
	}
	
	
}
