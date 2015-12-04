package com.kii.beehive.portal.config;


import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;

@Configuration
@EnableCaching
public class CacheConfig {

	public static final String TTL_CACHE="ttl_cache";

	public static final String LONGLIVE_CACHE="long_live_cache";


	@Bean
	public Cache cacheTTL() {
		return new GuavaCache(TTL_CACHE, CacheBuilder.newBuilder()
				.expireAfterWrite(60, TimeUnit.MINUTES)
				.build());
	}


	@Bean
	public Cache cachePeresie() {
		return new GuavaCache(LONGLIVE_CACHE, CacheBuilder.newBuilder()
				.expireAfterWrite(60, TimeUnit.MINUTES)
				.build());
	}


}
