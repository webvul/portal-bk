package com.kii.beehive.portal.jdbc.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
			" inner join ${2} loc on th.${0} = loc.${3} where 1=1  ${4} ";

	public List<GlobalThingInfo> getThingIDsByLocation(ThingLocQuery query){

		List<Object> paramList=new ArrayList<>();

		String subQuery=query.fillSubQuery(paramList);

		String fullSql= StrTemplate.gener(sqlTmpQueryThing,
				GlobalThingInfo.ID_GLOBAL_THING,GlobalThingSpringDao.TABLE_NAME,ThingLocationRelDao.TABLE_NAME,
				ThingLocationRelation.THING_ID,subQuery);


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
			"(select loc.${2} from ${3} locSrc  inner join  ${3}  loc on  locSrc.${4} = loc.${4} " +
			" where locSrc.${2} =  ?  ${5} )  ${6}  ";

	public List<GlobalThingInfo> getRelationThingsByThingLocatoin(long thingID,ThingLocQuery query ){

		List<Object> paramList=new ArrayList<>();
		paramList.add(thingID);

		ThingLocQuery locQuery=new ThingLocQuery();
		BeanUtils.copyProperties(query,locQuery);
		locQuery.setType(null);

		String subQuery1=locQuery.fillSubQuery(paramList);

		ThingLocQuery typeQuery=new ThingLocQuery();
		typeQuery.setType(query.getType());

		String subQuery2=typeQuery.fillSubQuery(paramList);

		String fullSql=StrTemplate.gener(sqlTmpRelThing,
				GlobalThingSpringDao.TABLE_NAME,GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.THING_ID,
				ThingLocationRelDao.TABLE_NAME, ThingLocationRelation.LOCATION,subQuery1,
				subQuery2);

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
			" ${2} th inner join  ${3} loc on th.${4} = loc.${5} " +
			" where th.is_deleted = false  ${6} " +
			" group by ${1}  ";
	public Map<String,ThingIDs> getIDsByTypeGroup(ThingLocQuery query,boolean groupByType){


		List<Object> paramList=new ArrayList<>();
		String subQuery=query.fillSubQuery(paramList);

		String subGroup="loc."+ThingLocationRelation.LOCATION;
		if(groupByType){
			subGroup="th."+GlobalThingInfo.THING_TYPE;
		}

		String fullSql=StrTemplate.gener(sqlTmpWithGroup,
				GlobalThingInfo.VANDOR_THING_ID,subGroup,GlobalThingSpringDao.TABLE_NAME,
				ThingLocationRelDao.TABLE_NAME, GlobalThingInfo.ID_GLOBAL_THING,ThingLocationRelation.THING_ID,
				subQuery);

		List<Map<String,Object>>  list=jdbcTemplate.queryForList(fullSql,paramList.toArray(new Object[0]));

		Map<String,ThingIDs> result=new HashMap<>();

		list.forEach( map->{

			String thingIDs= (String) map.get("thingids");
			String type= (String) map.get("name");


			result.put(type,new ThingIDs(thingIDs));

		});

		return result;

	}


	private static final String SqlTmpWithTwoGroup="select group_concat(th.${0}) as thingids, th.${1} as type,loc.${2} as location  from " +
			" ${3} th inner join  ${4} loc on th.${5} = loc.${6} " +
			" where th.is_deleted = false  ${7} " +
			" group by th.${1},loc.${2}   ";
	public Map<String,Map<String,ThingIDs>> getIDsByLocationAndTypeGroup(ThingLocQuery query){

		List<Object> paramList=new ArrayList<>();
		String subQuery=query.fillSubQuery(paramList);

		String fullSql=StrTemplate.gener(SqlTmpWithTwoGroup,
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
	

	
	public static class  ThingIDs{

		private List<String> thingIDs=new ArrayList<>();

		public ThingIDs(String ids){

			String[] array=StringUtils.split(ids);

			thingIDs.addAll(Arrays.asList(array));
		}
	}

}
