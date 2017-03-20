package com.kii.beehive.portal.manager;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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


	@Autowired
	private ExSpaceBookService spaceBookService;
	@Autowired
	private BeehiveFaceService beehiveFaceService;

	@Async
	public void onFaceThingStateChange(GlobalThingInfo thing, ThingStatus status, Date timestamp)  {
		String cameraId = spaceBookService.getCameraMap().get(thing.getId());
		if( StringUtils.isNoneBlank(cameraId) && status.getField("UserID") != null ){
			String userId = status.getField("UserID").toString();
			List<ExSpaceBook> bookedRuleByUser = spaceBookService.getBookedRuleByUser(userId);
			boolean isReserved = bookedRuleByUser.size() > 0 ? true : false;

			Map<String, Object> postData = new HashMap<>();
			postData.put("user_id", userId);
			postData.put("is_reserved", isReserved);
			postData.put("camera_id", cameraId);
			beehiveFaceService.doGuestCheckin(postData);
		}

	}

}
