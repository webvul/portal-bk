package com.kii.beehive.business.service;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.kii.beehive.business.ruleengine.TriggerManager;
import com.kii.beehive.portal.auth.AuthInfoStore;
import com.kii.beehive.portal.jdbc.dao.ExCameraDoorDao;
import com.kii.beehive.portal.jdbc.dao.ExSitLockDao;
import com.kii.beehive.portal.jdbc.dao.ExSitSysBeehiveUserRelDao;
import com.kii.beehive.portal.jdbc.dao.ExSpaceBookDao;
import com.kii.beehive.portal.jdbc.dao.ExSpaceBookTriggerItemDao;
import com.kii.beehive.portal.jdbc.entity.ExCameraDoor;
import com.kii.beehive.portal.jdbc.entity.ExSitLock;
import com.kii.beehive.portal.jdbc.entity.ExSitSysBeehiveUserRel;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBookTriggerItem;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.ruleengine.store.trigger.condition.NotLogic;
import com.kii.extension.ruleengine.store.trigger.groups.SummarySource;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;
import com.kii.extension.sdk.exception.ObjectNotFoundException;

@Component

public class ExSpaceBookService {

	private static final Logger log = LoggerFactory.getLogger(ExSpaceBookService.class);

	public static String SIT_BOOKING_APP_CODE = "youjing";
	public static String SIT_BOOKING_USER_DEFAULT_PWD = "119!!)youjing!!)110";

	@Autowired
	private ExSpaceBookDao dao;

	@Autowired
	private ExSpaceBookTriggerItemDao itemDao;

	@Autowired
	private ExSitSysBeehiveUserRelDao beehiveUserRelDao;

	@Autowired
	private ExCameraDoorDao cameraDoorDao;
	@Autowired
	private ExSitLockDao sitLockDao;
	@Autowired
	private TriggerManager triggerManager;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ResourceLoader loader;

	private Map<String, String> sitBeehiveUserIdMap = new ConcurrentHashMap<>();
	private Map<String, List<ExCameraDoor>> cameraDoorMap = new HashMap<>();
	private Map<String, ExSitLock> spaceCodeSitLockMap = new HashMap<>();//key: spaceCode
	static String OPEN_DOOR_TRIGGER = null;
	static String UNLOCK_TRIGGER = null;
	static String UNLOCK_ERRORPWD_TRIGGER = null;
	@PostConstruct
	public void init() throws IOException {
		log.info("init ExSpaceBookService...");
		cameraDoorMap.clear();
		spaceCodeSitLockMap.clear();
		sitBeehiveUserIdMap.clear();
		//
		List<ExCameraDoor> cameraDoorDaoAll = cameraDoorDao.findAll();
		cameraDoorDaoAll.forEach(door -> {
			String key = door.getFace_thing_id() + "`" + door.getDoor_thing_id();
			List<ExCameraDoor> cameraDoorListByKey = cameraDoorMap.get(key);
			if(cameraDoorListByKey == null) {
				cameraDoorListByKey = new ArrayList<ExCameraDoor>();
				cameraDoorMap.put(key, cameraDoorListByKey);
			}
			cameraDoorListByKey.add(door);
		});
		//
		List<ExSitLock> sitLockListAll = sitLockDao.findAll();
		sitLockListAll.forEach(lock -> {
			spaceCodeSitLockMap.put(lock.getSpace_code(), lock);
		});
		//load trigger template json
		OPEN_DOOR_TRIGGER = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/portal/ex/template/open_door_trigger.json").getInputStream(), Charsets.UTF_8);
		UNLOCK_TRIGGER = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/portal/ex/template/unlock_trigger.json").getInputStream(), Charsets.UTF_8);
		UNLOCK_ERRORPWD_TRIGGER = StreamUtils.copyToString(loader.getResource("classpath:com/kii/beehive/portal/ex/template/unlock_errorpwd_trigger.json").getInputStream(), Charsets.UTF_8);
		//
		List<ExSitSysBeehiveUserRel> exSitSysBeehiveUserRels = beehiveUserRelDao.findAll();
		exSitSysBeehiveUserRels.forEach(rel -> {
			sitBeehiveUserIdMap.put(rel.getSit_sys_user_id(), rel.getBeehive_user_id());
		});

		log.info("cameraDoorMap : " + cameraDoorMap);
		log.info("spaceCodeSitLockMap : " + spaceCodeSitLockMap);
		log.info("sitBeehiveUserIdMap : " + sitBeehiveUserIdMap);
	}

	@Scheduled(cron = "0 0/1 * * * ?")
	@Transactional(propagation = Propagation.NEVER)
	public void doCreateTrigger()  {
		try {
			List<ExSpaceBook> spaceBookList = dao.getNeedCreateRule();
			log.info("Scheduled getNeedCreateRule : " + spaceBookList.size());
			doCreateTrigger(spaceBookList);
		} catch (Exception e) {
			log.error("Scheduled doCreateTrigger error", e);
		}
	}

