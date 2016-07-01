package com.kii.beehive.business.elasticsearch;

import org.elasticsearch.client.Client;

/**
 * Created by hdchen on 7/1/16.
 */
public interface TransportClientProvider {
	Client get();

	void release(Client client);
}
