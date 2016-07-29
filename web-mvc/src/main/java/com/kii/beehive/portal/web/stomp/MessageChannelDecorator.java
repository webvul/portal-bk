package com.kii.beehive.portal.web.stomp;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by hdchen on 7/29/16.
 */
public class SubscribableChannelDecorator implements MessageChannel {
	private final SubscribableChannel subscribableChannel;

	public SubscribableChannelDecorator(SubscribableChannel subscribableChannel) {
		this.subscribableChannel = subscribableChannel;
	}

	@Override
	public boolean subscribe(MessageHandler handler) {
		return this.subscribableChannel.subscribe(handler);
	}

	@Override
	public boolean unsubscribe(MessageHandler handler) {
		return this.subscribableChannel.unsubscribe(handler);
	}

	@Override
	public boolean send(Message<?> message) {
		return this.send(message, 0);
	}

	@Override
	public boolean send(Message<?> message, long timeout) {
		return this.subscribableChannel.send(message, timeout);
	}
}
