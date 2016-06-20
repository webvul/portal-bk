package com.kii.beehive.portal.service.thing;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.store.entity.thing.Commands;
import com.kii.extension.sdk.annotation.AppBindParam;
import com.kii.extension.sdk.context.AppBindToolResolver;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.entity.ScopeType;
import com.kii.extension.sdk.query.Condition;
import com.kii.extension.sdk.query.ConditionBuilder;
import com.kii.extension.sdk.query.QueryParam;
import com.kii.extension.sdk.service.AbstractDataAccess;

@Component
public class CommandsDao extends AbstractDataAccess<Commands>{

	@Autowired
	private AppBindToolResolver resolver;

	private static final String CREATED_AT = "_created";

	/**
	 * query thing command list within a certain period
	 *
	 * @param thingID kii thing id
	 * @param startDateTime if null, query all commends sent before endDateTime
	 * @param endDateTime if null, query all commands sent after startDateTime
	 * @return
	 */
	public List<Commands> queryCommand(@AppBindParam String appID, String thingID, Long startDateTime, Long
			endDateTime) {

		ConditionBuilder conditionBuilder = ConditionBuilder.andCondition();
		if(startDateTime != null) {
			conditionBuilder.greatAndEq(CREATED_AT, startDateTime);
		}
		if(endDateTime != null) {
			conditionBuilder.lessAndEq(CREATED_AT, endDateTime);
		}
		Condition condition = conditionBuilder.getConditionInstance();
		QueryParam queryParam = QueryParam.generQueryParam(condition, CREATED_AT, true);

		List<Commands> result = null;

		BucketInfo bucketInfo = super.getBucketInfoInstance();
		// Important:
		// this part has to be synchronised, to avoid bucket scope name get mess up while same dao instance being
		// called in different threads (http requests)
		synchronized (bucketInfo) {

			// set scope type and scope name
			bucketInfo.setScopeType(ScopeType.Thing);
			bucketInfo.setScopeName(thingID);

			result = super.fullQuery(queryParam);

			// clear scope type and scope name
			bucketInfo.setScopeType(ScopeType.App);
			bucketInfo.setScopeName("");

		}

		return result;

	}

	@Override
	protected Class<Commands> getTypeCls() {
		return Commands.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("_commands");
	}
}
