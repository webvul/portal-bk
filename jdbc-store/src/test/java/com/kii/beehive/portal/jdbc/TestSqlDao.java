package com.kii.beehive.portal.jdbc;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kii.beehive.portal.jdbc.dao.BeehiveUserJdbcDao;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.IndustryTemplateDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocQuery;
import com.kii.beehive.portal.jdbc.dao.ThingLocationDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.jdbc.dao.UserGroupDao;
import com.kii.beehive.portal.jdbc.dao.UserNoticeDao;
import com.kii.beehive.portal.jdbc.entity.NoticeActionType;
import com.kii.beehive.portal.jdbc.helper.BindClsRowMapper;

public class TestSqlDao extends TestTemplate {


	@Autowired
	private GlobalThingSpringDao  thingDao;

	@Autowired
	private UserGroupDao  groupDao;

	@Autowired
	private TagIndexDao tagDao;

	@Autowired
	private ThingLocationDao  thingLocDao;

	@Autowired
	private ThingLocationRelDao thingLocRelDao;


	@Autowired
	private BeehiveUserJdbcDao  userDao;

	@Autowired
	private IndustryTemplateDao  templateDao;
	
	@Autowired
	private UserNoticeDao noticeDao;

	List<Long> ids=new ArrayList<>();


	List<String> tags=new ArrayList<>();

	@PostConstruct
	public void init(){

		ids.add(100l);

		tags.add("a");
		tags.add("b");
	}


@Test
public void testTemplate(){

	templateDao.getTemplateByThingID(1l);
	templateDao.getTemplateByName("foo");
}

	@Test
	public void testThingLocal(){
		
		ThingLocQuery query=new ThingLocQuery();
		query.setType("type");
		query.setLocation("loc");

		query.setIncludeSub(true);


		ThingLocQuery queryFalse=new ThingLocQuery();
		queryFalse.setType("type");
		queryFalse.setLocation("loc");

		queryFalse.setIncludeSub(false);


		ThingLocQuery queryType=new ThingLocQuery();
		queryType.setType("type");


		ThingLocQuery queryLoc=new ThingLocQuery();
		queryLoc.setLocation("loc");
		queryLoc.setIncludeSub(false);


		ThingLocQuery[]  array=new ThingLocQuery[]{query,queryFalse,queryType,queryLoc};

		for(ThingLocQuery q:array){

			thingLocDao.getIDsByLocationAndTypeGroup(q,10l);

			thingLocDao.getIDsByTypeGroup(q,10l,true);

			thingLocDao.getIDsByTypeGroup(q,10l,false);

			thingLocDao.getThingsByLocation(q,10l);

			thingLocDao.getRelationThingsByThingLocatoin(100l,10l,q);

		}

		thingLocDao.getCountGroupInTypeByLoc("abc");

	}


	@Test
	public void testThingLocRel(){

		thingLocRelDao.addRelation(100l,tags);

		thingLocRelDao.removeRelation(100l,tags);

		thingLocRelDao.clearAllRelation(100l);

		thingLocRelDao.getRelation(100l);

	}


	@Test
	public void testUserDao(){

		userDao.findUserByTagID(100l);

		userDao.findUserByTagName("abc");

		userDao.findUserByTagRelThing(100l);

		userDao.findUserByTags(ids);

		userDao.findUserByThingID(100l);

		userDao.findUserIDByUserGroupID(100l);

		userDao.findUsersByGroups(ids);

		userDao.findAll();
		userDao.findByID(100l);
		userDao.findByIDs(ids);
		userDao.findBySingleField("user_id","b");


	}


	@Test
	public void testTagDao(){

		tagDao.findTagByGlobalThingID(100l);

		tagDao.findTagIdsByCreatorAndFullTagNames(100l,tags);

		tagDao.findTagByFullTagName("abc");

		tagDao.findUserLocations(100l,"abc");

		tagDao.findUserTagByUserID(100l);

		tagDao.findTag(10l,"abc","abc");

		tagDao.findLocations("local");

		tagDao.findTagByTagTypeAndName("a","b");

		tagDao.findTagIdsByIDsAndFullname(this.ids,this.tags);

		tagDao.findUserTagByTypeAndName(10l,"a","b");

		tagDao.findTagsByTagIdsAndLocations(this.ids,"abc");


		tagDao.findUserTagByUserID(100l);

		tagDao.findTagIdsByCreatorAndFullTagNames(100l,this.tags);


	}

	@Test
	public void testGroupDao(){

		groupDao.getAllGroupByRelTagRelThing(10l);

		groupDao.getAllGroupByRelThing(10l);

		groupDao.findUserGroup(100l,100l,"name");

		groupDao.findUserGroupByName("name");

		groupDao.checkIdList(ids);



	}


	@Test
	public void testThingDao(){


		thingDao.getFullThingDetailByThingIDs(ids);


		thingDao.findThingByGroupIDRelUserID(100l);

		thingDao.findThingByGroupIDRelUserIDWithThingID(100l,100l);

		thingDao.findThingByTag("test");

		thingDao.findThingByTagRelUserID(100l);

		thingDao.findThingByUserID(100l);

		thingDao.findThingByUserIDThingID(100l,100l);


		thingDao.findThingIdsByCreator(100l,ids);


		thingDao.findThingTypeByFullTagNames(tags);
		thingDao.findThingTypeBytagIDs("a,b,c");

		thingDao.findAllThingTypes();
		thingDao.findAllThingTypesWithThingCount();
		
		thingDao.getThingsByVendorThings(tags);
		
		thingDao.getAllThingAndRelationData();


	}
	
	@Test
	public void testNotice(){
		
		noticeDao.readed(100l);
		BindClsRowMapper.Pager page=new BindClsRowMapper.Pager();
		page.setSize(10);
		page.setStart(0);
		
		BindClsRowMapper.SqlParam query=noticeDao.getSqlParam();
		
		query.addEq("actionType", NoticeActionType.ThingMonitorType.false2true);
		query.addLike("title","abc");
		query.addEq("readed",false);
		Date date=new Date();
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK,1);
		
		query.addBetween("createTime",cal.getTime(),null);
		query.addBetween("readTime",cal.getTime(),null);
		
		query.addPager(page);
		
		noticeDao.queryNoticeList(query);
	}


}
