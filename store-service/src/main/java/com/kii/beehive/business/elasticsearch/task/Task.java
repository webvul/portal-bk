package com.kii.beehive.business.elasticsearch.task;

import java.util.concurrent.Callable;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;

/**
 * Created by hdchen on 7/1/16.
 */
public abstract class Task<T extends ActionResponse> implements Callable<T> {
	private Client client;

	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public T call() throws Exception {
		try {
			return processRequest(client);
		} catch (Exception e) {
			handleFailure();
			throw e;
		}
	}

	protected abstract T processRequest(Client client);

	protected abstract void handleFailure();
}
