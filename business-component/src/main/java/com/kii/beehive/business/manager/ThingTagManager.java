package com.kii.beehive.business.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kii.beehive.business.entity.ESThingInfo;
import com.kii.beehive.business.service.IndustryTemplateService;
import com.kii.beehive.industrytemplate.PointDetail;
import com.kii.beehive.industrytemplate.ThingSchema;
import com.kii.beehive.portal.common.utils.ThingIDTools;
import com.kii.beehive.portal.jdbc.dao.GlobalThingSpringDao;
import com.kii.beehive.portal.jdbc.dao.PagerTag;
import com.kii.beehive.portal.jdbc.dao.TagIndexDao;
import com.kii.beehive.portal.jdbc.dao.ThingLocationRelDao;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.ruleengine.store.trigger.BusinessObjType;
import com.kii.extension.ruleengine.store.trigger.TagSelector;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
@Transactional
public class ThingTagManager {


	@Autowired
	private TagIndexDao tagDao;

	@Autowired
	private GlobalThingSpringDao globalThingDao;

	@Autowired
	private ThingLocationRelDao relDao;

	@Autowired
	private ObjectMapper mapper;
	
	public GlobalThingInfo findByID(Long id){
		return globalThingDao.findByID(id);
	}
	
	public GlobalThingInfo getThingByFullKiiThingID(String kiiApp,String thingID){
		return globalThingDao.getThingByFullKiiThingID(kiiApp,thingID);
	}

	@Autowired
	private IndustryTemplateService  templateService;

	public Set<String> getTagNamesByIDs(List<Long> tagIDs) {


		return tagDao.findByIDs(tagIDs).stream().map(tag -> tag.getFullTagName()).collect(Collectors.toSet());
	}

	public void updateKiicloudRelation(String vendorID, String fullKiiThingID) {
		GlobalThingInfo thing = globalThingDao.getThingByVendorThingID(vendorID);

		if (thing != null) {
			globalThingDao.updateKiiThingID(vendorID, fullKiiThingID);

		}
	}

	public  Map<String,Object> bindTemplate(Map<String,Object> input,String fullThingID){

		Map<String,Object> status=new HashMap<>(input);

		
		ThingSchema schema = templateService.getTemplateByKiiThingID(fullThingID);
		
		Map<String,PointDetail>  propMap=schema.getStatesSchema().getProperties();

		status.replaceAll((k,v)->{

			PointDetail detail=propMap.get(k);

			if(detail==null){
				return v;
			}

			Object val=detail.getValueMap().get(v);
			if(val==null){
				return v;
			}
			return val;

		});

		return status;


	}

	public Map<String,Object> updateState(ThingStatus status, String thingID, String appID) {

		String fullThingID = ThingIDTools.joinFullKiiThingID(appID, thingID);

		Map<String,Object>  values=status.getFields();
		
		Map<String,Object> valuesWithSchema=bindTemplate(values,fullThingID);
		
		globalThingDao.updateState(values, fullThingID);

		return values;

	}


	public GlobalThingInfo getThingByID(long globalThingID) {

		return globalThingDao.findByID(globalThingID);
	}

	public Set<GlobalThingInfo> getThingInfos(TagSelector source) {
		Set<GlobalThingInfo> things = new HashSet<>();

//		if (!source.getThingList().isEmpty()) {
//			things.addAll(globalThingDao.findByIDs(source.getThingList()));
//			return things;
//		}

		if (!source.getTagList().isEmpty()) {
			if (StringUtils.isEmpty(source.getType())) {

				if (source.isAndExpress()) {
					things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList()));
				} else {
					things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList()));
				}
			} else {

				if (source.isAndExpress()) {
					things.addAll(globalThingDao.queryThingByIntersectionTags(source.getTagList(), source.getType()));
				} else {
					things.addAll(globalThingDao.queryThingByUnionTags(source.getTagList(), source.getType()));
				}
			}
		} else {

//			throw new InvalidTriggerFormatException(" tag or thing List is null ");

		}

		return things;
	}


	public Set<String> getBusinessObjs(TagSelector source) {

		Set<GlobalThingInfo> thingList = getThingInfos(source);

		return thingList.stream().map(this::getInstance)
				.collect(Collectors.toSet());
	}

	
	private String getInstance(GlobalThingInfo thing){
		return BusinessObjType.Thing.getFullID(String.valueOf(thing.getId()),null);
	}

	public void iteratorAllThingsStatus(Consumer<GlobalThingInfo> consumer) {

		PagerTag pager = new PagerTag();
		pager.setPageSize(50);
		pager.setStartRow(0);

		List<GlobalThingInfo> list = globalThingDao.getAllThing(pager);


		while (pager.hasNext()) {

			list.forEach(consumer);
			list = globalThingDao.getAllThing(pager);
		}

		list.forEach(consumer);

	}
	
	public List<ESThingInfo> getAllThingFullInfo(){
		
		return globalThingDao.getAllThingAndRelationData().stream().map((m)-> new ESThingInfo(m.getThing(),m.getGeo(),m.getUserIDs(),m.getLocs())).collect(Collectors.toList());
	}
	
}
