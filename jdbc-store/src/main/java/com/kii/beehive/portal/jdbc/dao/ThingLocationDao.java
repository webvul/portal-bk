package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.common.utils.StrTemplate;
import com.kii.beehive.portal.jdbc.entity.BusinessEntity;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.ThingLocationRelation;

@Repository
public class ThingLocationDao extends SpringBaseDao<GlobalThingInfo> {


	@Override
	protected String getTableName() {
		return GlobalThingSpringDao.TABLE_NAME;
	}

	@Override
	protected String getKey() {
		return GlobalThingInfo.ID_GLOBAL_THING;
	}



	/*
	Select  th.*
from globaThingInfo th  inner join thingLocationRel loc
on th.thing_id = loc.thing_id
Where    loc.location like “locationPrefix%” [and th.type = ?]
	 */

	private static final String sqlTmpQueryThing="select distinct th.* from ${1} th inner join ${6} rel  on rel.${7} = th.${0} " +
			" where th.${0} in " +
			" (select loc.${3} from  ${2} loc where th.${0} = loc.${3} ${4} )  " +
			"  and rel.${8} = :user_id " +
			"  ${5} ";

	public List<GlobalThingInfo> getThingsByLocation(ThingLocQuery query,long userID){

		Map<String,Object> paramMap=new HashMap<>();

		String subLocQuery=query.fillLocQuery(paramMap);

		String subQuery=query.fillTypeQuery(paramMap);

		String fullSql= StrTemplate.gener(sqlTmpQueryThing,
				GlobalThingInfo.ID_GLOBAL_THING,GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,
				ThingLocationRelation.THING_ID,subLocQuery,subQuery,
				GlobalThingInfo.VIEW_NAME,GlobalThingInfo.VIEW_THING_ID,GlobalThingInfo.VIEW_USER_ID);

		paramMap.put("user_id",userID);

		return queryByNamedParam(fullSql,paramMap);
	}



/*
Select * from globalThingInfo
Where thing_id  in
(  Select  relLoc.thing_id
   From thingLocationRel   loc
  innor join thingLocationRel  relLoc on loc.location  like relLoc.location
  Where  loc.thing_id =  ?  )  [ and type = ?   ][and loation like ‘locationPrefix%’ ]
 */


	private static final String sqlTmpRelThing="select distinct th.* from ${0} th " +
			" inner join ${7}  v on v.${8} = th.${1}  where th.${1} in " +
			"(select loc.${2} from ${3} locSrc  inner join  ${3}  loc on  locSrc.${4} = loc.${4} " +
			" where locSrc.${2} =  :thing_id  ${5} ) " +
			" and v.${9} = :user_id " +
			" ${6}  ";

	public List<GlobalThingInfo> getRelationThingsByThingLocatoin(long thingID,long userID,ThingLocQuery query ){

		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put("thing_id",thingID);
		paramMap.put("user_id",userID);

		String subQuery1=query.fillLocQuery(paramMap);


		String subQuery2=query.fillTypeQuery(paramMap);

		String fullSql=StrTemplate.gener(sqlTmpRelThing,
				GlobalThingSpringDao.TABLE_NAME,GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.THING_ID,
				ThingLocationRelDao.TABLE_NAME, ThingLocationRelation.LOCATION,subQuery1,
				subQuery2,GlobalThingInfo.VIEW_NAME,GlobalThingInfo.VIEW_THING_ID,
				GlobalThingInfo.VIEW_USER_ID);

		return super.queryByNamedParam(fullSql,paramMap);

	}



	/*
	Select group_concat(th.venderThingID) , th.type ,  loc.location
from globaThingInfo  inner join thingLocationRel loc
on thing.thing_id = loc.thing_id
Where   thing.type=”...”  and  loc.location like “locationPrefix%”
Group by   thing.type, substring(loc.location ,？,？ )
	 */


	private static final String sqlTmpWithGroup="select group_concat(DISTINCT th.${0}) as thingids,  ${1} as name from " +
			" ${2} th inner join  ${3} loc on th.${4} = loc.${5} " +
			"  inner join ${7} v on v.${8} = th.${0} " +
			" where th.is_deleted = false  and v.${9} = :user_id " +
			" ${6} " +
			" group by ${1}  ";
	public Map<String,ThingIDs> getIDsByTypeGroup(ThingLocQuery query,long userID,boolean groupByType){


		Map<String,Object> paramMap=new HashMap<>();
		String subQuery=query.fillSubQuery(paramMap);

		String subGroup="loc."+ThingLocationRelation.LOCATION;
		if(groupByType){
			subGroup="th."+GlobalThingInfo.THING_TYPE;
		}

		String fullSql=StrTemplate.gener(sqlTmpWithGroup,
				GlobalThingInfo.ID_GLOBAL_THING,subGroup,GlobalThingSpringDao.TABLE_NAME,
				ThingLocationRelDao.TABLE_NAME, GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.THING_ID,
				subQuery,GlobalThingInfo.VIEW_NAME,GlobalThingInfo.VIEW_THING_ID,
				GlobalThingInfo.VIEW_USER_ID);

		paramMap.put("user_id",userID);
		List<Map<String,Object>>  list=namedJdbcTemplate.queryForList(fullSql,paramMap);

		Map<String,ThingIDs> result=new HashMap<>();

		list.forEach( map->{

			String thingIDs= (String) map.get("thingids");
			String type= (String) map.get("name");


			result.put(type,new ThingIDs(thingIDs));

		});

		return result;

	}


