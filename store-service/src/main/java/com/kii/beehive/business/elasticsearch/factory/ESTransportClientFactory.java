package com.kii.beehive.business.elasticsearch.factory;

import static java.lang.Runtime.getRuntime;

import java.io.IOException;
import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.threadpool.ThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.floragunn.searchguard.ssl.util.SSLConfigConstants;

/**
 * Created by hdchen on 6/30/16.
 */
@Configuration
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

	private final String[] threadPoolNames = new String[]{ThreadPool.Names.GENERIC, ThreadPool.Names.INDEX,
			ThreadPool.Names.SEARCH, ThreadPool.Names.SUGGEST, ThreadPool.Names.GET, ThreadPool.Names.BULK,
			ThreadPool.Names.PERCOLATE, ThreadPool.Names.SNAPSHOT, ThreadPool.Names.WARMER,
			ThreadPool.Names.REFRESH, ThreadPool.Names.LISTENER};

	@Bean
	public Client getTransportClient() throws IOException {
		int processors = Math.max(1, getRuntime().availableProcessors() >> 1);
		Settings.Builder builder = Settings.builder().put(PROP_CLUSTER_NAME, clusterName)
				.put("path.home", ".")
				.put(SSLConfigConstants.SEARCHGUARD_SSL_HTTP_ENABLE_OPENSSL_IF_AVAILABLE, true)
				.put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_ENABLE_OPENSSL_IF_AVAILABLE, true)
				.put("searchguard.ssl.transport.enabled", true)
				.put("searchguard.ssl.transport.keystore_filepath", clientKeystore.getFile().getAbsolutePath())
				.put("searchguard.ssl.transport.truststore_filepath", clientTruststore.getFile().getAbsolutePath())
				.put("searchguard.ssl.transport.enforce_hostname_verification", false)
				.put("searchguard.ssl.transport.resolve_hostname", false);
		for (String name : threadPoolNames) {
			builder.put("threadpool." + name + ".size", processors);
		}
		TransportClient client = TransportClient.builder().addPlugin(SearchGuardSSLPlugin.class)
				.settings(builder.build()).build();
		for (String address : transportAddress) {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(address), transportPort));
		}
		return client;
	}
}
