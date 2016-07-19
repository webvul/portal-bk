package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.kii.beehive.portal.common.utils.StrTemplate;
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

	private static final String sqlTmpQueryThing="select th.* from ${1} th " +
			" inner join ${2} rel on th.${0} = rel.${3} where rel.${4}${5} ";

	public List<GlobalThingInfo> getThingIDsByLocation(String location,boolean includeSubLevel,String type){

		List<Object> paramList=new ArrayList<>();


		String sqlTmp=sqlTmpQueryThing;
		String subQuery=" like  ? ";
		if(includeSubLevel){
			paramList.add(location+"%");
		}else{
			subQuery= " =  ? ";
			paramList.add(location);
		}

		if(StringUtils.isNoneBlank(type)){
			sqlTmp+=" and th.{6} = ? ";
			paramList.add(type);
		}

		String fullSql= StrTemplate.gener(sqlTmp,GlobalThingInfo.ID_GLOBAL_THING,GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,
				ThingLocationRelation.THING_ID,ThingLocationRelation.LOCATION,subQuery,GlobalThingInfo.THING_TYPE);


		return query(fullSql,paramList.toArray(new Object[0]));
	}

/*
Select * from globalThingInfo
Where thing_id  in
(  Select  relLoc.thing_id
   From thingLocationRel   loc
  innor join thingLocationRel  relLoc on loc.location  like relLoc.location
  Where  loc.thing_id =  ?  )  [ and type = ?   ][and loation like ‘locationPrefix%’ ]
 */

	private static final String sqlTmpRelThing="select  th.* from ${0} th where th.${1} in " +
			"(select locRel.${2} from ${3} loc  inner join  ${3}  locRel on  locRel.${4} = loc.${4} " +
			" where loc.${2} =  ? )  ";

	public List<GlobalThingInfo> getRelationThingsByThingLocatoin(long thingID,String type){


		List<Object> paramList=new ArrayList<>();

		paramList.add(thingID);

		String sqlTmp=sqlTmpRelThing;
		if(StringUtils.isNoneBlank(type)){
			sqlTmp+=" and th.${6} =  ? ";
			paramList.add(type);
		}


		String fullSql=StrTemplate.gener(sqlTmp,GlobalThingSpringDao.TABLE_NAME,GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.THING_ID,ThingLocationRelDao.TABLE_NAME,
				ThingLocationRelation.LOCATION,GlobalThingInfo.THING_TYPE);

		return super.query(fullSql,paramList.toArray(new Object[0]));

	}


	private static final String sqlTmpRelThingWithLoc="select  th.* from ${0} th where th.${1} in " +
			"(select locRel.${2} from ${3} loc  inner join  ${3}  locRel on  locRel.${4} = loc.${4} " +
			" where loc.${2} =  ? and locRel.${4}${5} )  ";

	public List<GlobalThingInfo> getRelationThingsByThingLocatoin(long thingID,String type,String location,boolean includeSubLevel ){

		if(StringUtils.isBlank(location)){
			throw new IllegalArgumentException("location cannot null in this function");
		}

		List<Object> paramList=new ArrayList<>();

		paramList.add(thingID);

		String sqlTmp=sqlTmpRelThing;

		String subQuery=" =  ?  ";
		if(includeSubLevel) {
			subQuery = " like   ? ";
			paramList.add(location+"%");
		}else{
			paramList.add(location);
		}

		if(StringUtils.isNoneBlank(type)){
			sqlTmp+=" and th.${6} =  ? ";
			paramList.add(type);
		}

		String fullSql=StrTemplate.gener(sqlTmp,GlobalThingSpringDao.TABLE_NAME,GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.THING_ID,ThingLocationRelDao.TABLE_NAME,
				ThingLocationRelation.LOCATION,subQuery,GlobalThingInfo.THING_TYPE);

		return super.query(fullSql,paramList.toArray(new Object[0]));

	}


	/*
	Select group_concat(th.venderThingID) , th.type ,  loc.location
from globaThingInfo  inner join thingLocationRel loc
on thing.thing_id = loc.thing_id
Where   thing.type=”...”  and  loc.location like “locationPrefix%”
Group by   thing.type, substring(loc.location ,？,？ )
	 */


	private static final String sqlTmpWithGroup="select group_concat(th.${0}) as thingids, ${1} as name from " +
			" ${1} th inner join  ${2} loc on th.${3} = loc.${4} " +
			" where th.is_deleted = false  ${5} " +
			" group by ${1}  ";
	public Map<String,ThingIDs> getIDsByTypeGroup(ThingLocQuery query,boolean groupByType){


		List<Object> paramList=new ArrayList<>();
		String subQuery=fillParamList(paramList,query);

		String subGroup="loc."+ThingLocationRelation.LOCATION;
		if(groupByType){
			subGroup="th."+GlobalThingInfo.THING_TYPE;
		}

		String fullSql=StrTemplate.gener(sqlTmpWithGroup,GlobalThingInfo.VANDOR_THING_ID,subGroup,GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,
				GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.LOCATION,subQuery);

		List<Map<String,Object>>  list=jdbcTemplate.queryForList(fullSql,paramList.toArray(new Object[0]));

		Map<String,ThingIDs> result=new HashMap<>();

		list.forEach( map->{

			String thingIDs= (String) map.get("thingids");
			String type= (String) map.get("name");


			result.put(type,new ThingIDs(thingIDs));

		});

		return result;

	}


	private static final String SqlTmpWithTwoGroup="select group_concat(th.${0} as thingids, th.${1} as type,loc.${2} as location  from " +
			" ${3} th inner join  ${4} loc on th.${5} = loc.${6} " +
			" where th.is_deleted = false  ${7} " +
			" group by th.${1},loc.${2}   ";
	public Map<String,Map<String,ThingIDs>> getIDsByLocationAndTypeGroup(ThingLocQuery query){

		List<Object> paramList=new ArrayList<>();
		String subQuery=fillParamList(paramList,query);

		String fullSql=StrTemplate.gener(sqlTmpWithGroup,
				GlobalThingInfo.VANDOR_THING_ID,GlobalThingInfo.THING_TYPE,ThingLocationRelation.LOCATION,
				GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,GlobalThingInfo.ID_GLOBAL_THING,
				ThingLocationRelation.THING_ID,subQuery);

		List<Map<String,Object>>  list=jdbcTemplate.queryForList(fullSql,paramList.toArray(new Object[0]));

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



	private String fillParamList(List<Object> paramList,ThingLocQuery  query){

		StringBuilder sb=new StringBuilder(" and 1=1 ");

		if(StringUtils.isNoneBlank(query.type)){
			sb.append("and th.${0} =  ? ");
			paramList.add(query.type);
		}
		if(StringUtils.isNoneBlank(query.location)){

			if(query.includeSub){

				sb.append(" and loc.${1}  like  ? ");
				paramList.add(query.location+"%");
			}else{

				sb.append(" and loc.${1}  =  ? ");
				paramList.add(query.location);

			}
		}

		String tmp= sb.toString();

		return StrTemplate.gener(tmp,GlobalThingInfo.THING_TYPE,ThingLocationRelation.LOCATION);

	}


	public static class  ThingIDs{

		private List<String> thingIDs=new ArrayList<>();

		public ThingIDs(String ids){

			String[] array=StringUtils.split(ids);

			thingIDs.addAll(Arrays.asList(array));
		}
	}

	public static class  ThingLocQuery{

		private String type;

		private String location;

		private boolean includeSub;


	}
}