	@Scheduled(cron = "0 0/5 * * * ?")
	@Transactional(propagation = Propagation.NEVER)
	public void doDeleteTrigger(){
		try {
			List<ExSpaceBook> spaceBookList = dao.getNeedDeleteRule();
			log.info("Scheduled getNeedDeleteRule : " + spaceBookList.size());
			doDeleteTrigger(spaceBookList);
		} catch (Exception e) {
			log.error("Scheduled doDeleteTrigger error", e);
		}


	}

	public void getUserIdList(List<String> userIds){

	}

	public String getUserIdList(String sitUserId){
		return sitBeehiveUserIdMap.get(sitUserId);
	}

	@Transactional
	public void insertBeehiveUserRel(ExSitSysBeehiveUserRel exSitSysBeehiveUserRel){
		beehiveUserRelDao.insert(exSitSysBeehiveUserRel);
		sitBeehiveUserIdMap.put(exSitSysBeehiveUserRel.getSit_sys_user_id(), exSitSysBeehiveUserRel.getBeehive_user_id());
	}


	@Transactional
	public void insertSpaceBook(List<ExSpaceBook> spaceBooks){
		for (int i = 0; i < spaceBooks.size(); i++) {
			ExSpaceBook book = spaceBooks.get(i);
			Map<String, Object> queryParam = new HashMap<>();
			queryParam.put("appCode", book.getAppCode());
			queryParam.put("campusCode", book.getCampusCode());
			queryParam.put("userId", book.getUserId());
			queryParam.put("spaceCode", book.getSpaceCode());
			queryParam.put("beginDate", book.getBeginDate());
			queryParam.put("endDate", book.getEndDate());
			List<ExSpaceBook> spaceBookTemp = dao.findByFields(queryParam);
			if(spaceBookTemp.size() > 0) {
				throw new IllegalArgumentException("Duplicate records index :" + (i+1) );
			}
			book.setAddedTrigger(false);
			book.setDeletedTrigger(false);
			book.setCreateTriggerError(false);
			this.insertSpaceBook(book);
		}

	}

	public void insertSpaceBook(ExSpaceBook spaceBook){

		spaceBook.setId(dao.insert(spaceBook));

	}

	@Transactional
	public List<ExSpaceBook> deleteSpaceBook(List<ExSpaceBook> spaceBooks){
		List<ExSpaceBook> spaceBookDeleted = new ArrayList<>();
		for (int i = 0; i < spaceBooks.size(); i++) {
			ExSpaceBook book = spaceBooks.get(i);
			Map<String, Object> queryParam = new HashMap<>();
			queryParam.put("appCode", book.getAppCode());
			queryParam.put("campusCode", book.getCampusCode());
			queryParam.put("userId", book.getUserId());
			if(book.getSpaceCode() != null) queryParam.put("spaceCode", book.getSpaceCode());
			if(book.getBeginDate() != null) queryParam.put("beginDate", book.getBeginDate());
			if(book.getEndDate() != null) queryParam.put("endDate", book.getEndDate());
			List<ExSpaceBook> spaceBookTemp = dao.findByFields(queryParam);
//			if(spaceBookTemp.size() == 0) {
//				throw new IllegalArgumentException("not find records index :" + (i+1) );
//			}
			spaceBookTemp.forEach(bookDel -> {
				dao.deleteByID(bookDel.getId());
			});
			spaceBookDeleted.addAll(spaceBookTemp);
		}


		return (spaceBookDeleted);
	}

	@Async
	public void asyncDeleteTrigger(List<ExSpaceBook> spaceBookDeleted) {
		doDeleteTrigger(spaceBookDeleted);
	}


//	@Transactional
	public void updatePassword(ExSpaceBook book, String newPwd){

		Map<String, Object> queryParam = new HashMap<>();
		queryParam.put("appCode", book.getAppCode());
		queryParam.put("campusCode", book.getCampusCode());
		queryParam.put("userId", book.getUserId());
//		queryParam.put("password", book.getPassword());
		List<ExSpaceBook> spaceBookTemp = dao.findByFields(queryParam);
		if(spaceBookTemp.size() == 0) {
			throw new IllegalArgumentException("not find records " );
		}
		spaceBookTemp.forEach(bookDel -> {

			dao.updateFieldByID("password", newPwd, bookDel.getId());

			//update trigger
			queryParam.clear();
			queryParam.put("exSpaceBookId", bookDel.getId());
			queryParam.put("addedTrigger", true);
			queryParam.put("deletedTrigger", false);
			queryParam.put("type", ExSpaceBookTriggerItem.ExSpaceBookTriggerItemType.unlock.name());
			List<ExSpaceBookTriggerItem> spaceBookForUpdate = itemDao.findByFields(queryParam);
			spaceBookForUpdate.forEach( item -> {
				TriggerRecord unlockTriggerRecord = triggerManager.getTriggerByID(item.getTriggerId());
				//condition
				if("unlock-error-pwd".equals(unlockTriggerRecord.getName())){
					AndLogic pwdEqAndLogic = (AndLogic)unlockTriggerRecord.getPredicate().getCondition();
					Equal pwdEq = (Equal) ( (NotLogic)pwdEqAndLogic.getClauses().get(0) ).getClause();
					pwdEq.setValue(newPwd);
					triggerManager.updateTrigger(unlockTriggerRecord);
				} else if("unlock".equals(unlockTriggerRecord.getName())){
					Equal pwdEq = (Equal)unlockTriggerRecord.getPredicate().getCondition();
					pwdEq.setValue(newPwd);
					triggerManager.updateTrigger(unlockTriggerRecord);
				}

			});

		});

	}


