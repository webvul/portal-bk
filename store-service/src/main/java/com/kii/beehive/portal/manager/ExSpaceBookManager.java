package com.kii.beehive.portal.manager;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	@Async
	public void onFaceThingStateChange(GlobalThingInfo thing, ThingStatus status, Date timestamp)  {
		//{"date":1489641038000,"UserID":"ac63a81b9cb36feaab9715cf14fd444c6df5bf49"
		// ,"timestamp":1489638032,"target":"th.f83120e36100-09e9-6e11-a52f-09a1df22"}
		String cameraId = spaceBookService.getCameraMap().get(thing.getId());
		if( StringUtils.isNoneBlank(cameraId) && status.getField("UserID") != null ){
			String userId = status.getField("UserID").toString();
			List<ExSpaceBook> bookedRuleByUser = spaceBookService.getBookedRuleByUser(userId);
			boolean isReserved = bookedRuleByUser.size() > 0 ? true : false;

			Map<String, Object> postData = new HashMap<>();
			postData.put("user_id", userId);
			postData.put("is_reserved", isReserved);
			postData.put("camera_id", cameraId);
			postData.put("yitu_timestamp", status.getField("timestamp"));
			postData.put("call_yitu_timestamp", System.currentTimeMillis()/1000L);
			log.info("yitu doGuestCheckin start: " + postData);
			beehiveFaceService.doGuestCheckin(postData);
			log.info("yitu doGuestCheckin end: " + postData);
		}

	}

}
