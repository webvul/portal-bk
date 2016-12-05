package com.kii.beehive.portal.jdbc.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

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
	
	
	public List<UserNotice>  queryNoticeList(NoticeQuery query){
		
		
		BindClsRowMapper.SqlParam sqlParam=super.getSqlParam();
		
		sqlParam.addEqCondition("type",query.type);
		sqlParam.addEqCondition("readed",query.readed);
		
		sqlParam.addPageEnd(query.getNextPage());
		
		String fullSql=sqlParam.getFullSql();
		
		List<UserNotice>  list=super.jdbcTemplate.query(fullSql,sqlParam.getParamArray(),super.getRowMapper());
		
		return list;
	}
	
	public int readed(Long id){
		
		Map<String,Object> paramMap= Collections.singletonMap("readed",true);
		
		return super.updateEntityByID(paramMap,id);
	}
	

	public static class NoticeQuery{
		
		private UserNotice.MsgType type;
		
		private Boolean readed;
		
		private int nextPage;
		
	
		public void setType(UserNotice.MsgType type) {
			this.type = type;
		}
		

		
		public void setReaded(Boolean readed) {
			this.readed = readed;
		}
		
		public int getNextPage() {
			return nextPage;
		}
		
		public void setNextPage(int nextPage) {
			this.nextPage = nextPage;
		}
		
	
	}
	
	
	
}
