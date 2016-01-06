package com.kii.beehive.mock.web.data;

import org.springframework.stereotype.Component;

import com.kii.extension.sdk.annotation.BindAppByName;
import com.kii.extension.sdk.entity.BucketInfo;
import com.kii.extension.sdk.service.AbstractDataAccess;

@BindAppByName(appName="portal",appBindSource="propAppBindTool")
@Component
public class MockResultDao extends AbstractDataAccess<MockResult> {
	@Override
	protected Class<MockResult> getTypeCls() {
		return MockResult.class;
	}

	@Override
	protected BucketInfo getBucketInfo() {
		return new BucketInfo("mock_result");
	}
}
