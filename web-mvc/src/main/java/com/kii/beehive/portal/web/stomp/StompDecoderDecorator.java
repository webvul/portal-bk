package com.kii.beehive.portal.web.stomp;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import com.kii.beehive.portal.web.security.AccessController;

/**
 * Created by hdchen on 7/29/16.
 */
@Component
@Scope(value = SCOPE_PROTOTYPE)
public class StompDecoderDecorator extends StompDecoder {
	@Value("${beehive.websocket.stomp.broker:/topic}")
	private String broker;

	@Autowired
	private AccessController accessController;

	@Override
	public List<Message<byte[]>> decode(ByteBuffer buffer) {
		return postProcessing(super.decode(buffer));
	}

	@Override
	public List<Message<byte[]>> decode(ByteBuffer buffer, MultiValueMap<String, String> partialMessageHeaders) {
		return postProcessing(super.decode(buffer, partialMessageHeaders));
	}

	private List<Message<byte[]>> postProcessing(List<Message<byte[]>> messages) {
		List<Message<byte[]>> list = new ArrayList();
		for (Message<byte[]> message : messages) {
			StompHeaderAccessor headerAccessor =
					MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
			if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand()) ||
					StompCommand.UNSUBSCRIBE.equals(headerAccessor.getCommand())) {
				if (!headerAccessor.getDestination().startsWith(broker)) {
					continue;
				}
				String[] partitions = headerAccessor.getDestination().substring(broker.length() + 1).split("/");
				if (2 != partitions.length) {
					continue;
				}

				if ("*".equals(partitions[1])) {
					if (accessController.canSubscribeAllThingStatus(partitions[0])) {
						list.add(message);
						return list;
					}
					continue;
				}

				String[] thingIds = partitions[1].split(",");
				for (String thingId : thingIds) {
					if (!accessController.canSubscribeThingStatus(partitions[0], thingId)) {
						continue;
					}

					HashMap header = new HashMap(message.getHeaders());
					header.put(StompHeaderAccessor.DESTINATION_HEADER,
							new StringBuilder(broker).append("/")
									.append(partitions[0])
									.append("/")
									.append(thingId)
									.toString());
					StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.create(headerAccessor.getCommand());
					stompHeaderAccessor.copyHeaders(header);
					stompHeaderAccessor.setLeaveMutable(true);
					list.add(MessageBuilder.createMessage(
							message.getPayload(), stompHeaderAccessor.getMessageHeaders()));
				}
			} else {
				list.add(message);
			}
		}
		return list;
	}
}
