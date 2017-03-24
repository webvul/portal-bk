package com.kii.beehive.portal.manager;


import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.kii.beehive.business.service.ExSpaceBookService;
import com.kii.beehive.portal.face.BeehiveFaceService;
import com.kii.beehive.portal.jdbc.entity.ExSpaceBook;
import com.kii.beehive.portal.jdbc.entity.GlobalThingInfo;
import com.kii.extension.sdk.entity.thingif.ThingStatus;

@Component
//@Transactional
public class ExSpaceBookManager {

	private Logger log = LoggerFactory.getLogger(ExSpaceBookManager.class);
	@Autowired
	private ExSpaceBookService spaceBookService;
	@Autowired
	private BeehiveFaceService beehiveFaceService;

	private Map<String, String> cameraThingStateMap;

	@PostConstruct
	public void init(){
//		cameraThingStateMap = new ConcurrentHashMap<>( spaceBookService.getCameraMap().size() * spaceBookService.getSitBeehiveUserIdMap().size() );
		cameraThingStateMap = new ConcurrentHashMap<>( spaceBookService.getSitBeehiveUserIdMap().size() );
	}
	/**
	 * 处理依图识别消息, 用于工位预订屏幕显示
	 *	相关逻辑:
	 * 如果两次给你的thingstate是相同的你就不用计算也不用告诉依图大屏幕
	   判断标准：camera_id, yitu_timestamp, userid 三个字段
	   如果给你的信息是gateway启动时的默认值，你也不用计算，默认值是："timestamp":0,"UserID":"0"
	 * @param thing
	 * @param status
	 * @param beehiveReceivedTimestamp
	 */
	@Async
	public void onFaceThingStateChange(GlobalThingInfo thing, ThingStatus status, Date beehiveReceivedTimestamp)  {
		//{"date":1489641038000,"UserID":"ac63a81b9cb36feaab9715cf14fd444c6df5bf49"
		// ,"timestamp":1489638032,"target":"th.f83120e36100-09e9-6e11-a52f-09a1df22"}
		Date pluginReceivedDate = new Date();

		String cameraId = spaceBookService.getCameraMap().get(thing.getId());
		if( StringUtils.isNoneBlank(cameraId) && status.getField("UserID") != null ){
			String userId = status.getField("UserID").toString();
			String yituTimestamp = status.getField("timestamp").toString();
			if(yituTimestamp.equals("0") && userId.equals("0")){//"timestamp":0,"UserID":"0"
				return;
			}
			String thingStateKey = cameraId + "`" + userId;
			String oldYituTimestamp = cameraThingStateMap.get(thingStateKey);
			if(oldYituTimestamp != null && oldYituTimestamp.equals(yituTimestamp)){//相同thingstate:camera_id, yitu_timestamp, userid
				return;
			}
			cameraThingStateMap.put(thingStateKey, yituTimestamp);
			//
			List<ExSpaceBook> bookedRuleByUser = spaceBookService.getBookedRuleByUser(userId);
			boolean isReserved = bookedRuleByUser.size() > 0 ? true : false;

			Map<String, Object> postData = new LinkedHashMap<>();
			postData.put("user_id", userId);
			postData.put("is_reserved", isReserved);
			postData.put("camera_id", cameraId);
			postData.put("yitu_timestamp", status.getField("timestamp"));
			postData.put("gateway_date", status.getField("date"));
			postData.put("beehive_received_timestamp", beehiveReceivedTimestamp.getTime());
			postData.put("plugin_received_timestamp", pluginReceivedDate.getTime());
			postData.put("plugin_call_yitu_timestamp", System.currentTimeMillis());
			log.info("yitu doGuestCheckin cameraThingStateMap.size(): " + cameraThingStateMap.size());
			log.info("yitu doGuestCheckin start: " + postData);
			beehiveFaceService.doGuestCheckin(postData);
			log.info("yitu doGuestCheckin end: " + postData);
		}

	}

}
