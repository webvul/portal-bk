package com.kii.beehive.business.elasticsearch.task;

import java.util.concurrent.Callable;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import com.kii.beehive.business.elasticsearch.TransportClientProvider;

/**
 * Created by hdchen on 7/1/16.
 */
public abstract class Task<T extends ActionResponse> implements Callable<T> {
	private TransportClientProvider clientProvider;

	public void setClientProvider(TransportClientProvider clientProvider) {
		this.clientProvider = clientProvider;
	}

	@Override
	public T call() throws Exception {
		Client client = clientProvider.get();
		try {
			return processRequest(client);
		} catch (Exception e) {
			handleFailure();
			throw e;
		} finally {
			clientProvider.release(client);
		}
	}

	protected abstract T processRequest(Client client);

	protected abstract void handleFailure();
}
