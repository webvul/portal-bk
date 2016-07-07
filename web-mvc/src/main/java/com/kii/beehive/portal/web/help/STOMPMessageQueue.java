package com.kii.beehive.portal.web.help;

import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.stereotype.Component;

/**
 * Created by hdchen on 7/7/16.
 */
@Component
public class STOMPMessageQueue {
	private LinkedBlockingQueue<ThingStatusInfo> thingStatusQueue = new LinkedBlockingQueue();

	public void offerThingStatus(ThingStatusInfo status) {
		thingStatusQueue.offer(status);
	}

	public ThingStatusInfo pollThingStatus() throws InterruptedException {
		return thingStatusQueue.take();
	}
}
