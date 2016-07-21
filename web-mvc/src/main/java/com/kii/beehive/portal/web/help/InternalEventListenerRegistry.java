package com.kii.beehive.portal.web.help;

import java.util.ArrayList;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.kii.beehive.portal.web.entity.StateUpload;

/**
 * Created by hdchen on 7/11/16.
 */
@Component
public class InternalEventListenerRegistry {
	public interface ExtensionCallbackEventListener {
		void onStateChange(String appId, StateUpload state);
	}

	private List<ExtensionCallbackEventListener> listeners = new ArrayList<ExtensionCallbackEventListener>();

	public void addEventListener(ExtensionCallbackEventListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeEventListener(ExtensionCallbackEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Async
	public void onStateChange(String appId, StateUpload state) {
		synchronized (listeners) {
			for (ExtensionCallbackEventListener listener : listeners) {
				listener.onStateChange(appId, state);
			}
		}
	}
}
