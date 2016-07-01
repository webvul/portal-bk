package com.kii.beehive.business.elasticsearch;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.kii.beehive.business.factory.ESTaskFactory;

/**
 * Created by hdchen on 6/30/16.
 */
@Component
public class TaskManager {

	private final ESTaskFactory taskFactory;

	private final ThreadPoolTaskExecutor indexThreadPoolTaskExecutor;

	private final ThreadPoolTaskExecutor searchThreadPoolTaskExecutor;

	@Autowired
	public TaskManager(@Value("${elasticsearch.indexTask.corePoolSize}") int indexTaskPoolSize,
					   @Value("${elasticsearch.indexTask.maxPoolSize}") int indexTaskMaxSize,
					   @Value("${elasticsearch.searchTask.corePoolSize}") int searchTaskPoolSize,
					   @Value("${elasticsearch.searchTask.maxPoolSize}") int searchTaskMaxSize,
					   @Value("${elasticsearch.taskManager.waitForTasksToCompleteOnShutdown}")
							   boolean waitForTasksToCompleteOnShutdown,
					   ESTaskFactory esTaskFactory) throws IOException {
		taskFactory = esTaskFactory;
		indexThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		indexThreadPoolTaskExecutor.setCorePoolSize(indexTaskPoolSize);
		indexThreadPoolTaskExecutor.setMaxPoolSize(indexTaskMaxSize);
		indexThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		indexThreadPoolTaskExecutor.initialize();
		searchThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		searchThreadPoolTaskExecutor.setCorePoolSize(searchTaskPoolSize);
		searchThreadPoolTaskExecutor.setMaxPoolSize(searchTaskMaxSize);
		searchThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
		searchThreadPoolTaskExecutor.initialize();
	}

	public void bulkUpload(String index, String type, List<JsonNode> data) {
		indexThreadPoolTaskExecutor.submit(taskFactory.getBulkUploadTask(index, type, data));
	}
}
