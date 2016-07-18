package com.kii.beehive.portal.web.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hdchen on 7/18/16.
 */
public class WebSocketSessionDecorator implements WebSocketSession {
	private Principal principal;

	private WebSocketSession delegate;

	public WebSocketSessionDecorator(WebSocketSession webSocketSession) {
		this.delegate = webSocketSession;
		this.principal = webSocketSession.getPrincipal();
	}

	@Override
	public String getId() {
		return delegate.getId();
	}

	@Override
	public URI getUri() {
		return delegate.getUri();
	}

	@Override
	public HttpHeaders getHandshakeHeaders() {
		return delegate.getHandshakeHeaders();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return delegate.getAttributes();
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return delegate.getLocalAddress();
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return delegate.getRemoteAddress();
	}

	@Override
	public String getAcceptedProtocol() {
		return delegate.getAcceptedProtocol();
	}

	@Override
	public void setTextMessageSizeLimit(int messageSizeLimit) {
		delegate.setTextMessageSizeLimit(messageSizeLimit);
	}

	@Override
	public int getTextMessageSizeLimit() {
		return delegate.getTextMessageSizeLimit();
	}

	@Override
	public void setBinaryMessageSizeLimit(int messageSizeLimit) {
		delegate.setBinaryMessageSizeLimit(messageSizeLimit);
	}

	@Override
	public int getBinaryMessageSizeLimit() {
		return delegate.getBinaryMessageSizeLimit();
	}

	@Override
	public List<WebSocketExtension> getExtensions() {
		return delegate.getExtensions();
	}

	@Override
	public void sendMessage(WebSocketMessage<?> message) throws IOException {
		delegate.sendMessage(message);
	}

	@Override
	public boolean isOpen() {
		return delegate.isOpen();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public void close(CloseStatus status) throws IOException {
		delegate.close(status);
	}
}
