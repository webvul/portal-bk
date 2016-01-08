package com.kii.beehive.business;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.kii.beehive.portal.manager.SimpleThingTriggerManager;
import com.kii.beehive.business.service.ServiceExtensionDeployService;
import com.kii.beehive.business.service.ThingIFInAppService;
import com.kii.beehive.business.service.TriggerFireCallbackService;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingDao;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.TagThingRelationDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.beehive.portal.jdbc.entity.TagIndex;
import com.kii.beehive.portal.jdbc.entity.TagThingRelation;
import com.kii.beehive.portal.jdbc.entity.TagType;
import com.kii.beehive.portal.manager.ThingTagManager;
import com.kii.beehive.portal.store.entity.trigger.SimpleTriggerRecord;
import com.kii.beehive.portal.store.entity.trigger.TagSelector;
import com.kii.beehive.portal.store.entity.trigger.TargetAction;
import com.kii.beehive.portal.store.entity.trigger.TriggerTarget;
import com.kii.extension.sdk.entity.thingif.Action;
import com.kii.extension.sdk.entity.thingif.OnBoardingParam;
import com.kii.extension.sdk.entity.thingif.ServiceCode;
import com.kii.extension.sdk.entity.thingif.StatePredicate;
import com.kii.extension.sdk.entity.thingif.ThingCommand;
import com.kii.extension.sdk.entity.thingif.ThingStatus;
import com.kii.extension.sdk.entity.thingif.TriggerWhen;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;

@Transactional
public class TestTriggerCreate extends TestTemplate {

	private Logger log= LoggerFactory.getLogger(TestTriggerCreate.class);




//	@Autowired
//	private ExtensionCodeDao extensionDao;


	@Autowired
	private ServiceExtensionDeployService extensionService;

	@Autowired
	private ThingIFInAppService  thingIFService;


	@Autowired
	private ThingTagManager thingTagService;

	@Autowired
	private GlobalThingDao thingDao;

	@Autowired
	private TriggerFireCallbackService callbackService;


	@Autowired
	private TagIndexDao tagDao;

	@Autowired
	private TagThingRelationDao relationDao;

	private Long[] thingIDs={575l,576l,577l,578l,579l,580l,581l,582l,583l,584l};

	private String appName="b8ca23d0";

	private Long[] tags={311l,312l,313l,314l,315l};

	private String[] tagNames={"Custom-name0","Custom-name1","Custom-name2","Custom-name3","Custom-name4"};

//	@Test
//	@Commit
	public void createThings(){

		for(int i=0;i<10;i++) {

			OnBoardingParam param = new OnBoardingParam();
			String vendorThingID="vendorThing"+i;
			param.setVendorThingID(vendorThingID);
			param.setThingPassword("password");

			String thingID=thingIFService.onBoarding(param,appName).getThingID();


			GlobalThingInfo info=new GlobalThingInfo();
			String fullKiiThingID= ThingIDTools.joinFullKiiThingID(thingID,appName);

			info.setFullKiiThingID(fullKiiThingID);

			info.setVendorThingID(vendorThingID);
			info.setType("demo");

			long id=thingDao.saveOrUpdate(info);

			TagThingRelation relation=new TagThingRelation();
			relation.setThingID(id);
			relation.setTagID(tags[i%5]);

			relationDao.saveOrUpdate(relation);


			TagThingRelation  relation2=new TagThingRelation();
			relation2.setThingID(id);
			relation2.setTagID(tags[(i+1)%5]);

			relationDao.saveOrUpdate(relation2);

//			thingIDs.add(id);
		}
	}

//	@Commit
//	@Test
	public void init() {


		for (int i = 0; i < 5; i++) {

			TagIndex tag = new TagIndex();
			tag.setDisplayName("name" + i);
			tag.setTagType(TagType.Custom);
			tag.setFullTagName(TagType.Custom.getTagName("name" + i));

			long id = tagDao.saveOrUpdate(tag);

			log.info("tagID:"+id);
		}
	}


	@Autowired
	private SimpleThingTriggerManager simpleMang;


	@Test
	public void fireCallback(){

		String triggerID="1d08aa50-af80-11e5-962a-00163e02138f";
		callbackService.onSimpleArrive("",triggerID);



	}

	@Test
	public void sendState(){

//		extensionService.deployScriptToApp("b8ca23d0");


		long thingID=thingIDs[0];

		GlobalThingInfo thingInfo=thingDao.findByID(thingID);


		ThingStatus status=new ThingStatus();
		status.setField("foo",99);

		thingIFService.putStatus(thingInfo.getFullKiiThingID(),status);


	}

	@Test
	public void testTriggerCreate(){


		SimpleTriggerRecord record=new SimpleTriggerRecord();

		record.setThingID(thingIDs[0]);

		StatePredicate preidcate=new StatePredicate();
		Condition condition= ConditionBuilder.newCondition().great("foo",0).getConditionInstance();
		preidcate.setCondition(condition);
		preidcate.setTriggersWhen(TriggerWhen.CONDITION_TRUE);

		record.setPerdicate(preidcate);

		TriggerTarget target = getTagCmdTarget();

		record.addTarget(target);


		TriggerTarget target2=getServiceCodeTarget();
		record.addTarget(target2);

		simpleMang.createSimpleTrigger(record);

	}

	private TriggerTarget getServiceCodeTarget() {
		TriggerTarget target=new TriggerTarget();


		TargetAction action = new TargetAction();

		ServiceCode service=new ServiceCode();
		service.setEndpoint("hello");
		service.setTargetAppID("master");
		service.addParameter("name","world");

		action.setServiceCode(service);
		target.setCommand(action);
		return target;
	}

	private TriggerTarget getTagCmdTarget() {
		TriggerTarget target=new TriggerTarget();

		TagSelector selector=new TagSelector();

		selector.addTag(tagNames[2]);
		selector.addTag(tagNames[3]);
		selector.setAndExpress(true);

		TargetAction action = getTargetAction("powerOn","power",true);
		target.setCommand(action);
		return target;
	}

	private TargetAction getTargetAction(String name,String actName,Object value) {
		TargetAction action=new TargetAction();
		ThingCommand cmd=new ThingCommand();

		Action act=new Action();
		act.setField(actName,value);

		cmd.addAction(name,act);
		action.setCommand(cmd);
		return action;
	}


}
