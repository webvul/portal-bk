package com.kii.beehive.portal.web.socket;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hdchen on 7/18/16.
 */
public class ConcurrentWebSocketSessionHolder extends ConcurrentHashMap<String, WebSocketSessionDecorator> {
	private static ConcurrentWebSocketSessionHolder instance = new ConcurrentWebSocketSessionHolder();

	public static ConcurrentWebSocketSessionHolder getInstance() {
		return instance;
	}
}
