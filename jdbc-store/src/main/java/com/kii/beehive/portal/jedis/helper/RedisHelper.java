package com.kii.beehive.portal.jedis.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by carlos.yang on 16/6/3.
 */

@Component
public class RedisHelper {

	@Autowired
	private ShardedJedisPool shardedJedisPool;


	public synchronized Long lpush(String key, String... value) {
		if(key == null || value == null){
			return 0L;
		}
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = shardedJedisPool.getResource();
			return shardedJedis.lpush(key, value);
		} catch (Exception ex) {
//			ex.printStackTrace();
		} finally {
			shardedJedis.close();
		}
		return 0L;
	}


	public String brpop(int timeout, String key) {
		if(key == null){
			return null;
		}
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = shardedJedisPool.getResource();
			return shardedJedis.brpop(timeout, key).get(1);
		} catch (Exception ex) {
//			ex.printStackTrace();
		} finally {
			shardedJedis.close();
		}
		return null;
	}




}
