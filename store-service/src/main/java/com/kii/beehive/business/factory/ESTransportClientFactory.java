package com.kii.beehive.business.factory;

import java.io.IOException;
import java.net.InetAddress;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.floragunn.searchguard.ssl.util.SSLConfigConstants;

/**
 * Created by hdchen on 6/30/16.
 */
@Component
public class ESTransportClientFactory {

	private static final String PROP_CLUSTER_NAME = "cluster.name";

	@Value("${elasticsearch.cluster.name}")
	private String clusterName;

	@Value("${elasticsearch.transport.address}")
	private String[] transportAddress;

	@Value("${elasticsearch.transport.port}")
	private int transportPort;

	@Value("classpath:elasticsearch/client_keystore.jks")
	private Resource clientKeystore;

	@Value("classpath:elasticsearch/client_truststore.jks")
	private Resource clientTruststore;

	public Client getTransportClient() throws IOException {
		Settings settings = Settings.builder().put(PROP_CLUSTER_NAME, clusterName)
				.put("path.home", ".")
				.put(SSLConfigConstants.SEARCHGUARD_SSL_HTTP_ENABLE_OPENSSL_IF_AVAILABLE, true)
				.put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_ENABLE_OPENSSL_IF_AVAILABLE, true)
				.put("searchguard.ssl.transport.enabled", true)
				.put("searchguard.ssl.transport.keystore_filepath", clientKeystore.getFile().getAbsolutePath())
				.put("searchguard.ssl.transport.truststore_filepath", clientTruststore.getFile().getAbsolutePath())
				.put("searchguard.ssl.transport.enforce_hostname_verification", false)
				.put("searchguard.ssl.transport.resolve_hostname", false).build();
		TransportClient client = TransportClient.builder().addPlugin(SearchGuardSSLPlugin.class)
				.settings(settings).build();
		for (String address : transportAddress) {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(address), transportPort));
		}
		return client;
	}
}
