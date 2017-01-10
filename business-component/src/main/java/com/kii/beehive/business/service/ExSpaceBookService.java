package com.kii.beehive.business.service;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
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
import com.kii.beehive.portal.jdbc.dao.ExSpaceBookDao;
import com.kii.beehive.portal.jdbc.dao.ExSpaceBookTriggerItemDao;
import com.kii.beehive.portal.jdbc.entity.ExCameraDoor;
import com.kii.beehive.portal.jdbc.entity.ExSitLock;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBookTriggerItem;
import com.kii.extension.ruleengine.store.trigger.TriggerRecord;
import com.kii.extension.ruleengine.store.trigger.condition.AndLogic;
import com.kii.extension.ruleengine.store.trigger.condition.Equal;
import com.kii.extension.ruleengine.store.trigger.groups.SummarySource;
import com.kii.extension.ruleengine.store.trigger.groups.SummaryTriggerRecord;
import com.kii.extension.ruleengine.store.trigger.task.CommandToThing;

@Component
@Transactional
public class ExSpaceBookService {

	private static final Logger log = LoggerFactory.getLogger(ExSpaceBookService.class);


	@Autowired
	private ExSpaceBookDao dao;

	@Autowired
	private ExSpaceBookTriggerItemDao itemDao;

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

	private Map<String, List<ExCameraDoor>> cameraDoorMap = new HashMap<>();
	private Map<String, ExSitLock> spaceCodeSitLockMap = new HashMap<>();//key: spaceCode
	static String OPEN_DOOR_TRIGGER = null;
	static String UNLOCK_TRIGGER = null;
	@PostConstruct
	public void init() throws IOException {

		cameraDoorMap.clear();
		spaceCodeSitLockMap.clear();
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



	}

	@Scheduled(cron = "0/5 * * * * ?")
	@Transactional(propagation = Propagation.NEVER)
	public void doCreateTrigger()  {
		List<ExSpaceBook> spaceBookList = dao.getNeedCreateRule();
		doCreateTrigger(spaceBookList);
	}

	@Scheduled(cron = "0/5 * * * * ?")
	@Transactional(propagation = Propagation.NEVER)
	public void doDeleteTrigger(){
		List<ExSpaceBook> spaceBookList = dao.getNeedDeleteRule();
		doDeleteTrigger(spaceBookList);


	}



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

	public void deleteSpaceBook(List<ExSpaceBook> spaceBooks){
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
			if(spaceBookTemp.size() == 0) {
				throw new IllegalArgumentException("not find records index :" + (i+1) );
			}
			spaceBookTemp.forEach(bookDel -> {
				dao.deleteByID(bookDel.getId());
			});
			spaceBookDeleted.addAll(spaceBookTemp);
		}


		doDeleteTrigger(spaceBookDeleted);
	}



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
				Equal pwdEq = (Equal)unlockTriggerRecord.getPredicate().getCondition();
				pwdEq.setValue(newPwd);
				triggerManager.updateTrigger(unlockTriggerRecord);

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
		log.info("doDeleteTrigger ExSpaceBook id:" + book.getId());
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
				log.error("doDeleteTrigger open door error " + book.getId(), e);
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

		//unlock trigger
		ExSitLock sitLock = spaceCodeSitLockMap.get(book.getSpaceCode());
		if(sitLock != null ) {
			SummaryTriggerRecord unlockTriggerTpl = getUnlockTriggerRecordTpl();
			//source
			((SummarySource)unlockTriggerTpl.getSummarySource().values().iterator().next()).getSource()
					.setThingList(Arrays.asList(sitLock.getLock_global_thing_id()));
			//condition
			Equal pwdEq = (Equal)unlockTriggerTpl.getPredicate().getCondition();
			pwdEq.setValue(book.getPassword());
			//target
			CommandToThing command = (CommandToThing) unlockTriggerTpl.getTargets().get(0);
			command.setThingList(Arrays.asList(sitLock.getLock_global_thing_id().toString()));

			TriggerRecord unlockTrigger = null;
			try {
				unlockTrigger = triggerManager.createTrigger(unlockTriggerTpl);
			} catch (Exception e) {
				log.error("doDeleteTrigger unlockTrigger error " + book.getId(), e);
				dao.updateFieldByID("createTriggerError", true, book.getId());
				return;
			}

			ExSpaceBookTriggerItem esbi = new ExSpaceBookTriggerItem();
			esbi.setExSpaceBookId(book.getId());
			esbi.setTriggerId(unlockTrigger.getTriggerID());
			esbi.setAddedTrigger(true);
			esbi.setDeletedTrigger(false);
			esbi.setType(ExSpaceBookTriggerItem.ExSpaceBookTriggerItemType.unlock);
			itemDao.insert(esbi);
			
		}

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


	@Transactional(propagation = Propagation.NEVER)
	private void doDeleteTrigger(List<ExSpaceBook> spaceBookList) {
		spaceBookList.forEach(book -> {
			doDeleteTrigger(book);
		});
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void doDeleteTrigger(ExSpaceBook book) {
		log.info("doDeleteTrigger ExSpaceBook id:" + book.getId());
		List<ExSpaceBookTriggerItem> itemList = itemDao.findBySingleField("ex_space_book_id", book.getId());

		itemList.forEach( item -> {
			if(item.getAddedTrigger() && ! item.getDeletedTrigger()) {
				triggerManager.deleteTrigger(item.getTriggerId());
				itemDao.updateFieldByID("deletedTrigger", true, item.getId());
			}
		});

		dao.updateFieldByID("deletedTrigger", true, book.getId());

	}

}