	@Transactional(propagation = Propagation.NEVER)
	private void doCreateTrigger(List<ExSpaceBook> spaceBookList) {
		spaceBookList.forEach(book -> {
			doCreateTrigger(book);
		});
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void doCreateTrigger(ExSpaceBook book)  {
		log.info("doCreateTrigger ExSpaceBook id:" + book.getId());


		//unlock trigger
		ExSitLock sitLock = spaceCodeSitLockMap.get(book.getSpaceCode());
		if(sitLock == null ) {
			log.error("sit booking can not find lock by spacecode error " + book.getId());
			dao.deleteByID(book.getId());
			return;
		}else {

			//unlock for error pwd
			SummaryTriggerRecord unlockErrorPwdTriggerTpl = getUnlockErrorPwdTriggerRecordTpl();
			//source
			((SummarySource)unlockErrorPwdTriggerTpl.getSummarySource().values().iterator().next()).getSource()
					.setThingList(Arrays.asList(sitLock.getLock_global_thing_id()));
			//condition
			AndLogic andLogic = (AndLogic)unlockErrorPwdTriggerTpl.getPredicate().getCondition();
			NotLogic notLogic = (NotLogic)andLogic.getClauses().get(0);
			Equal pwdEqForErrorPwd = (Equal)notLogic.getClause();
			pwdEqForErrorPwd.setValue(book.getPassword());

			//target
			CommandToThing command = (CommandToThing) unlockErrorPwdTriggerTpl.getTargets().get(0);
			command.setThingList(Arrays.asList(sitLock.getLock_global_thing_id().toString()));

			TriggerRecord unlockErrorPwdTrigger = null;
			try {
				unlockErrorPwdTrigger = triggerManager.createTrigger(unlockErrorPwdTriggerTpl);
			} catch (Exception e) {
				log.error("sit booking doCreateTrigger unlockTrigger error " + book.getId(), e);
				dao.updateFieldByID("createTriggerError", true, book.getId());
				return;
			}
			ExSpaceBookTriggerItem esbi = new ExSpaceBookTriggerItem();
			esbi.setExSpaceBookId(book.getId());
			esbi.setTriggerId(unlockErrorPwdTrigger.getTriggerID());
			esbi.setAddedTrigger(true);
			esbi.setDeletedTrigger(false);
			esbi.setType(ExSpaceBookTriggerItem.ExSpaceBookTriggerItemType.unlock);
			itemDao.insert(esbi);

			//unlock
			SummaryTriggerRecord unlockTriggerTpl = getUnlockTriggerRecordTpl();
			//source
			((SummarySource)unlockTriggerTpl.getSummarySource().values().iterator().next()).getSource()
					.setThingList(Arrays.asList(sitLock.getLock_global_thing_id()));
			//condition
			Equal pwdEq = (Equal)unlockTriggerTpl.getPredicate().getCondition();
			pwdEq.setValue(book.getPassword());
			//target
			CommandToThing commandUnlock = (CommandToThing) unlockTriggerTpl.getTargets().get(0);
			commandUnlock.setThingList(Arrays.asList(sitLock.getLock_global_thing_id().toString()));

			TriggerRecord unlockTrigger = null;
			try {
				unlockTrigger = triggerManager.createTrigger(unlockTriggerTpl);
			} catch (Exception e) {
				log.error("sit booking doCreateTrigger unlockTrigger error " + book.getId(), e);
				dao.updateFieldByID("createTriggerError", true, book.getId());
				return;
			}

			ExSpaceBookTriggerItem esbiErrorPwd = new ExSpaceBookTriggerItem();
			esbiErrorPwd.setExSpaceBookId(book.getId());
			esbiErrorPwd.setTriggerId(unlockTrigger.getTriggerID());
			esbiErrorPwd.setAddedTrigger(true);
			esbiErrorPwd.setDeletedTrigger(false);
			esbiErrorPwd.setType(ExSpaceBookTriggerItem.ExSpaceBookTriggerItemType.unlock);
			itemDao.insert(esbiErrorPwd);

		}


		//多个门 trigger
		cameraDoorMap.forEach( (key, doorList) -> {
			ExCameraDoor door = doorList.get(0);
			SummaryTriggerRecord openDoorTriggerTpl = getOpenDoorTriggerRecordTpl();
			openDoorTriggerTpl.setUserID(AuthInfoStore.getUserID());
			((SummarySource)openDoorTriggerTpl.getSummarySource().values().iterator().next()).getSource()
					.setThingList(Arrays.asList(door.getFace_thing_id()));
			AndLogic andLogic = (AndLogic)openDoorTriggerTpl.getPredicate().getCondition();
			Equal userIdEqual = (Equal)andLogic.getClauses().get(0);
			userIdEqual.setValue(book.getUserId());

//			OrLogic cameraOr = (OrLogic)andLogic.getClauses().get(1);
//			Equal cameraEq = (Equal)cameraOr.getClauses().get(0);
//			cameraOr.getClauses().clear();
//			//一个门对应多个摄像头
//			doorList.forEach( doorCamera -> {
//				cameraOr.getClauses().add(new Equal( cameraEq.getField(), doorCamera.getCamera_id() ) );
//			});

			//target
			CommandToThing command = (CommandToThing) openDoorTriggerTpl.getTargets().get(0);
			command.setThingList(Arrays.asList(door.getDoor_thing_id().toString()));

			TriggerRecord openDoorTrigger = null;
			try {
				openDoorTrigger = triggerManager.createTrigger(openDoorTriggerTpl);
			} catch (Exception e) {
				log.error("sit booking doCreateTrigger open door error " + book.getId(), e);
				dao.updateFieldByID("createTriggerError", true, book.getId());
				return;
			}
			ExSpaceBookTriggerItem esbi = new ExSpaceBookTriggerItem();
			esbi.setExSpaceBookId(book.getId());
			esbi.setTriggerId(openDoorTrigger.getTriggerID());
			esbi.setAddedTrigger(true);
			esbi.setDeletedTrigger(false);
			esbi.setType(ExSpaceBookTriggerItem.ExSpaceBookTriggerItemType.open_door);
			itemDao.insert(esbi);

		});

		dao.updateFieldByID("addedTrigger", true, book.getId());

	}

	private SummaryTriggerRecord getOpenDoorTriggerRecordTpl() {
		try {
			return mapper.readValue(OPEN_DOOR_TRIGGER, SummaryTriggerRecord.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private SummaryTriggerRecord getUnlockTriggerRecordTpl() {
		try {
			return mapper.readValue(UNLOCK_TRIGGER, SummaryTriggerRecord.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	private SummaryTriggerRecord getUnlockErrorPwdTriggerRecordTpl() {
		try {
			return mapper.readValue(UNLOCK_ERRORPWD_TRIGGER, SummaryTriggerRecord.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Transactional(propagation = Propagation.NEVER)
	private void doDeleteTrigger(List<ExSpaceBook> spaceBookList) {
		spaceBookList.forEach(book -> {
			doDeleteTrigger(book);
		});
	}

//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Transactional(propagation = Propagation.NEVER)
	private void doDeleteTrigger(ExSpaceBook book) {
		log.info("doDeleteTrigger ExSpaceBook id:" + book.getId());
		Map<String, Object> queryParam = new HashMap<>();
		queryParam.put("exSpaceBookId", book.getId());
		queryParam.put("addedTrigger", true);
		queryParam.put("deletedTrigger", false);
		List<ExSpaceBookTriggerItem> itemList = itemDao.findByFields(queryParam);
//		List<ExSpaceBookTriggerItem> itemList = itemDao.findBySingleField("ex_space_book_id", book.getId());
		for(ExSpaceBookTriggerItem item : itemList) {
			if(item.getAddedTrigger() && ! item.getDeletedTrigger()) {
				try {
					triggerManager.deleteTrigger(item.getTriggerId());
					noTransactionUpdateDeletedTriggerFlag(item);
				} catch (ObjectNotFoundException e) {
					log.error("sit booking deleteTrigger error, ObjectNotFoundException " , e);
				} catch (Exception e) {
					log.error("sit booking deleteTrigger error !!!" , e);
					throw new RuntimeException("sit booking deleteTrigger error !");
				}
			}
		}

		dao.updateFieldByID("deletedTrigger", true, book.getId());

	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void noTransactionUpdateDeletedTriggerFlag(ExSpaceBookTriggerItem item) {
		itemDao.updateFieldByID("deletedTrigger", true, item.getId());
	}

}