	private static final String SqlTmpWithTwoGroup=
			"select group_concat(th.${0}) as thingids, th.${1} as type,loc.${2} as location  from " +
			" ${3} th inner join  ${4} loc on th.${5} = loc.${6} " +
			" inner join ${8} v on v.${9} = th.${0} " +
			" where th.is_deleted = false and v.${10} = :user_id  ${7} " +
			" group by th.${1},loc.${2}   ";
	public Map<String,Map<String,ThingIDs>> getIDsByLocationAndTypeGroup(ThingLocQuery query,long userID){

		Map<String,Object> paramMap=new HashMap<>();
		String subQuery=query.fillSubQuery(paramMap);

		String fullSql=StrTemplate.gener(SqlTmpWithTwoGroup,
				GlobalThingInfo.ID_GLOBAL_THING,GlobalThingInfo.THING_TYPE,ThingLocationRelation.LOCATION,
				GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,GlobalThingInfo.ID_GLOBAL_THING,
				ThingLocationRelation.THING_ID,subQuery,GlobalThingInfo.VIEW_NAME,
				GlobalThingInfo.VIEW_THING_ID,GlobalThingInfo.VIEW_USER_ID);

		paramMap.put("user_id",userID);

		List<Map<String,Object>>  list=namedJdbcTemplate.queryForList(fullSql,paramMap);

		Map<String,Map<String,ThingIDs>> result=new HashMap<>();

		list.forEach( map->{

			ThingIDs  thingIDs= new ThingIDs((String) map.get("thingids"));
			String type= (String) map.get("type");
			String location=(String)map.get("location");

			Map<String,ThingIDs> newMap=new HashMap<>();
			newMap.put(location,thingIDs);

			result.merge(type,newMap,(oldV,v)->{
				 oldV.putAll(v);
				 return oldV;
			});

		});

		return result;

	}


	/*
	select group_concat(th.id_global_thing),count(th.id_global_thing)
from global_thing th join rel_thing_location loc on (loc.thing_id = th.`id_global_thing`)
where th.`id_global_thing` in (select thing_id from view_thing_user_ownership v where v.user_id = 0)
and  loc.location like "0807%"
group by th.`thing_type`;
	 */
	private static final String typeGroupSql="select th.${8} as type ,count(th.${0}) as num " +
			"from ${1} th join ${2} loc on (loc.${3} = th.${0} ) " +
			"where th.${0} in (select ${4} from ${5} v where v.${6} = ?) " +
			"and  loc.${7} like  ? " +
			"and  th.${9} = false  " +
			"group by th.${8}  ";


	public List<TypeWithCount> getCountGroupInTypeByLoc(String loc){

		String fullSql=StrTemplate.gener(typeGroupSql,
				GlobalThingInfo.ID_GLOBAL_THING,GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,ThingLocationRelation.THING_ID,
				GlobalThingInfo.VIEW_THING_ID,GlobalThingInfo.VIEW_NAME,GlobalThingInfo.VIEW_USER_ID,ThingLocationRelation.LOCATION,
				GlobalThingInfo.THING_TYPE, BusinessEntity.IS_DELETED);

		List<Map<String,Object>>  result=this.jdbcTemplate.queryForList(fullSql,new Object[]{AuthInfoStore.getUserID(),loc+"%"});


		List<TypeWithCount>  list=new ArrayList<>();

		result.forEach(m->{
			list.add(new TypeWithCount(m));
		});

		return list;
	}

	public static class TypeWithCount{

		private String type;

		private int count;

		public TypeWithCount(){

		}

		public TypeWithCount(Map<String,Object> val){
			type= (String) val.get("type");

			count=((Number) val.get("num")).intValue();
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@JsonProperty("thingNumber")
		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}



	
	public static class  ThingIDs{

		private List<String> thingIDs=new ArrayList<>();

		public ThingIDs(String ids){

			String[] array=StringUtils.split(ids);

			thingIDs.addAll(Arrays.asList(array));
		}

		public ThingIDs(){

		}


		public List<String> getThingIDs() {
			return thingIDs;
		}

		public void setThingIDs(List<String> thingIDs) {
			this.thingIDs = thingIDs;
		}
	}

}
