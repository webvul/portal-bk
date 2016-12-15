package com.kii.beehive.portal.jdbc.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.IndustryTemplate;

@Repository
public class IndustryTemplateDao extends SpringBaseDao<IndustryTemplate> {

	
	public static final String TABLE_NAME = "industry_template";
	public static final String KEY = "id";

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}


	@Override
	public String getKey() {
		return KEY;
	}

	public List<IndustryTemplate> findByField(List<String> fieldNames, List<Object> values) {

		StringBuffer where = new StringBuffer();

		for(String fieldName : fieldNames) {
			where.append(" AND ").append(fieldName).append("=?");
		}

		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE 1=1" + where.toString() + " order by version desc";


		List<IndustryTemplate> rows = query(sql, values.toArray() );
		return rows;
	}
	public List<IndustryTemplate> getMaxVersionList() {

		String sql = "SELECT *, max(version) FROM " + TABLE_NAME + " WHERE 1=1"
				+ " group by schema_type, thing_type, `name`";

		List<IndustryTemplate> rows = query( sql );
		return rows;
	}


	private static final String sqlQueryByThingID="select t.* " +
			"from ${0} t inner join ${1} th on ( th.${2} = t.${3} and th.${4}=t.${5} ) " +
			"where  t.${6} = ? and th.${7} = ?  ";

	private static final String[] params=new String[]{IndustryTemplateDao.TABLE_NAME,GlobalThingSpringDao.TABLE_NAME,
			GlobalThingInfo.SCHEMA_VERSION,IndustryTemplate.VERSION,
			GlobalThingInfo.SCHEMA_NAME,IndustryTemplate.THING_TYPE,
			IndustryTemplate.SCHEMA_TYPE,GlobalThingInfo.FULL_KII_THING_ID};


	public IndustryTemplate getTemplateByThingID(Long thingID){

		params[params.length-1]=GlobalThingInfo.ID_GLOBAL_THING;

		String fullSql= StrTemplate.gener(sqlQueryByThingID,params);

		List<IndustryTemplate> list= super.query(fullSql,new Object[]{"industrytemplate",thingID});

		if(list.isEmpty()){
			return null;
		}else{
			return list.get(0);
		}
	}

	public IndustryTemplate getTemplateByVendorThingID(String thingID){

		params[params.length-1]=GlobalThingInfo.VANDOR_THING_ID;


		String fullSql= StrTemplate.gener(sqlQueryByThingID,params);

		return super.queryForObject(fullSql,new Object[]{"industrytemplate",thingID});

	}


	public IndustryTemplate getTemplateByKiiThingID(String thingID){

		params[params.length-1]=GlobalThingInfo.FULL_KII_THING_ID;

		String fullSql= StrTemplate.gener(sqlQueryByThingID,params);

		return super.queryForObject(fullSql,new Object[]{"industrytemplate",thingID});

	}

	private static final String sqlByName="select * from ${0} where ${1} = ? and ${2}=? ";

	public IndustryTemplate getTemplateByName(String name){

		String fullSql=StrTemplate.gener(sqlByName,IndustryTemplateDao.TABLE_NAME,IndustryTemplate.NAME,IndustryTemplate.SCHEMA_TYPE);

		List<IndustryTemplate>  list=super.query(fullSql,new Object[]{name,"industrytemplate"});

		if(list.size()==1){
			return list.get(0);
		}else{
			return null;
		}
	}
}
